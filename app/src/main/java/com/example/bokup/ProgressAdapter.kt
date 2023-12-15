package com.example.bokup

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProgressAdapter(private val dataList : ArrayList<ProgressDataClass>) : RecyclerView.Adapter<ProgressAdapter.ViewHolderClass> (){
    private var isHidden: Boolean = false

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolderClass {
        val itemView = LayoutInflater.from(p0.context).inflate(R.layout.progress_layout, p0, false)
        return ViewHolderClass(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val currentItem = dataList[position]
        holder.dateCal.text = currentItem.dateCal ?: "No Date"
        holder.tvcalTotal.text = currentItem.totalCal ?: "No Total"
    }

    override fun getItemCount(): Int {
        return if (isHidden) 0 else dataList.size
    }

    class ViewHolderClass(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var dateCal: TextView = itemView.findViewById(R.id.tvcalDate)
        var tvcalTotal: TextView = itemView.findViewById(R.id.tvcalTotal)  // Corrected ID
    }

    fun hideItems() {
        isHidden = true
        notifyDataSetChanged()
    }

}
