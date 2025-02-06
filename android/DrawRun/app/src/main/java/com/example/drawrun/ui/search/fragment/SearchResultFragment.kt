package com.example.drawrun.ui.search.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.drawrun.R
import com.example.drawrun.databinding.FragmentSearchResultBinding
import com.example.drawrun.ui.search.adaptor.SearchResultAdapter

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SearchResultFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchResultFragment : Fragment() {
    private var _binding: FragmentSearchResultBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: SearchResultAdapter

    companion object {
        private const val ARG_QUERY = "query"

        fun newInstance(query: String) = SearchResultFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_QUERY, query)
            }
        }
    }

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
        performSearch()
    }

    private fun setupRecyclerView() {
        adapter = SearchResultAdapter()
        binding.searchResultRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@SearchResultFragment.adapter
        }
    }

    private fun performSearch() {
        val query = arguments?.getString(ARG_QUERY) ?: return
        // TODO: 검색 결과 로드 구현
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
