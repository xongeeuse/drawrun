package com.example.drawrun.ui.search.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.drawrun.R
import com.example.drawrun.data.repository.CourseRepository
import com.example.drawrun.data.repository.SearchRepository
import com.example.drawrun.databinding.FragmentRankingBinding
import com.example.drawrun.ui.search.adaptor.CourseAdapter
import com.example.drawrun.utils.RetrofitInstance
import com.example.drawrun.viewmodel.SearchViewModel
import com.example.drawrun.viewmodel.SearchViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class RankingFragment : Fragment() {
    private var _binding: FragmentRankingBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModels {
        SearchViewModelFactory(
            SearchRepository(RetrofitInstance.SearchApi(requireContext())),
            CourseRepository(RetrofitInstance.CourseApi(requireContext()))
        )
    }

    private lateinit var courseAdapter: CourseAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("RankRank", "onCreateView called")
        _binding = FragmentRankingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("RankRank", "onViewCreated called")

        // 3. RecyclerView 설정
        setupRecyclerView()

        // 4. 인기 코스 데이터 가져오기
        fetchTopCourses()

        // 5. 데이터 관찰 설정
        observeTopCourses()
    }

    private fun setupRecyclerView() {
        Log.d("RankRank", "setupRecyclerView called")
        courseAdapter = CourseAdapter(
            onBookmarkClick = { course ->
                viewModel.toggleBookmark(course)
            },
            showRanking = true
        )

        binding.rankingRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = courseAdapter
        }
    }

    private fun fetchTopCourses() {
        Log.d("RankRank", "fetchTopCourses called")
        viewModel.getTopCourses()
    }

    private fun observeTopCourses() {
        Log.d("RankRank", "observeTopCourses called")
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.topCourses.collectLatest { courses ->
                Log.d("RankRank", "Received ${courses.size} courses")
                val top10Courses = courses.take(10)
                courseAdapter.submitList(top10Courses)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
