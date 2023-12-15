package com.example.bokup

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TrackAdapter(private val dataList : ArrayList<TrackDataClass>) : RecyclerView.Adapter<TrackAdapter.ViewHolderClass> (){
    private var isHidden: Boolean = false

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): TrackAdapter.ViewHolderClass {
        val itemView = LayoutInflater.from(p0.context).inflate(R.layout.track_layout, p0, false)
        return ViewHolderClass(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val currentItem = dataList[position]
        holder.timeCal.text = currentItem.timeCal
        holder.cal.text = currentItem.cal
        holder.dateCal.text = currentItem.dateCal
    }

    override fun getItemCount(): Int {
        return dataList.size
        return if (isHidden) 0 else dataList.size
    }

    class ViewHolderClass(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var timeCal: TextView = itemView.findViewById(R.id.tvcalTime)
        var cal: TextView = itemView.findViewById(R.id.tvcalText)
        var dateCal : TextView = itemView.findViewById(R.id.tvDate)
    }

    fun hideItems() {
        isHidden = true
        notifyDataSetChanged()
    }

}
