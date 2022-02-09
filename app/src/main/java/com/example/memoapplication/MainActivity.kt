package com.example.memoapplication

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.memoapplication.data.memoInfo
import com.example.memoapplication.databinding.ActivityMainBinding
import com.example.memoapplication.recyclerview.MainAdapter
import com.google.firebase.database.*

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

                firebaseDatabase.reference.child("memo").child(temp).child("lock")
                    .addValueEventListener(object :ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val solveValue = snapshot.getValue(String::class.java)
                            if (!solveValue.equals("")) {
                                val dialog = LayoutInflater.from(this@MainActivity).inflate(R.layout.custom_dialog_solving_memo, null)
                                val mBuilder = AlertDialog.Builder(this@MainActivity)
                                    .setView(dialog)
                                    .setTitle("암호 입력")
                                val customDialog = mBuilder.show()
                                val yesBtn = customDialog.findViewById<AppCompatButton>(R.id.yes_btn_solve)
                                val noBtn = customDialog.findViewById<AppCompatButton>(R.id.no_btn_solve)
                                val solvingPw = customDialog.findViewById<EditText>(R.id.pw_solve_editText)

                                yesBtn.setOnClickListener {
                                    val pw = solvingPw.text.toString()
                                    if (pw == solveValue) {
                                        Toast.makeText(this@MainActivity, "암호가 알맞습니다", Toast.LENGTH_SHORT).show()
                                        customDialog.dismiss()
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
                                    } else {
                                        solvingPw.text = null
                                        Toast.makeText(this@MainActivity, "암호가 올바르지 않아요", Toast.LENGTH_SHORT).show()
                                    }
                                }

                                noBtn.setOnClickListener {
                                    customDialog.dismiss()
                                }
                            } else {
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
                        override fun onCancelled(error: DatabaseError) {}
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

        // 잠금 메뉴
        binding.lock.setOnClickListener {
            startActivity(Intent(this, LockMemoActivity::class.java))
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