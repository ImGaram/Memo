package com.example.memoapplication.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.memoapplication.R
import com.example.memoapplication.data.memoInfo

class DeleteAdapter(private val items: MutableList<memoInfo>): RecyclerView.Adapter<DeleteAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeleteAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_item_delete_info, parent, false)
        return ViewHolder(view)
    }

    interface ItemClick {
        fun onClick(view: View, data: memoInfo, position: Int)
    }
    var itemClick: ItemClick? = null

    override fun onBindViewHolder(holder: DeleteAdapter.ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.recyclerview_title)

        fun bind(item: memoInfo) {
            title.text = item.title

            val pos = adapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                itemView.setOnClickListener {
                    itemClick?.onClick(itemView, item, pos)
                }
            }

        }
    }
}