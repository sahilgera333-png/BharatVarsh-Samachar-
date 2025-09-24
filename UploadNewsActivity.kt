package com.bvsamachar

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bvsamachar.databinding.ActivityUploadNewsBinding
import com.bvsamachar.models.News
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class UploadNewsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadNewsBinding
    private var imageUri: Uri? = null

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.uploadImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 101)
        }

        binding.btnSubmit.setOnClickListener {
            val title = binding.newsTitle.text.toString().trim()
            val desc = binding.newsDesc.text.toString().trim()
            val videoUrl = binding.videoUrl.text.toString().trim()

            if (title.isEmpty() || desc.isEmpty()) {
                Toast.makeText(this, "Please enter title and description", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (imageUri != null) {
                val ref = storage.reference.child("news/${UUID.randomUUID()}")
                ref.putFile(imageUri!!)
                    .continueWithTask { task -> ref.downloadUrl }
                    .addOnSuccessListener { uri ->
                        saveNews(title, desc, uri.toString(), videoUrl)
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Upload failed: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            } else {
                saveNews(title, desc, null, videoUrl)
            }
        }
    }

    private fun saveNews(title: String, desc: String, imageUrl: String?, videoUrl: String?) {
        val newsId = db.collection("news").document().id
        val news = News(newsId, title, desc, imageUrl, videoUrl, System.currentTimeMillis())

        db.collection("news").document(newsId).set(news)
            .addOnSuccessListener {
                Toast.makeText(this, "News Uploaded", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save news: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == RESULT_OK) {
            imageUri = data?.data
            binding.uploadImage.setImageURI(imageUri)
        }
    }
}
