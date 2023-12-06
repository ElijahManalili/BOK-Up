package com.example.bokup

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
 class AdapterClass(var dataList : ArrayList<DataClass>) : RecyclerView.Adapter<AdapterClass.ViewHolderClass> (){

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): AdapterClass.ViewHolderClass {
        var itemView = LayoutInflater.from(p0.context).inflate(R.layout.item_layout, p0, false)
        return ViewHolderClass(itemView)
    }

    override fun onBindViewHolder(p0: ViewHolderClass, p1: Int ) {
        var currentItems = dataList[p1]
        p0.rvTitle.text = currentItems.cal
        p0.rvTitle.text = currentItems.timeCal
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    class ViewHolderClass(itemView: View) : RecyclerView.ViewHolder(itemView){
        var rvTitle : TextView = itemView.findViewById(R.id.reyclerTitle)

    }

}