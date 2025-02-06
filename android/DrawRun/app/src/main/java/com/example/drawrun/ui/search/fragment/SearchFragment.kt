package com.example.drawrun.ui.search.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.drawrun.R
import com.example.drawrun.databinding.FragmentSearchBinding
import com.google.android.material.tabs.TabLayout

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private var isKeywordMode = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSearchType.setOnClickListener {
            isKeywordMode = !isKeywordMode
            updateSearchMode()
        }
    }

    private fun updateSearchMode() {
        with(binding) {
            if (isKeywordMode) {
                btnSearchType.text = "키워드"
                searchEditText.hint = "키워드로 검색하세요"
            } else {
                btnSearchType.text = "지역"
                searchEditText.hint = "지역으로 검색하세요"
            }
        }
    }

    private fun performSearch() {
        val query = binding.searchEditText.text.toString()
        // 검색 로직 구현
    }
}
