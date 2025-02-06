package com.example.drawrun.ui.search

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.drawrun.R
import com.example.drawrun.databinding.ActivitySearchBinding
import com.example.drawrun.ui.search.fragment.RankingFragment
import com.example.drawrun.ui.search.fragment.SearchResultFragment

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private var isKeywordMode = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupSearchUI()

        supportFragmentManager.beginTransaction()
            .replace(R.id.course_list_container, RankingFragment())
            .commit()
    }

    private fun setupSearchUI() {
        with(binding.searchLayout) {
            // 검색 타입 버튼 클릭 리스너
            btnSearchType.setOnClickListener {
                Log.d("searchtype", "검색 타입 클릭!")
                isKeywordMode = !isKeywordMode
                updateSearchMode()
            }

            // 검색 버튼 클릭 리스너
            searchBtn.setOnClickListener {
                performSearch()
            }

            // 키보드 검색 버튼 리스너
            searchEditText.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch()
                    true
                } else {
                    false
                }
            }
        }
    }

    private fun updateSearchMode() {
        with(binding.searchLayout) {
            btnSearchType.text = if (isKeywordMode) "키워드" else "지역"
            searchEditText.hint = if (isKeywordMode) "검색어를 입력하세요" else "지역을 입력하세요"

            // EditText 내용 초기화
            searchEditText.text.clear()

            // 키보드 숨기기
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
        }
    }

    private fun performSearch() {
        val query = binding.searchLayout.searchEditText.text.toString()
        if (query.isNotEmpty()) {
            val searchResultFragment = SearchResultFragment().apply {
                arguments = Bundle().apply {
                    putString("query", query)
                }
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.course_list_container, searchResultFragment)
                .addToBackStack(null)
                .commit()

            // 키보드 숨기기
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.searchLayout.searchEditText.windowToken, 0)
        }
    }
}
