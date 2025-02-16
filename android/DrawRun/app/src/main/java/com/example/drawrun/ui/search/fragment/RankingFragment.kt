package com.example.drawrun.ui.search.fragment

import android.content.Intent
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
import com.example.drawrun.dto.course.PathPoint
import com.example.drawrun.ui.navi.NaviActivity
import com.example.drawrun.ui.search.adaptor.CourseAdapter
import com.example.drawrun.utils.RetrofitInstance
import com.example.drawrun.viewmodel.CourseDetailsViewModel
import com.example.drawrun.viewmodel.CourseDetailsViewModelFactory
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

    private val courseRepository: CourseRepository by lazy {
        CourseRepository(RetrofitInstance.CourseApi(requireContext()))
    }

    private val courseDetailsViewModel: CourseDetailsViewModel by viewModels {
        CourseDetailsViewModelFactory(courseRepository)
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
            onCourseClick = { course ->
                Log.d("ClickClick", "Course clicked: ${course.courseName}")
                fetchCourseDetails(course.courseId)
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

    private fun fetchCourseDetails(courseId: Int) {
        Log.d("CourseDetailsViewModel", "Fetching details for courseId: $courseId") // ✅ 추가
        courseDetailsViewModel.courseDetails.removeObservers(viewLifecycleOwner)
        courseDetailsViewModel.fetchCourseDetails(courseId) // ✅ ViewModel에 요청

        courseDetailsViewModel.courseDetails.observe(viewLifecycleOwner) { result ->
            result.onSuccess { details ->
                Log.d("SearchResultFragment", "Loaded Course Details: $details")
                // showCourseDetailsDialog(details) // ✅ UI 업데이트

                // ✅ `LatLngData` -> `PathPoint` 변환
                val pathPoints = details.path.map { PathPoint(it.latitude, it.longitude) }

                // ✅ `NaviActivity`로 이동하도록 추가!
                val intent = Intent(requireContext(), NaviActivity::class.java).apply {
                    putParcelableArrayListExtra("path", ArrayList(pathPoints)) // ✅ path 데이터를 전달
                    putExtra("startLocation", details.location) // ✅ 위치 정보 전달
                    putExtra("distance", details.distance) // ✅ 거리 정보 전달
                }
                startActivity(intent) // ✅ `NaviActivity` 실행!
                requireActivity().overridePendingTransition(0, 0) // ✅ 애니메이션 제거


            }.onFailure { e ->
                Log.e("SearchResultFragment", "Error loading course details", e)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
