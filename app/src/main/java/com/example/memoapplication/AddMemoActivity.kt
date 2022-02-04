package com.example.memoapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.memoapplication.data.memoInfo
import com.example.memoapplication.databinding.ActivityAddMemoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class AddMemoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddMemoBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMemoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()
        val simpleDateFormat = SimpleDateFormat("yyyy.MM.dd hh:mm", Locale.KOREA)

        binding.saveBtn.setOnClickListener {
            val title: String = binding.titleEditText.text.toString()
            val comment: String = binding.commentEditText.text.toString()
            val time = System.currentTimeMillis()
            val mDate = Date(time)
            val date = simpleDateFormat.format(mDate)

            if (TextUtils.isEmpty(title) || TextUtils.isEmpty(comment)) {
                Toast.makeText(this, "비면 안됨", Toast.LENGTH_SHORT).show()
            } else if (title.length > 9) {
                Toast.makeText(this, "이름이 너무 김", Toast.LENGTH_SHORT).show()
            } else {
                addData(title, comment, date)
            }
        }

        binding.back.setOnClickListener {
            finish()
        }
    }

    // 데이터 저장
    private fun addData(title: String, comment: String, date: String) {
        val memoInfo = memoInfo(title, comment, date)
        val reference: DatabaseReference = database.reference
        reference.child("memo").child(title).setValue(memoInfo).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(this, "업로드가 됨", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
