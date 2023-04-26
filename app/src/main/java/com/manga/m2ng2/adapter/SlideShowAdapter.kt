package com.manga.m2ng2.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.manga.m2ng2.databinding.ItemSlideBinding

class SlideShowAdapter(private val slideList: ArrayList<Int>, private val viewPager2: ViewPager2) :
    RecyclerView.Adapter<SlideShowAdapter.SlideShowViewHolder>() {
    class SlideShowViewHolder(val binding: ItemSlideBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlideShowViewHolder {
        val binding = ItemSlideBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SlideShowViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SlideShowViewHolder, position: Int) {
        holder.binding.imageView.setImageResource(slideList[position])
        if (position == slideList.size - 1) {
            viewPager2.post(runnable)
        }
    }

    override fun getItemCount(): Int {
        return slideList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    private val runnable = Runnable {
        slideList.addAll(slideList)
        notifyDataSetChanged()
    }
}
