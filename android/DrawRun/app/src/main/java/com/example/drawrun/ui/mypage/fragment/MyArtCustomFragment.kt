package com.example.drawrun.ui.mypage.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.drawrun.R
import com.example.drawrun.data.model.MyArtItem
import com.example.drawrun.ui.mypage.adaptor.MyArtAdapter

class MyArtCustomFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyMessageTextView: TextView
    private lateinit var adapter: MyArtAdapter
    private val artList = mutableListOf<MyArtItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_myartcustom, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.myArtRecyclerView)
        emptyMessageTextView = view.findViewById(R.id.emptyMessageTextView)

        adapter = MyArtAdapter(artList)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(context, 2)


        // 예시 데이터
        loadInitialData()

        // 데이터가 없으면 emptyMessage 표시
        updateUI()

    }

    private fun loadInitialData() {
        artList.clear()  // ✅ 기존 리스트 초기화

        // ✅ 같은 이미지를 7번 추가
        repeat(7) {
            Log.d("ImageTest", "Adding image: ${R.drawable.course_img_example}") // ✅ 로그 추가
            artList.add(MyArtItem(R.drawable.course_img_example, "코스 이미지 $it", "총 5km, 2시간대"))
        }
        adapter.notifyDataSetChanged()
    }

    private fun updateUI() {
        if (artList.isEmpty()) {
            emptyMessageTextView.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            emptyMessageTextView.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }
}