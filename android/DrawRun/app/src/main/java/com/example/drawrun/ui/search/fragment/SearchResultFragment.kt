package com.example.drawrun.ui.search.fragment

import android.app.AlertDialog
import android.app.appsearch.SearchResult
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.drawrun.R
import com.example.drawrun.data.dto.response.course.CourseDetailsResponse
import com.example.drawrun.data.repository.CourseRepository
import com.example.drawrun.data.repository.SearchRepository
import com.example.drawrun.databinding.FragmentSearchResultBinding
import com.example.drawrun.dto.course.PathPoint
import com.example.drawrun.ui.navi.NaviActivity
import com.example.drawrun.ui.search.adaptor.CourseAdapter
import com.example.drawrun.utils.RetrofitInstance
import com.example.drawrun.viewmodel.CourseDetailsViewModel
import com.example.drawrun.viewmodel.CourseDetailsViewModelFactory
import com.example.drawrun.viewmodel.SearchState
import com.example.drawrun.viewmodel.SearchViewModel
import com.example.drawrun.viewmodel.SearchViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class SearchResultFragment : Fragment() {
    private var _binding: FragmentSearchResultBinding? = null
    private val binding get() = _binding!!

    private lateinit var courseAdapter: CourseAdapter
    private val searchRepository: SearchRepository by lazy {
        // 여기서 SearchRepository를 초기화합니다.
        // 여기서 왜 RetrofitInstance에 직접 접근해야 하는지..의문...뭐야?
        SearchRepository(RetrofitInstance.SearchApi(requireContext()))
    }

    private val courseRepository: CourseRepository by lazy {
        CourseRepository(RetrofitInstance.CourseApi(requireContext()))
    }

    private val viewModel: SearchViewModel by viewModels {
        SearchViewModelFactory(searchRepository, courseRepository)
    }

    private val courseDetailsViewModel: CourseDetailsViewModel by viewModels {
        CourseDetailsViewModelFactory(courseRepository)
    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("SearchSearch", "onCreateView() called")
        _binding = FragmentSearchResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("SearchSearch", "onViewCreated() called")

//        _binding = FragmentSearchResultBinding.bind(view)
        Log.d("SearchSearch", "ViewModel initialized: $viewModel")


        setupRecyclerView()
        setupToolbar()
        setupObservers()

        // 2. arguments?.getString("query") 호출 결과를 로그로 출력
        val query = arguments?.getString("query")
        Log.d("SearchSearch", "Received query: $query")

        // 전달받은 검색어 표시
        arguments?.getString("query")?.let { query ->
            binding.toolbar.title = "'$query'에 대한 검색 결과"
            Log.d("SearchSearch", "search 실행 전")
            val isKeywordMode = arguments?.getBoolean("isKeywordMode") ?: true
            viewModel.search(query, isKeywordMode) // 키워드 모드로 검색
            Log.d("SearchSearch", "search 실행 후")
        }
    }

    private fun setupRecyclerView() {
        courseAdapter = CourseAdapter(
            { course ->
            Log.d("SearchSearch", "Bookmark clicked for course: ${course.courseName}")
            viewModel.toggleBookmark(course)
        },
            { course ->
                Log.d("ClickClick", "Course clicked: ${course.courseName}")
                fetchCourseDetails(course.courseId)
            }
        )

        binding.searchResultRecyclerView.apply {
            layoutManager = LinearLayoutManager(context).also {
                Log.d("SearchSearch", "LayoutManager set: $it")

            }
            adapter = courseAdapter.also {
                Log.d("SearchSearch", "Adapter set: $it")
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

    private fun showCourseDetailsDialog(details: CourseDetailsResponse) {
        Log.d("내비", "Path Data: ${details.path}") // ✅ AlertDialog 실행 전에 로그 찍기!

        AlertDialog.Builder(requireContext())
            .setTitle("Course Details")
            .setMessage("PathId: ${details.userPathId}\nLocation: ${details.location}\nDistance: ${details.distance} km")
            .setPositiveButton("OK", null)
            .show()

    }



    private fun setupToolbar() {
        // 거리 필터 스피너 설정
        val distances = arrayOf("전체", "3km 이하", "5km 이하", "10km 이하", "15km 이하", "20km 이하")
        val spinnerAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            distances
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        binding.distanceSpinner.adapter = spinnerAdapter
        binding.distanceSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // TODO: 거리 필터 적용
                // viewModel.applyDistanceFilter(distances[position])
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.searchResults.collectLatest { courses ->
                Log.d("SearchSearch", "Courses collected: ${courses.size}")
                if (courses.isNullOrEmpty()) {
                    Log.d("SearchSearch", "Empty or null list received")
                    courseAdapter.submitList(emptyList()) // 빈 리스트 전달
                }
                try {
                    courseAdapter.submitList(courses)
                } catch (e: Exception) {
                    Log.e("SearchSearch", "Error submitting list", e)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.searchState.collectLatest { state ->
                Log.d("SearchSearch", "Search state: $state")
                when (state) {
                    is SearchState.Initial -> {
                        // 초기 상태 처리, 예를 들어 UI 초기화
                        Log.d("SearchSearch", "Initial state")
                        hideLoading()
                        binding.searchResultRecyclerView.visibility = View.GONE
                        binding.emptyStateLayout.visibility = View.GONE
                    }
                    is SearchState.Loading -> showLoading()
                    is SearchState.Success -> hideLoading()
                    is SearchState.Empty -> showEmptyState()
                    is SearchState.Error -> showError(state.message)
                    SearchState.Empty -> TODO()
                    is SearchState.Error -> TODO()
                    SearchState.Initial -> TODO()
                    SearchState.Loading -> TODO()
                    SearchState.Success -> TODO()
                }
            }
        }
    }


    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.searchResultRecyclerView.visibility = View.GONE
        binding.emptyStateLayout.visibility = View.GONE
    }

    private fun hideLoading() {
        binding.progressBar.visibility = View.GONE
        binding.searchResultRecyclerView.visibility = View.VISIBLE
        binding.emptyStateLayout.visibility = View.GONE
    }

    private fun showEmptyState() {
        binding.progressBar.visibility = View.GONE
        binding.searchResultRecyclerView.visibility = View.GONE
        binding.emptyStateLayout.visibility = View.VISIBLE
    }

    private fun showError(message: String) {
        binding.progressBar.visibility = View.GONE
        binding.searchResultRecyclerView.visibility = View.GONE
        binding.emptyStateLayout.visibility = View.VISIBLE
        binding.emptyStateText.text = "오류: $message"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

