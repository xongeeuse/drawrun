package com.example.drawrun.ui.search.fragment

import android.app.appsearch.SearchResult
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.drawrun.R
import com.example.drawrun.databinding.FragmentSearchResultBinding



class SearchResultFragment : Fragment() {
    private var _binding: FragmentSearchResultBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupToolbar()

        // 전달받은 검색어 표시
        arguments?.getString("query")?.let { query ->
            binding.toolbar.title = "'$query'에 대한 검색 결과"
        }
    }

    private fun setupRecyclerView() {
        binding.searchResultRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            // TODO: adapter 설정
        }
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

