package com.bvsamachar

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bvsamachar.adapters.NewsAdapter
import com.bvsamachar.databinding.ActivityMainBinding
import com.bvsamachar.models.News
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: NewsAdapter
    private val newsList = mutableListOf<News>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = NewsAdapter(newsList) { news ->
            val intent = Intent(this, NewsDetailActivity::class.java)
            intent.putExtra("newsId", news.id)
            startActivity(intent)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        fetchNews()
    }

    private fun fetchNews() {
        FirebaseFirestore.getInstance().collection("news")
            .orderBy("timestamp", Query.Direction.DESCENDING)
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
