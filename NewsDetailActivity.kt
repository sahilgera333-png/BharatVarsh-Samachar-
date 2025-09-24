package com.bvsamachar

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bvsamachar.databinding.ActivityNewsDetailBinding
import com.bvsamachar.models.News
import com.google.firebase.firestore.FirebaseFirestore
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

class NewsDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewsDetailBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val newsId = intent.getStringExtra("newsId") ?: return

        db.collection("news").document(newsId).get()
            .addOnSuccessListener { doc ->
                val news = doc.toObject(News::class.java)
                if (news != null) showNews(news)
            }
    }

    private fun showNews(news: News) {
        binding.title.text = news.title
        binding.description.text = news.description

        if (!news.imageUrl.isNullOrEmpty()) {
            Glide.with(this).load(news.imageUrl).into(binding.image)
        }

        if (!news.videoUrl.isNullOrEmpty()) {
            binding.video.visibility = YouTubePlayerView.VISIBLE
            lifecycle.addObserver(binding.video)
            binding.video.addYouTubePlayerListener(object :
                com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer) {
                    val videoId = news.videoUrl.substringAfter("v=")
                    youTubePlayer.cueVideo(videoId, 0f)
                }
            })
        }
    }
}
