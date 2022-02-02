package com.example.memoapplication

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.memoapplication.data.memoInfo
import com.example.memoapplication.databinding.ActivityDeleteMemoBinding
import com.example.memoapplication.recyclerview.DeleteAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DeleteMemoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDeleteMemoBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var firebaseDatabase: FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDeleteMemoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val itemList = mutableListOf<memoInfo>()

        firebaseDatabase = FirebaseDatabase.getInstance()
        recyclerView = findViewById(R.id.recyclerview_delete)
        val adapter = DeleteAdapter(itemList)
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

        adapter.itemClick = object: DeleteAdapter.ItemClick {
            override fun onClick(view: View, data: memoInfo, position: Int) {
                val temp = itemList[position].title.toString()
                val dialog =LayoutInflater.from(this@DeleteMemoActivity).inflate(R.layout.custom_dialog_delete_memo, null)
                val mBuilder = AlertDialog.Builder(this@DeleteMemoActivity)
                    .setView(dialog)
                    .setTitle("메모 삭제")

                val customDialog = mBuilder.show()
                val text = customDialog.findViewById<TextView>(R.id.text_delete_whether)
                text.text = "메모 $temp 을(를) 삭제 할까요?"

                val yesBtn = customDialog.findViewById<AppCompatButton>(R.id.yes_btn)
                val noBtn = customDialog.findViewById<AppCompatButton>(R.id.no_btn)

                yesBtn.setOnClickListener {
                    firebaseDatabase.reference.child("memo").child(temp).removeValue()
                    Toast.makeText(this@DeleteMemoActivity, "삭제 되었습니다.", Toast.LENGTH_SHORT).show()
                    customDialog.dismiss()
                }

                noBtn.setOnClickListener {
                    customDialog.dismiss()
                }
            }
        }

        binding.backDel.setOnClickListener {
            finish()
        }
    }
}