package com.example.drawrun.ui.masterpiece.fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.drawrun.R
import com.example.drawrun.data.dto.response.masterpiece.Masterpiece
import com.example.drawrun.data.repository.MasterpieceRepository
import com.example.drawrun.databinding.FragmentMasterpieceSearchBinding
import com.example.drawrun.ui.masterpiece.adapter.MasterpieceAdapter
import com.example.drawrun.utils.RetrofitInstance
import com.example.drawrun.viewmodel.MasterpieceViewModel
import com.example.drawrun.viewmodel.MasterpieceViewModelFactory

class MasterpieceSearchFragment : Fragment() {

    private var _binding: FragmentMasterpieceSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: MasterpieceAdapter

    private val viewModel: MasterpieceViewModel by viewModels {
        MasterpieceViewModelFactory(MasterpieceRepository(RetrofitInstance.MasterpieceApi(requireContext())))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMasterpieceSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // RecyclerView 설정
        adapter = MasterpieceAdapter(emptyList()) { masterpieceBoardId ->
            navigateToDetail(masterpieceBoardId) // 아이템 클릭 시 호출
        }
        binding.searchResultRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.searchResultRecyclerView.adapter = adapter

        // 데이터 관찰 및 업데이트
        viewModel.masterpieceList.observe(viewLifecycleOwner) { masterpieces ->
            if (masterpieces.isEmpty()) {
                binding.emptyStateLayout.visibility = View.VISIBLE
                binding.searchResultRecyclerView.visibility = View.GONE
            } else {
                binding.emptyStateLayout.visibility = View.GONE
                binding.searchResultRecyclerView.visibility = View.VISIBLE
                adapter.updateData(masterpieces)
            }
        }

        // 검색 버튼 클릭 리스너 추가
        binding.searchLayout.searchBtn.setOnClickListener {
            performSearch()
        }

        // 키보드의 검색 버튼 클릭 리스너 추가
        binding.searchLayout.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                true
            } else {
                false
            }
        }

        /*// 검색 기능 구현
        binding.searchLayout.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.searchMasterpieces(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })*/

        // 필터링된 결과 관찰
        viewModel.filteredMasterpieceList.observe(viewLifecycleOwner) { masterpieces ->
            if (masterpieces.isEmpty()) {
                binding.emptyStateLayout.visibility = View.VISIBLE
                binding.searchResultRecyclerView.visibility = View.GONE
            } else {
                binding.emptyStateLayout.visibility = View.GONE
                binding.searchResultRecyclerView.visibility = View.VISIBLE
                adapter.updateData(masterpieces)
            }
        }

        // 데이터 가져오기 호출
        viewModel.getMasterpieceList()
    }

    private fun performSearch() {
        val query = binding.searchLayout.searchEditText.text.toString()
        viewModel.searchMasterpieces(query)

        // 키보드 숨기기
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }


    private fun navigateToDetail(masterpieceBoardId: Int) {
        val fragment = MasterpieceDetailFragment().apply {
            arguments = Bundle().apply {
                putInt("masterpieceBoardId", masterpieceBoardId)
            }
        }
        parentFragmentManager.beginTransaction()
            .replace(R.id.masterpiece_fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

}
