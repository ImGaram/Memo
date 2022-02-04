package com.example.memoapplication.recyclerview

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.example.memoapplication.recyclerview.FilterAdapter.MyViewHolder
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.Filter
import com.example.memoapplication.R
import android.widget.TextView
import android.widget.Filterable
import com.example.memoapplication.data.memoInfo
import java.util.ArrayList

class FilterAdapter(var context: Context, var unFilteredList: ArrayList<String>) :
    RecyclerView.Adapter<MyViewHolder>(), Filterable {
    var filteredList: ArrayList<String>
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.recyclerview_searching_item_info, parent, false)
        return MyViewHolder(view)
    }

    interface ItemClick {
        fun onClick(view:View, data: String, position: Int)
    }
    var itemClick: ItemClick? = null


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.textView.text = filteredList[position]
        holder.bind(filteredList[position])
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textView: TextView = itemView.findViewById(R.id.recyclerview_item_memoTitle)

        fun bind(items: String) {
            val pos = adapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                itemView.setOnClickListener {
                    itemClick?.onClick(itemView, items, pos)
                }
            }
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                val charString = constraint.toString()
                filteredList = if (charString.isEmpty()) {
                    unFilteredList
                } else {
                    val filteringList = ArrayList<String>()
                    for (name in unFilteredList) {
                        if (name.toLowerCase().contains(charString.toLowerCase())) {
                            filteringList.add(name)
                        }
                    }
                    filteringList
                }
                val filterResults = FilterResults()
                filterResults.values = filteredList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence, results: FilterResults) {
                filteredList = results.values as ArrayList<String>
                notifyDataSetChanged()
            }
        }
    }

    init {
        filteredList = unFilteredList
    }
}