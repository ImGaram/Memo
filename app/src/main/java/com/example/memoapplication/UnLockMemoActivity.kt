package com.example.memoapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.memoapplication.data.memoInfo
import com.example.memoapplication.databinding.ActivityUnLockMemoBinding
import com.example.memoapplication.recyclerview.LockAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UnLockMemoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUnLockMemoBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var firebaseDatabase: FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUnLockMemoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val itemList = mutableListOf<memoInfo>()

        firebaseDatabase = FirebaseDatabase.getInstance()
        recyclerView = findViewById(R.id.recyclerview_unlock)
        val adapter = LockAdapter(itemList)

        firebaseDatabase.reference.child("memo").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                itemList.clear()
                for (datasnapshot in snapshot.children) {
                    val memoInfo = datasnapshot.getValue(memoInfo::class.java)
                    itemList.add(memoInfo!!)
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        adapter.itemClick = object :LockAdapter.ItemClick {
            override fun onClick(view: View, data: memoInfo, position: Int) {
                val temp = itemList[position].title.toString()

                firebaseDatabase.reference.child("memo").child(temp).child("lock")
                    .addValueEventListener(object :ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val unlockValue = snapshot.getValue(String::class.java)

                            if (!unlockValue.equals("")) {
                                val dialog = LayoutInflater.from(this@UnLockMemoActivity)
                                    .inflate(R.layout.custom_dialog_unlock_code_memo, null)
                                val mBuilder = AlertDialog.Builder(this@UnLockMemoActivity)
                                    .setView(dialog)
                                    .setTitle("암호 혜제")
                                val customDialog = mBuilder.show()

                                val yesBtn = customDialog.findViewById<AppCompatButton>(R.id.yes_btn_unlock)
                                val noBtn = customDialog.findViewById<AppCompatButton>(R.id.no_btn_unlock)

                                yesBtn?.setOnClickListener {
                                    firebaseDatabase.reference.child("memo").child(temp).child("lock").setValue("")
                                        .addOnCompleteListener {
                                            if (it.isSuccessful) {
                                                Toast.makeText(this@UnLockMemoActivity, "혜제 되었습니다.", Toast.LENGTH_SHORT).show()
                                                customDialog.dismiss()
                                            }
                                        }
                                }

                                noBtn?.setOnClickListener {
                                    customDialog.dismiss()
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })
            }
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        binding.backUnlock.setOnClickListener {
            finish()
        }
    }
}