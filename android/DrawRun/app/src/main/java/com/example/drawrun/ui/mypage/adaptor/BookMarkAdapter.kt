package com.example.drawrun.ui.mypage.adaptor

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.drawrun.R
import com.example.drawrun.data.dto.response.user.BookMarkData

class BookMarkAdapter(private var bookmarkList: List<BookMarkData>) :
    RecyclerView.Adapter<BookMarkAdapter.BookMarkViewHolder>() {

    class BookMarkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val courseName: TextView = itemView.findViewById(R.id.courseNameTextView)
        val courseImage: ImageView = itemView.findViewById(R.id.courseImageView)
        val courseInfo: TextView = itemView.findViewById(R.id.addressTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookMarkViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_bookmark, parent, false)
        return BookMarkViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookMarkViewHolder, position: Int) {
        val bookmark = bookmarkList[position]

        Log.d("BookMarkAdapter", "코스 이름: ${bookmark.courseName}, 출발 위치: ${bookmark.address}")
        holder.courseName.text = "[ ${bookmark.courseName} ]"
        holder.courseInfo.text = "📍${bookmark.address}" // ✅ 출발위치 표시

        // ✅ 이미지 로딩 (Glide 사용)
        Glide.with(holder.itemView.context)
            .load(bookmark.pathImgUrl)
            .into(holder.courseImage)
    }

    override fun getItemCount(): Int = bookmarkList.size

    fun submitList(newList: List<BookMarkData>) {
        bookmarkList = newList
        notifyDataSetChanged()
    }
}