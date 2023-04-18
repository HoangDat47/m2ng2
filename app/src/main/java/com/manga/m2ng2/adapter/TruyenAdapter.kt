package com.manga.m2ng2.adapter

import android.R
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.manga.m2ng2.databinding.LayoutTruyenBinding
import com.manga.m2ng2.model.TruyenModel
import com.manga.m2ng2.tools.DayConvert
import com.squareup.picasso.Picasso

class TruyenAdapter (private var ds1: ArrayList<TruyenModel>)
    : RecyclerView.Adapter<TruyenAdapter.truyenViewHolder>() {
        //lang nghe su kien click item recyclerview
    private var listener: OnItemClickListener? = null
    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }
    @SuppressLint("NotifyDataSetChanged")
    fun updateList(filtered: ArrayList<TruyenModel>) {
        ds1 = filtered
        notifyDataSetChanged()
    }
    class truyenViewHolder(val binding: LayoutTruyenBinding, listener: OnItemClickListener) : RecyclerView.ViewHolder(binding.root){
        init {
            binding.root.setOnClickListener {
                listener?.onItemClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): truyenViewHolder {
        val binding = LayoutTruyenBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return truyenViewHolder(binding, listener!!)
    }

    override fun onBindViewHolder(holder: truyenViewHolder, position: Int) {
        val tenTruyen = ds1[position].title
        val timestamp = ds1[position].timestamp
        val timeFormat = DayConvert().formatTimeStamp(timestamp!!)
        val imageUrl = ds1[position].imageUrl
        if(imageUrl != null) {
            Picasso.get().load(imageUrl).placeholder(R.drawable.ic_menu_gallery).into(holder.binding.imgTruyen)
        } else {
            Picasso.get().load(R.drawable.ic_menu_gallery).into(holder.binding.imgTruyen)
        }
        holder.binding.tvTenTruyen.text = tenTruyen
        holder.binding.tvNgayDang.text = timeFormat
    }

    override fun getItemCount(): Int {
        return ds1.size
    }
}