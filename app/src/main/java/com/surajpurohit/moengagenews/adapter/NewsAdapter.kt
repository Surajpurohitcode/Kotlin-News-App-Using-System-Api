package com.surajpurohit.moengagenews.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.surajpurohit.moengagenews.R
import com.surajpurohit.moengagenews.model.News

class NewsAdapter(private val newsList: MutableList<News>, val context: Context) :
    RecyclerView.Adapter<NewsAdapter.ViewHolder>() {
    private var filteredList = newsList.toMutableList()
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val newsImage: ImageView = itemView.findViewById(R.id.news_image)
        val newsSource: TextView = itemView.findViewById(R.id.news_source)
        val newsDate: TextView = itemView.findViewById(R.id.news_date)
        val newsHeadline: TextView = itemView.findViewById(R.id.news_title)
        val newsDescription: TextView = itemView.findViewById(R.id.news_description)
        val newsAuthor: TextView = itemView.findViewById(R.id.author_name)
        val shareButton: ImageButton = itemView.findViewById(R.id.share_btn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsAdapter.ViewHolder, position: Int) {
        Glide.with(context).load(newsList.get(position).newsImage).into(holder.newsImage)
        holder.newsSource.setText(newsList.get(position).newsSource)
        holder.newsDate.setText(newsList.get(position).newsDate)
        holder.newsHeadline.setText(newsList.get(position).newsHeadline)
        holder.newsDescription.setText(newsList.get(position).newsDescription)
        holder.newsAuthor.setText(newsList.get(position).newsAuthor)

        //Open WebView when user click on news headline
        holder.newsHeadline.setOnClickListener{v ->
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(newsList.get(position).newsUrl))
            context.startActivity(intent)
        }

        //Share Button
        holder.shareButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain" // MIME type for sharing text content
                putExtra(Intent.EXTRA_TEXT,newsList.get(position).newsUrl) // Share URL as text content
            }
            val chooserIntent = Intent.createChooser(intent, "Share article")
            context.startActivity(chooserIntent)
        }

    }

    fun updateData(newData: List<News>) {
        newsList.clear()
        newsList.addAll(newData)
        filteredList = newData.toMutableList()
        notifyDataSetChanged() // Update adapter after data change
    }

    fun filter(query: String) {
        filteredList = newsList.filterTo(mutableListOf()) { item ->
            item.newsHeadline.contains(query, ignoreCase = true) || item.newsDescription.contains(query, ignoreCase = true)
        }
        notifyDataSetChanged() // Update adapter after filtering
    }

    override fun getItemCount(): Int {
        return newsList.size
    }

}