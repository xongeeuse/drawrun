package com.example.drawrun.ui.masterpiece.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.drawrun.R
import com.example.drawrun.data.dto.response.masterpiece.Masterpiece
import com.example.drawrun.databinding.ItemMasterpieceBinding

class MasterpieceAdapter(
    private var items: List<Masterpiece>,
    private val onItemClick: (Int) -> Unit // masterpieceBoardId를 전달하도록 변경
) :
    RecyclerView.Adapter<MasterpieceAdapter.MasterpieceViewHolder>() {

    inner class MasterpieceViewHolder(private val binding: ItemMasterpieceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Masterpiece) {
            binding.titleText.text = "${ item.courseName }"
            binding.nicknameText.text = " \uD83D\uDC64 ${ item.nickname }"
            binding.distanceText.text = " \uD83C\uDFC3 ${item.distance} km"
            binding.guText.text = " \uD83D\uDCCD ${item.gu}"
            binding.restrictCountText.text = " \uD83D\uDC65 ${item.joinCount} / ${item.restrictCount}"

            // D-Day 조건 처리
            binding.dDayText.text = when {
                item.dday == 0 -> "D - day"
                item.dday > 0 -> "D - ${item.dday}"
                else -> "종료"
            }

            // 이미지 로딩 (Glide 사용)
            Glide.with(binding.root.context) // itemView.context 대신 binding.root.context 사용
                .load(item.pathImgUrl) // 이미지 URL
//                .placeholder(R.drawable.course_img_example) // 로딩 중 표시할 기본 이미지
                .error(R.drawable.course_img_example) // 로드 실패 시 표시할 이미지
                .into(binding.pathImage) // binding.pathImage로 참조

            // 아이템 클릭 리스너 수정
            binding.root.setOnClickListener {
                onItemClick(item.masterpieceBoardId) // masterpieceBoardId만 전달
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MasterpieceViewHolder {
        val binding =
            ItemMasterpieceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MasterpieceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MasterpieceViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<Masterpiece>) {
        items = newItems
        notifyDataSetChanged()
    }
}
