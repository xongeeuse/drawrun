package com.example.drawrun.ui.mypage.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.drawrun.R
import com.example.drawrun.data.repository.UserRepository
import com.example.drawrun.ui.mypage.adaptor.BookMarkAdapter
import com.example.drawrun.utils.RetrofitInstance
import com.example.drawrun.viewmodel.user.BookMarkViewModel
import com.example.drawrun.viewmodel.user.BookMarkViewModelFactory

class BookMarkFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyMessageTextView: TextView
    private lateinit var adapter: BookMarkAdapter

    private val bookMarkViewModel: BookMarkViewModel by viewModels {
        BookMarkViewModelFactory(UserRepository(RetrofitInstance.UserApi(requireContext())))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_bookmark, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.bookmarkRecyclerView)
        emptyMessageTextView = view.findViewById(R.id.emptyMessageTextView)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2) // ✅ 2열 그리드 적용

        adapter = BookMarkAdapter(emptyList())
        recyclerView.adapter = adapter

        // ✅ 북마크 데이터 가져오기
        bookMarkViewModel.fetchBookMarkInfo()

        // ✅ 북마크 데이터 변경 감지하여 UI 업데이트
        bookMarkViewModel.bookmarkList.observe(viewLifecycleOwner) { bookmarkList ->
            if (bookmarkList.isEmpty()) {
                emptyMessageTextView.visibility = View.VISIBLE  // ✅ 안내 문구 표시
                recyclerView.visibility = View.GONE             // ✅ RecyclerView 숨김
            } else {
                emptyMessageTextView.visibility = View.GONE     // ✅ 안내 문구 숨김
                recyclerView.visibility = View.VISIBLE         // ✅ RecyclerView 표시
                adapter.submitList(bookmarkList)               // ✅ 데이터 업데이트
            }
        }

        // ✅ 오류 메시지 처리
        bookMarkViewModel.errorState.observe(viewLifecycleOwner) { errorMessage ->
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        }
    }
}