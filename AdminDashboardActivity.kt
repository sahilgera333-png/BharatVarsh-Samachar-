package com.bvsamachar

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bvsamachar.adapters.NewsAdapter
import com.bvsamachar.databinding.ActivityAdminDashboardBinding
import com.bvsamachar.models.News
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminDashboardBinding
    private lateinit var adapter: NewsAdapter
    private val newsList = mutableListOf<News>()

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = NewsAdapter(newsList) { news ->
            Toast.makeText(this, "Click to edit/delete news later", Toast.LENGTH_SHORT).show()
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        binding.uploadNewsBtn.setOnClickListener {
            startActivity(Intent(this, UploadNewsActivity::class.java))
        }

        binding.logoutBtn.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        fetchNews()
    }

    private fun fetchNews() {
        db.collection("news").orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, _ ->
                newsList.clear()
                for (doc in snapshots!!) {
                    val news = doc.toObject(News::class.java)
                    newsList.add(news)
                }
                adapter.notifyDataSetChanged()
            }
    }
}
