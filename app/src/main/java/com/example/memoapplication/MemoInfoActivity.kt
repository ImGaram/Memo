package com.example.memoapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toolbar
import com.example.memoapplication.data.memoInfo
import com.example.memoapplication.databinding.ActivityMemoInfoBinding

class MemoInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMemoInfoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMemoInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val data = intent.getStringExtra("title")
        val comment = intent.getStringExtra("data")
        binding.memoInfoTitle.title = data
        binding.memoInfoComment.text = comment

        binding.back.setOnClickListener {
            finish()
        }
    }
}