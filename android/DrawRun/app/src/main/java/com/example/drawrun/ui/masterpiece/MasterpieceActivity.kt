package com.example.drawrun.ui.masterpiece

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.drawrun.R
import com.example.drawrun.databinding.ActivityMasterpieceBinding
import com.example.drawrun.ui.masterpiece.fragment.MasterpieceDetailFragment
import com.example.drawrun.ui.masterpiece.fragment.MasterpieceSearchFragment

class MasterpieceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMasterpieceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMasterpieceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction()
            .replace(R.id.masterpiece_fragment_container, MasterpieceSearchFragment())
            .commit()
    }
}