package com.example.memoapplication.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.memoapplication.R

class ModifyCodeAdapter(val items: MutableList<String>): RecyclerView.Adapter<ModifyCodeAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ModifyCodeAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_item_modify_code, parent, false)
        return ViewHolder(view)
    }

    interface ItemClick {
        fun onClick(view:View, data: String, position: Int)
    }
    var itemClick: ItemClick? = null

    override fun onBindViewHolder(holder: ModifyCodeAdapter.ViewHolder, position: Int) {
        holder.titleText.text = items[position]
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val titleText = itemView.findViewById<TextView>(R.id.recyclerview_item_modify_memo)

        fun bind(items: String) {
            val pos = adapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                itemView.setOnClickListener {
                    itemClick?.onClick(itemView, items, pos)
                }
            }
        }
    }
}