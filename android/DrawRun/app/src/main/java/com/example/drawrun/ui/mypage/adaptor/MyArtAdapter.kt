package com.example.drawrun.ui.mypage.adaptor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.drawrun.R
import com.example.drawrun.data.dto.response.user.ArtData

class MyArtAdapter(private var artList: List<ArtData>) :
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
            .load(item.pathImgUrl)  // ✅ Glide를 사용하여 JPG 불러오기
            .into(holder.imageView)

        holder.titleTextView.text = "[ ${item.name} ]"
        holder.infoTextView.text = "거리 : ${item.distance}km"
    }

    override fun getItemCount(): Int = artList.size

    // ✅ 새로운 리스트를 설정하는 함수
    fun submitList(newList: List<ArtData>) {
        artList = newList
        notifyDataSetChanged()
    }
}
