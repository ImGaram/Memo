package com.example.memoapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.memoapplication.data.memoInfo
import com.example.memoapplication.databinding.ActivityMainBinding
import com.example.memoapplication.recyclerview.MainAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var firebaseDatabase: FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val itemList = mutableListOf<memoInfo>()

        firebaseDatabase = FirebaseDatabase.getInstance()
        recyclerView = findViewById(R.id.recyclerview)
        val adapter = MainAdapter(itemList)
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

        adapter.itemClick = object: MainAdapter.ItemClick {
            override fun onClick(view: View, data: memoInfo, position: Int) {
//                Toast.makeText(this@MainActivity, "${itemList[position].title}", Toast.LENGTH_SHORT).show()
                val temp = itemList[position].title.toString()
                firebaseDatabase.reference.child("memo").child(temp).child("comment")
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val value: String? = snapshot.getValue(String::class.java)
                            val intent = Intent(this@MainActivity, MemoInfoActivity::class.java)
                                .putExtra("data",value).putExtra("title", temp)
                            startActivity(intent)
                        }
                        override fun onCancelled(error: DatabaseError) {  }
                    })
            }
        }

        binding.addMemo.setOnClickListener {
            startActivity(Intent(this, AddMemoActivity::class.java))
        }
    }
}