package com.example.memoapplication

import android.app.AlertDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.memoapplication.data.memoInfo
import com.example.memoapplication.databinding.ActivityLockMemoBinding
import com.example.memoapplication.recyclerview.LockAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LockMemoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLockMemoBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var firebaseDatabase: FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLockMemoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val itemList = mutableListOf<memoInfo>()

        firebaseDatabase = FirebaseDatabase.getInstance()
        recyclerView = findViewById(R.id.recyclerview_lock)
        val adapter = LockAdapter(itemList)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        firebaseDatabase.reference.child("memo").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                itemList.clear()
                for (datasnapshot in snapshot.children) {
                    val memoInfo = datasnapshot.getValue(memoInfo::class.java)
                    itemList.add(memoInfo!!)
                }
                adapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {  }
        })

        adapter.itemClick = object: LockAdapter.ItemClick {
            override fun onClick(view: View, data: memoInfo, position: Int) {
                val temp = itemList[position].title.toString()

                firebaseDatabase.reference.child("memo").child(temp).child("lock")
                    .addValueEventListener(object :ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val unlockValue = snapshot.getValue(String::class.java)
                            if (!unlockValue.equals("")) {
                                Toast.makeText(this@LockMemoActivity, "이미 잠금 설정 됨", Toast.LENGTH_SHORT).show()
                            } else {
                                val dialog = LayoutInflater.from(this@LockMemoActivity).inflate(R.layout.custom_dialog_lock_memo, null)
                                val mBuilder = AlertDialog.Builder(this@LockMemoActivity)
                                    .setView(dialog)
                                    .setTitle("암호 설정")
                                val customDialog = mBuilder.show()

                                val yesBtn = customDialog.findViewById<AppCompatButton>(R.id.yes_btn_lock)
                                val noBtn = customDialog.findViewById<AppCompatButton>(R.id.no_btn_lock)
                                val lockRef = firebaseDatabase.reference.child("memo").child(temp).child("lock")

                                yesBtn.setOnClickListener {
                                    val lockEditText = customDialog.findViewById<EditText>(R.id.pw_lock_editText)
                                    val pw = lockEditText.text.toString()
                                    lockRef.setValue(pw)
                                        .addOnCompleteListener {
                                            if (it.isSuccessful) {
                                                Toast.makeText(this@LockMemoActivity, "설정 되었습니다.", Toast.LENGTH_SHORT).show()
                                                customDialog.dismiss()
                                            }
                                        }
                                }

                                noBtn.setOnClickListener {
                                    customDialog.dismiss()
                                }
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {}
                    })
            }
        }

        binding.backLock.setOnClickListener {
            finish()
        }
    }
}