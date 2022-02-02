package com.example.memoapplication

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.memoapplication.databinding.ActivityMemoModifyBinding
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class ModifyMemoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMemoModifyBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMemoModifyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseDatabase = FirebaseDatabase.getInstance()
        val comment = intent.getStringExtra("comment")
        val title = intent.getStringExtra("title")
        val simpleDateFormat = SimpleDateFormat("yyyy.MM.dd hh:mm", Locale.KOREA)

        binding.modifyMemoTitle.text = title
        binding.modifyCommentEditText.setText(comment)

        binding.saveModifyBtn.setOnClickListener {
            if (TextUtils.isEmpty(binding.modifyCommentEditText.text)) {
                Toast.makeText(this, "비면 안됨", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val changedText = binding.modifyCommentEditText.text.toString()
            val time = System.currentTimeMillis()
            val mDate = Date(time)
            val date = simpleDateFormat.format(mDate)
            val ref = firebaseDatabase.reference.child("memo").child(title.toString())
            ref.child("time").setValue(date)
            ref.child("comment")
                .setValue(changedText).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "수정 됨", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addCategory(Intent.CATEGORY_HOME)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                }
            }
        }

        binding.backModify.setOnClickListener {
            finish()
        }
    }
}