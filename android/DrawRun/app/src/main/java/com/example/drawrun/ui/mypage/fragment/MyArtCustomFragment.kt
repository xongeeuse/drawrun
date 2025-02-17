package com.example.drawrun.ui.mypage.fragment

import android.os.Bundle
import android.util.Log
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
import com.example.drawrun.data.dto.response.user.ArtData
import com.example.drawrun.data.model.MyArtItem
import com.example.drawrun.data.repository.UserRepository
import com.example.drawrun.ui.mypage.adaptor.MyArtAdapter
import com.example.drawrun.utils.RetrofitInstance
import com.example.drawrun.viewmodel.user.ArtCustomViewModel
import com.example.drawrun.viewmodel.user.ArtCustomViewModelFactory

class MyArtCustomFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyMessageTextView: TextView
    private lateinit var adapter: MyArtAdapter

    private val artCustomViewModel: ArtCustomViewModel by viewModels {
        ArtCustomViewModelFactory(UserRepository(RetrofitInstance.UserApi(requireContext())))
    }

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

        adapter = MyArtAdapter(emptyList())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(context, 2)

        // API 데이터 가져오기
        artCustomViewModel.fetchMyArtCustomInfo()

        // ✅ 데이터가 변경되면 RecyclerView 업데이트
        artCustomViewModel.artCollection.observe(viewLifecycleOwner) { artList ->
            updateUI(artList)
        }

        // ✅ 에러 메시지 처리
        artCustomViewModel.errorState.observe(viewLifecycleOwner) { errorMessage ->
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        }

    }

    // ✅ UI 업데이트 (데이터가 없을 경우 메시지 표시)
    private fun updateUI(artList: List<ArtData>) {
        if (artList.isEmpty()) {
            emptyMessageTextView.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            emptyMessageTextView.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            adapter.submitList(artList) // ✅ Adapter에 새로운 데이터 적용
        }
    }
}