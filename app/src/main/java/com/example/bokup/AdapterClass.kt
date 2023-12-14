package com.example.bokup

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
 class AdapterClass(private val dataList : ArrayList<DataClass>) : RecyclerView.Adapter<AdapterClass.ViewHolderClass> (){

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): AdapterClass.ViewHolderClass {
        var itemView = LayoutInflater.from(p0.context).inflate(R.layout.track_layout, p0, false)
        return ViewHolderClass(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int ) {

        var currentItems = dataList[position]
        holder.timeCal.text = currentItems.timeCal
        holder.cal.text = currentItems.cal
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    class ViewHolderClass(itemView: View) : RecyclerView.ViewHolder(itemView){

        var timeCal: TextView = itemView.findViewById(R.id.tvcalTime)
        var cal : TextView = itemView.findViewById(R.id.tvcalText)
    }

}