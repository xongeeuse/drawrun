package com.example.drawrun.ui.mypage.adaptor


import android.icu.text.SimpleDateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.drawrun.R
import com.example.drawrun.data.dto.response.user.UserHistory
import java.util.Locale

class RunningHistoryAdapter(private val historyList: List<UserHistory>) :
    RecyclerView.Adapter<RunningHistoryAdapter.RunningHistoryViewHolder>() {

    class RunningHistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        val historyTitleTextView: TextView = view.findViewById(R.id.historyTitleTextView)
//        val historyDateTextView: TextView = view.findViewById(R.id.historyDateTextView)
        val dateTextView: TextView = view.findViewById(R.id.dateTextView)
        val pathImageView: ImageView = view.findViewById(R.id.pathImageView)
        val distanceTextView: TextView = view.findViewById(R.id.distanceTextView)
        val timeTextView: TextView = view.findViewById(R.id.timeTextView)
        val paceTextView: TextView = view.findViewById(R.id.paceTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunningHistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_running_history, parent, false)
        return RunningHistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: RunningHistoryViewHolder, position: Int) {
        val historyItem = historyList[position]

        // 🟢 날짜 변환: "최근 러닝 기록 YYYY-MM-DD" 형식
        val formattedDate = formatDate(historyItem.createDate)
        holder.dateTextView.text = formattedDate

        // 🟢 거리 변환: 소수점 둘째 자리까지 표시
        val formattedDistance = String.format(Locale.US, "%.2f", historyItem.distance)
        holder.distanceTextView.text = "거리: ${formattedDistance} km"

        // 🟢 시간 변환: "X분 Y초" 형식
        val formattedTime = formatTime(historyItem.time)
        holder.timeTextView.text = "시간: ${formattedTime}"

        holder.paceTextView.text = "페이스: ${historyItem.pace} 초/km"

        Glide.with(holder.itemView.context)
            .load(historyItem.pathImgUrl)
            .placeholder(R.drawable.ic_default_profile)
            .into(holder.pathImageView)

        Log.d("RunningHistoryAdapter", "날짜 데이터: ${historyItem.createDate}")

    }

    override fun getItemCount() = historyList.size


    // 🟢 날짜 변환 함수: "YYYY년 MM월 DD일" 형식
    private fun formatDate(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            date?.let { outputFormat.format(it) } ?: dateString

        } catch (e: Exception) {
            dateString // 변환 실패 시 원래 문자열 반환
        }
    }

    // 🟢 시간 변환 함수: "X분 Y초" 형식
    private fun formatTime(seconds: Int): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return "${minutes}분 ${remainingSeconds}초"
    }
}
