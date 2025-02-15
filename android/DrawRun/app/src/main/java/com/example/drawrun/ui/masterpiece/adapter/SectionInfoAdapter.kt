package com.example.drawrun.ui.masterpiece.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import androidx.recyclerview.widget.RecyclerView
import com.example.drawrun.data.dto.response.masterpiece.SectionInfo
import com.example.drawrun.databinding.ItemSectionInfoBinding

class SectionInfoAdapter : RecyclerView.Adapter<SectionInfoAdapter.ViewHolder>() {
    private var sections: List<SectionInfo> = emptyList()
    private val distances = mutableMapOf<Int, Double>()
    private val colors = mutableMapOf<Int, String>()

    class ViewHolder(private val binding: ItemSectionInfoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(sectionInfo: SectionInfo, position: Int, distance: Double?, color: String?) {
            binding.sectionNumber.text = (position + 1).toString()
            binding.sectionAddress.text = sectionInfo.address
            binding.sectionDistance.text = distance?.let { String.format("%.2f km", it / 1000) } ?: "계산 중..."
            binding.sectionStatusButton.text = "달리기 시작"

            // 동그란 배경 크기 조정
            binding.sectionNumber.layoutParams.width = binding.sectionNumber.layoutParams.height
            binding.sectionNumber.requestLayout()

            // 색상 적용
            color?.let {
                val drawable = binding.sectionNumber.background as? GradientDrawable
                drawable?.setColor(Color.parseColor(it))
                binding.sectionStatusButton.setBackgroundColor(Color.parseColor(it))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSectionInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(sections[position], position, distances[position], colors[position])
    }

    override fun getItemCount() = sections.size

    fun updateSections(newSections: List<SectionInfo>) {
        sections = newSections
        distances.clear() // 새로운 섹션 목록으로 업데이트할 때 거리 정보 초기화
        colors.clear()
        notifyDataSetChanged()
    }

    fun updateColor(position: Int, color: String) {
        colors[position] = color
        notifyItemChanged(position)
    }

    fun updateDistance(position: Int, distance: Double) {
        distances[position] = distance
        notifyItemChanged(position)
    }
}
