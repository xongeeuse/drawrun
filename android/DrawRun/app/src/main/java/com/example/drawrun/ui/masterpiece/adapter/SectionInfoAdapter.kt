package com.example.drawrun.ui.masterpiece.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.drawrun.data.dto.response.masterpiece.SectionInfo
import com.example.drawrun.databinding.ItemSectionInfoBinding

class SectionInfoAdapter : RecyclerView.Adapter<SectionInfoAdapter.ViewHolder>() {
    private var sections: List<SectionInfo> = emptyList()

    class ViewHolder(private val binding: ItemSectionInfoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(sectionInfo: SectionInfo, position: Int) {
            binding.sectionNumber.text = (position + 1).toString()
            binding.sectionAddress.text = sectionInfo.address
            binding.sectionDistance.text = "0.00KM" // 거리 계산 로직 필요
            binding.sectionStatusButton.text = "달리기 시작"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSectionInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(sections[position], position)
    }

    override fun getItemCount() = sections.size

    fun updateSections(newSections: List<SectionInfo>) {
        sections = newSections
        notifyDataSetChanged()
    }
}
