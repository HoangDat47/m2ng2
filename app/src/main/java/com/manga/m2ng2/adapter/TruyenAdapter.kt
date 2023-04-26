package com.manga.m2ng2.adapter

import android.R
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.manga.m2ng2.databinding.LayoutTruyen2Binding
import com.manga.m2ng2.databinding.LayoutTruyenBinding
import com.manga.m2ng2.model.TruyenModel
import com.manga.m2ng2.tools.Helper
import com.squareup.picasso.Picasso

class TruyenAdapter(
    private var ds1: ArrayList<TruyenModel>,
    private val viewType: Int
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_1 -> {
                val binding = LayoutTruyenBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                TruyenViewHolder(binding, listener!!)
            }

            VIEW_TYPE_2 -> {
                val binding = LayoutTruyen2Binding.inflate(LayoutInflater.from(parent.context), parent, false)
                TruyenViewHolder2(binding, listener!!)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return viewType
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == VIEW_TYPE_1) {
            val truyenViewHolder = holder as TruyenViewHolder
            // bind data cho item chẵn
            val tenTruyen = ds1[position].title
            val timestamp = ds1[position].timestamp
            val timeFormat = Helper().formatTimeStamp(timestamp!!)
            val imageUrl = ds1[position].imageUrl
            if (imageUrl != null) {
                Picasso.get().load(imageUrl).placeholder(R.drawable.ic_menu_gallery)
                    .into(truyenViewHolder.binding.imgTruyen)
            } else {
                Picasso.get().load(R.drawable.ic_menu_gallery).into(truyenViewHolder.binding.imgTruyen)
            }
            truyenViewHolder.binding.tvTenTruyen.text = tenTruyen
            truyenViewHolder.binding.tvNgayDang.text = timeFormat
        } else {
            val truyenViewHolder2 = holder as TruyenViewHolder2
            val tenTruyen = ds1[position].title
            val imageUrl = ds1[position].imageUrl
            if (imageUrl != null) {
                Picasso.get().load(imageUrl).placeholder(R.drawable.ic_menu_gallery)
                    .into(truyenViewHolder2.binding.imgTruyen)
            } else {
                Picasso.get().load(R.drawable.ic_menu_gallery).into(truyenViewHolder2.binding.imgTruyen)
            }
            truyenViewHolder2.binding.tvTenTruyen.text = tenTruyen
        }


    }


    override fun getItemCount(): Int {
        return ds1.size
    }

    class TruyenViewHolder(val binding: LayoutTruyenBinding, listener: OnItemClickListener) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }

    class TruyenViewHolder2(val binding: LayoutTruyen2Binding, listener: OnItemClickListener) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }


    companion object {
        private const val VIEW_TYPE_1 = 1
        private const val VIEW_TYPE_2 = 2
    }
}

