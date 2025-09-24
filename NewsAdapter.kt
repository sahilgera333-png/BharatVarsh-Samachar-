package com.bvsamachar.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bvsamachar.databinding.ItemNewsBinding
import com.bvsamachar.models.News

class NewsAdapter(
    private val list: List<News>,
    private val onItemClick: (News) -> Unit
) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    inner class NewsViewHolder(val binding: ItemNewsBinding) :
        RecyclerView.ViewHolder(binding.root) {
            init {
                binding.root.setOnClickListener {
                    onItemClick(list[adapterPosition])
                }
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = ItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val news = list[position]
        holder.binding.title.text = news.title
        holder.binding.description.text = news.description
        if (!news.imageUrl.isNullOrEmpty()) {
            Glide.with(holder.itemView.context).load(news.imageUrl).into(holder.binding.image)
        }
    }

    override fun getItemCount() = list.size
}
