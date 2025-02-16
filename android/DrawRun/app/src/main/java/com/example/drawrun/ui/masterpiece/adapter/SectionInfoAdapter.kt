package com.example.drawrun.ui.masterpiece.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import androidx.recyclerview.widget.RecyclerView
import com.example.drawrun.data.dto.response.masterpiece.SectionInfo
import com.example.drawrun.databinding.ItemSectionInfoBinding

class SectionInfoAdapter(private val onJoinClick: (SectionInfo, Int, Int) -> Unit) : RecyclerView.Adapter<SectionInfoAdapter.ViewHolder>() {
    private var sections: List<SectionInfo> = emptyList()
    private val distances = mutableMapOf<Int, Double>()
    private val colors = mutableMapOf<Int, String>()
    private var masterpieceBoardId: Int = 0  // 추가된 필드

    // masterpieceBoardId를 설정하는 함수 추가
    fun setMasterpieceBoardId(id: Int) {
        masterpieceBoardId = id
    }

    fun updateNickname(position: Int, newNickname: String) {
        val updatedSection = sections[position].copy(nickname = newNickname)
        sections = sections.toMutableList().apply {
            set(position, updatedSection)
        }
        notifyItemChanged(position, PAYLOAD_NICKNAME)
    }


    companion object {
        private const val PAYLOAD_NICKNAME = "payload_nickname"
    }


    class ViewHolder(private val binding: ItemSectionInfoBinding, private val onJoinClick: (SectionInfo, Int, Int) -> Unit) : RecyclerView.ViewHolder(binding.root) {
        fun bind(sectionInfo: SectionInfo, position: Int, distance: Double?, color: String?, masterpieceBoardId: Int) {
            binding.sectionNumber.text = (position + 1).toString()
            binding.sectionAddress.text = sectionInfo.address
            binding.sectionDistance.text = distance?.let { String.format("%.2f km", it / 1000) } ?: "계산 중..."
            binding.sectionStatusButton.text = sectionInfo.nickname
            updateNickname(sectionInfo.nickname)

            // 동그란 배경 크기 조정
            binding.sectionNumber.layoutParams.width = binding.sectionNumber.layoutParams.height
            binding.sectionNumber.requestLayout()

            // 색상 적용
            color?.let {
                val drawable = binding.sectionNumber.background as? GradientDrawable
                drawable?.setColor(Color.parseColor(it))
                binding.sectionStatusButton.setBackgroundColor(Color.parseColor(it))
            }

            // 버튼 클릭 가능 여부 설정
            val isJoinable = sectionInfo.nickname == "달리기 시작"
            binding.sectionStatusButton.isEnabled = isJoinable
            binding.sectionStatusButton.alpha = if (isJoinable) 1f else 0.5f


            // 클릭 리스너 설정
            binding.sectionStatusButton.setOnClickListener {
                if (isJoinable) {
                    onJoinClick(sectionInfo, masterpieceBoardId, position)
                }
            }
        }
        fun updateNickname(nickname: String) {
            binding.sectionStatusButton.text = nickname
            val isJoinable = nickname == "달리기 시작"
            binding.sectionStatusButton.isEnabled = isJoinable
            binding.sectionStatusButton.alpha = if (isJoinable) 1f else 0.5f
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSectionInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onJoinClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(sections[position], position, distances[position], colors[position], masterpieceBoardId)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: List<Any>) {
        if (payloads.isNotEmpty()) {
            for (payload in payloads) {
                if (payload == PAYLOAD_NICKNAME) {
                    holder.updateNickname(sections[position].nickname)
                    return
                }
            }
        }
        holder.bind(sections[position], position, distances[position], colors[position], masterpieceBoardId)
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

    fun getDistance(position: Int): Double? {
        return distances[position]
    }
}
