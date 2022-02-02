package com.example.memoapplication.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.memoapplication.R
import com.example.memoapplication.data.memoInfo

class MainAdapter(private val items: MutableList<memoInfo>): RecyclerView.Adapter<MainAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_item_main_info, parent, false)
        return ViewHolder(view)
    }

    interface ItemClick {
        fun onClick(view:View, data: memoInfo, position: Int)
    }
    var itemClick: ItemClick? = null

    interface ItemLongClick {
        fun onLongClick(view:View, data: memoInfo, position: Int)
    }
    var itemLongClick: ItemLongClick? = null

    override fun onBindViewHolder(holder: MainAdapter.ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.recyclerview_title)
        val time = itemView.findViewById<TextView>(R.id.recyclerview_time)

        fun bind(item: memoInfo) {
            title.text = item.title
            time.text = item.time

            val pos = adapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                itemView.setOnClickListener {
                    itemClick?.onClick(itemView, item, pos)
                }

                itemView.setOnLongClickListener(object : View.OnLongClickListener {
                    override fun onLongClick(p0: View?): Boolean {
                        itemLongClick?.onLongClick(itemView, item, pos)
                        return false
                    }
                })
            }
        }
    }
}