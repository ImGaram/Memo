package com.example.memoapplication

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.memoapplication.data.memoInfo
import com.example.memoapplication.databinding.ActivitySearchMemoBinding
import com.example.memoapplication.recyclerview.FilterAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SearchMemoActivity : AppCompatActivity(), TextWatcher {

    private lateinit var binding: ActivitySearchMemoBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var adapter: FilterAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchMemoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val itemList = arrayListOf<String>()
        firebaseDatabase = FirebaseDatabase.getInstance()

        val recyclerview = findViewById<RecyclerView>(R.id.recyclerview_searching)
        val editText = binding.searchEditText
        editText.addTextChangedListener(this)

        adapter = FilterAdapter(applicationContext, itemList)

        firebaseDatabase.reference.child("memo").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                itemList.clear()
                for (datasnapshot in snapshot.children) {
                    val memoInfo = datasnapshot.getValue(memoInfo::class.java)
                    itemList.add(memoInfo?.title.toString())
                }
                adapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {  }
        })

        recyclerview.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        recyclerview.adapter = adapter

        adapter.itemClick = object : FilterAdapter.ItemClick {
            override fun onClick(view: View, data: String, position: Int) {
                val temp = itemList[position]
                firebaseDatabase.reference.child("memo").child(temp).child("comment")
                    .addValueEventListener(object :ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val value: String? = snapshot.getValue(String::class.java)
                            val intent = Intent(this@SearchMemoActivity, MemoInfoActivity::class.java)
                                .putExtra("data",value).putExtra("title", temp)
                            startActivity(intent)
                        }
                        override fun onCancelled(error: DatabaseError) {}
                    })
            }
        }

        binding.backSearch.setOnClickListener {
            finish()
        }
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        adapter.filter.filter(p0)
    }

    override fun afterTextChanged(p0: Editable?) {}
}