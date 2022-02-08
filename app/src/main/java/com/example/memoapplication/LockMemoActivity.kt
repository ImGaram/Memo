package com.example.memoapplication

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.memoapplication.data.memoInfo
import com.example.memoapplication.databinding.ActivityLockMemoBinding
import com.example.memoapplication.recyclerview.LockAdapter
import com.example.memoapplication.recyclerview.ModifyCodeAdapter
import com.google.firebase.database.*

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

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter.itemClick = object : LockAdapter.ItemClick {
            override fun onClick(view: View, data: memoInfo, position: Int) {
                val temp = itemList[position].title.toString()
                val lockRef = firebaseDatabase.reference.child("memo").child(temp).child("lock")

                lockRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val unlockValue = snapshot.getValue(String::class.java)

                        if (unlockValue == "") {
                            val dialog = LayoutInflater.from(this@LockMemoActivity)
                                .inflate(R.layout.custom_dialog_lock_memo, null)
                            val mBuilder = AlertDialog.Builder(this@LockMemoActivity)
                                .setView(dialog)
                                .setTitle("암호 설정")
                            val customDialog = mBuilder.show()

                            val yesBtn = customDialog.findViewById<AppCompatButton>(R.id.yes_btn_lock)
                            val noBtn = customDialog.findViewById<AppCompatButton>(R.id.no_btn_lock)

                            yesBtn.setOnClickListener {
                                val lockEditText = customDialog.findViewById<EditText>(R.id.pw_lock_editText)
                                val pw = lockEditText.text.toString()
                                lockRef.setValue(pw)
                                    .addOnCompleteListener {
                                        if (it.isSuccessful) {
                                            Toast.makeText(this@LockMemoActivity, "설정 되었습니다.",
                                                Toast.LENGTH_SHORT).show()
                                            customDialog.dismiss()
                                            return@addOnCompleteListener
                                        }
                                    }
                                return@setOnClickListener
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

        binding.lockMore.setOnClickListener {
            val popupMenu = PopupMenu(applicationContext, it)
            menuInflater.inflate(R.menu.memo_unlock, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
                override fun onMenuItemClick(p0: MenuItem?): Boolean {
                    when(p0?.itemId) {
                        R.id.modify_lock -> {
                            val modifyLockDialog = LayoutInflater.from(this@LockMemoActivity)
                                .inflate(R.layout.custom_dialog_modify_lock, null)
                            val modifyBuilder = AlertDialog.Builder(this@LockMemoActivity)
                                .setView(modifyLockDialog)
                                .setTitle("암호 수정")
                            val customModifyDialog = modifyBuilder.show()

                            val nextBtn = customModifyDialog.findViewById<AppCompatButton>(R.id.yes_btn_modify_lock)
                            val cancelBtn = customModifyDialog.findViewById<AppCompatButton>(R.id.no_btn_modify_lock)

                            val lockingList = arrayListOf<String>()
                            val modifyAdapter = ModifyCodeAdapter(lockingList)
                            val modifyRecyclerview: RecyclerView = customModifyDialog
                                .findViewById(R.id.recyclerview_modify_lock)

                            modifyRecyclerview.layoutManager = LinearLayoutManager(this@LockMemoActivity)
                            modifyRecyclerview.adapter = modifyAdapter

                            firebaseDatabase.reference.child("memo").addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    lockingList.clear()
                                    for (datasnapshot in snapshot.children) {
                                        val memoInfo = datasnapshot.getValue(memoInfo::class.java)
                                        lockingList.add(memoInfo?.title.toString())
                                    }
                                    modifyAdapter.notifyDataSetChanged()
                                }
                                override fun onCancelled(error: DatabaseError) {  }
                            })


                            var selectedItem: String? = null
                            modifyAdapter.itemClick = object :ModifyCodeAdapter.ItemClick {
                                override fun onClick(view: View, data: String, position: Int) {
                                    val selectedText = customModifyDialog.findViewById<TextView>(R.id.recyclerview_selected_memo)

                                    readData(firebaseDatabase.reference.root.child("memo"), object :OnGetDataListener {
                                        override fun onSuccess(dataSnapshotValue: MutableList<String>) {
                                            selectedText.text = "선택한 메모 : ${dataSnapshotValue[position]}"
                                            selectedItem = dataSnapshotValue[position]
                                        }
                                    })
                                }
                            }

                            nextBtn.setOnClickListener {
                                customModifyDialog.dismiss()

                                val changeCodeRef = firebaseDatabase.reference.child("memo").child(selectedItem.toString())
                                changeCodeRef.child("lock").addValueEventListener(object :ValueEventListener{
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        val pw = snapshot.getValue(String::class.java)
                                        if (pw.equals("")) {
                                            Toast.makeText(this@LockMemoActivity, "암호가 있는 메모가 아닙니다..", Toast.LENGTH_SHORT).show()
                                            return
                                        } else {

                                            val changeCodeDialog = LayoutInflater.from(this@LockMemoActivity)
                                                .inflate(R.layout.custom_dialog_change_code, null)
                                            val changeCodeBuilder = AlertDialog.Builder(this@LockMemoActivity)
                                                .setView(changeCodeDialog)
                                                .setTitle("암호 수정")
                                            val customChangeCodeDialog = changeCodeBuilder.show()

                                            val changeYesBtn = customChangeCodeDialog.findViewById<AppCompatButton>(R.id.yes_btn_change_code)
                                            val changeNoBtn = customChangeCodeDialog.findViewById<AppCompatButton>(R.id.no_btn_change_code)

                                            changeYesBtn.setOnClickListener {
                                                val selectedChangeCodeText = customChangeCodeDialog.findViewById<EditText>(R.id.pw_change_code_editText)
                                                val selectedCode: String = selectedChangeCodeText.text.toString()

                                                changeCodeRef.child("lock").setValue(selectedCode).addOnCompleteListener { code ->
                                                    if (code.isSuccessful) {
                                                        Toast.makeText(this@LockMemoActivity, "암호가 수정 되었습니다.", Toast.LENGTH_SHORT).show()
                                                        customChangeCodeDialog.dismiss()
                                                    }
                                                }
                                            }

                                            changeNoBtn.setOnClickListener {
                                                customChangeCodeDialog.dismiss()
                                            }
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {}
                                })
                            }

                            cancelBtn.setOnClickListener {
                                customModifyDialog.dismiss()
                            }
                        }
                        R.id.unlock -> {
                            startActivity(Intent(this@LockMemoActivity, UnLockMemoActivity::class.java))
                        }
                    }
                    return false
                }
            })
            popupMenu.show()
        }

        binding.backLock.setOnClickListener {
            finish()
        }
    }

    private fun readData(ref: DatabaseReference, listener: OnGetDataListener) {
        ref.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<String>()
                for (datasnapshot in snapshot.children) {
                    val getTitle = datasnapshot.getValue(memoInfo::class.java)
                    list.add(getTitle!!.title!!)
                }
                listener.onSuccess(list)
            }

            override fun onCancelled(error: DatabaseError) {}

        })

    }

    interface OnGetDataListener {
        fun onSuccess(dataSnapshotValue: MutableList<String>)
    }
}