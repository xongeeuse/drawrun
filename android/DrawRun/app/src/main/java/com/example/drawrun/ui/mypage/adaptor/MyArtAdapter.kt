package com.example.drawrun.ui.mypage.adaptor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.drawrun.R
import com.example.drawrun.data.model.MyArtItem

class MyArtAdapter(private val artList: List<MyArtItem>) :
    RecyclerView.Adapter<MyArtAdapter.MyArtViewHolder>() {

    class MyArtViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.artImageView)
        val titleTextView: TextView = view.findViewById(R.id.artTitleTextView)
        val infoTextView: TextView = view.findViewById(R.id.artInfoTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyArtViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_myart, parent, false)
        return MyArtViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyArtViewHolder, position: Int) {
        val item = artList[position]

        Glide.with(holder.imageView.context)
            .load(item.imageRes)  // ✅ Glide를 사용하여 JPG 불러오기
            .into(holder.imageView)

        holder.titleTextView.text = item.title
        holder.infoTextView.text = item.info
    }

    override fun getItemCount(): Int = artList.size
}
