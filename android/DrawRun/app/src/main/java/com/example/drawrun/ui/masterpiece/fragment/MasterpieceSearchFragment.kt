package com.example.drawrun.ui.masterpiece.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        adapter = MasterpieceAdapter(emptyList()) { masterpiece ->
            navigateToDetail(masterpiece) // 아이템 클릭 시 호출
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

        // 데이터 가져오기 호출
        viewModel.getMasterpieceList()
    }

    private fun navigateToDetail(masterpiece: Masterpiece) {
        val fragment = MasterpieceDetailFragment().apply {
            arguments = Bundle().apply {
                putSerializable("masterpiece", masterpiece) // 데이터 전달
            }
        }
        parentFragmentManager.beginTransaction()
            .replace(R.id.masterpiece_fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}
