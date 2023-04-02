package com.manga.m2ng2.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.manga.m2ng2.databinding.LayoutTruyenBinding
import com.manga.m2ng2.model.TruyenModel
import com.manga.m2ng2.tools.DayConvert

class TruyenAdapter (private var ds1: ArrayList<TruyenModel>)
    : RecyclerView.Adapter<TruyenAdapter.truyenViewHolder>() {
    @SuppressLint("NotifyDataSetChanged")
    fun updateList(filtered: ArrayList<TruyenModel>) {
        ds1 = filtered
        notifyDataSetChanged()
    }
    class truyenViewHolder(val binding: LayoutTruyenBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): truyenViewHolder {
        val binding = LayoutTruyenBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return truyenViewHolder(binding)
    }

    override fun onBindViewHolder(holder: truyenViewHolder, position: Int) {
        val id = ds1[position].id
        val tenTruyen = ds1[position].title
        val timestamp = ds1[position].timestamp
        val timeFormat = DayConvert().formatTimeStamp(timestamp!!)
        holder.binding.tvTenTruyen.text = tenTruyen
        holder.binding.tvNgayDang.text = timeFormat
    }

    override fun getItemCount(): Int {
        return ds1.size
    }
}