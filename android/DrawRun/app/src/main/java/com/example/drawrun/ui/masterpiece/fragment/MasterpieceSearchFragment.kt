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
import com.google.android.material.tabs.TabLayout

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

        // TabLayout 설정
        setupTabLayout()

        // 초기 탭 선택
        binding.tabLayout.getTabAt(0)?.select()

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

        /*// 데이터 관찰 및 업데이트
        viewModel.masterpieceList.observe(viewLifecycleOwner) { masterpieces ->
            if (masterpieces.isEmpty()) {
                binding.emptyStateLayout.visibility = View.VISIBLE
                binding.searchResultRecyclerView.visibility = View.GONE
            } else {
                binding.emptyStateLayout.visibility = View.GONE
                binding.searchResultRecyclerView.visibility = View.VISIBLE
                adapter.updateData(masterpieces)
            }
        }*/

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

        // filteredMasterpieceList 관찰 코드를 masterpieceList 관찰 코드 대신 사용
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

        // 초기 필터링 설정 (그리는 중)
//        viewModel.filterMasterpieces(isInProgress = true)


        // 데이터 가져오기 호출
        viewModel.getMasterpieceList()
    }

    private fun performSearch() {
        val query = binding.searchLayout.searchEditText.text.toString()
        val isInProgress = binding.tabLayout.selectedTabPosition == 0
        viewModel.searchMasterpieces(query, isInProgress)

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

    private fun setupTabLayout() {
        // TabLayout에 탭 추가
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("그리는 중"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("완성작"))

        // 탭 선택 리스너 설정
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> viewModel.filterMasterpieces(isInProgress = true)
                    1 -> viewModel.filterMasterpieces(isInProgress = false)
                }
                // 탭 변경 시 현재 검색어로 다시 검색
                performSearch()
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        // 초기 탭 선택
        binding.tabLayout.getTabAt(0)?.select()
    }



}
