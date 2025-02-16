package com.example.drawrun.ui.masterpiece

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.drawrun.R
import com.example.drawrun.databinding.ActivityMasterpieceBinding
import com.example.drawrun.ui.common.BaseActivity
import com.example.drawrun.ui.masterpiece.fragment.MasterpieceDetailFragment
import com.example.drawrun.ui.masterpiece.fragment.MasterpieceSearchFragment

class MasterpieceActivity : BaseActivity() {
    override fun getLayoutId(): Int = R.layout.activity_masterpiece  // ✅ 레이아웃 리소스 지정
    private lateinit var binding: ActivityMasterpieceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMasterpieceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val masterpieceBoardId = intent.getIntExtra("masterpieceBoardId", -1)
        if (masterpieceBoardId != -1) {
            navigateToMasterpieceDetail(masterpieceBoardId)
        } else {
            supportFragmentManager.beginTransaction()
                .replace(R.id.masterpiece_fragment_container, MasterpieceSearchFragment())
                .commit()
        }
    }

    fun navigateToMasterpieceDetail(masterpieceBoardId: Int) {
        val fragment = MasterpieceDetailFragment().apply {
            arguments = Bundle().apply {
                putInt("masterpieceBoardId", masterpieceBoardId)
            }
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.masterpiece_fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}