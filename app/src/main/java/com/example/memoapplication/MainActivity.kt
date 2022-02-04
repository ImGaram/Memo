package com.example.memoapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
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

        // 메모 보기
        adapter.itemClick = object: MainAdapter.ItemClick {
            override fun onClick(view: View, data: memoInfo, position: Int) {
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

        // 메모 수정
        adapter.itemLongClick = object : MainAdapter.ItemLongClick {
            override fun onLongClick(view: View, data: memoInfo, position: Int) {
                val modifyTemp = itemList[position].title.toString()
                val modifyMenu = PopupMenu(applicationContext, view)
                menuInflater.inflate(R.menu.memo_modify, modifyMenu.menu)
                modifyMenu.setOnMenuItemClickListener(object :PopupMenu.OnMenuItemClickListener {
                    override fun onMenuItemClick(p0: MenuItem?): Boolean {
                        when (p0?.itemId) {
                            R.id.modify_memo -> {
                                firebaseDatabase.reference.child("memo").child(modifyTemp).child("comment")
                                    .addValueEventListener(object :ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            val modifyValue: String? = snapshot.getValue(String::class.java)
                                            val intent = Intent(this@MainActivity, ModifyMemoActivity::class.java)
                                                .putExtra("title", modifyTemp).putExtra("comment", modifyValue)
                                            startActivity(intent)
                                        }
                                        override fun onCancelled(error: DatabaseError) {}
                                    })
                            }
                        }
                        return false
                    }

                })
                modifyMenu.show()
            }
        }

        // 검색 메뉴
        binding.search.setOnClickListener {
            startActivity(Intent(this, SearchMemoActivity::class.java))
        }

        // 팝업 메뉴
        binding.menu.setOnClickListener {
            val popupMenu = PopupMenu(applicationContext, it)
            menuInflater.inflate(R.menu.memo_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener(object :PopupMenu.OnMenuItemClickListener {
                override fun onMenuItemClick(p0: MenuItem?): Boolean {
                    when (p0?.itemId) {
                        R.id.addd_memo -> {
                            startActivity(Intent(this@MainActivity, AddMemoActivity::class.java))
                        }
                        R.id.delete_memo -> {
                            startActivity(Intent(this@MainActivity, DeleteMemoActivity::class.java))
                        }
                    }
                    return false
                }
            })
            popupMenu.show()
        }
    }

    private var backPressedTime : Long = 0
    override fun onBackPressed() {
        if (System.currentTimeMillis() > backPressedTime + 2000) {
            backPressedTime = System.currentTimeMillis()
            Toast.makeText(this, "\'뒤로\' 버튼을 한번 더 누르면 종료", Toast.LENGTH_SHORT).show()
            return
        } else if (System.currentTimeMillis() <= backPressedTime + 2000) {
            finish()
        }
    }
}