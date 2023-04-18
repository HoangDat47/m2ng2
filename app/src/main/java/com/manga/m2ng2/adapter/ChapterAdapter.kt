package com.manga.m2ng2.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.manga.m2ng2.databinding.LayoutChapterBinding
import com.manga.m2ng2.model.ChapterModel

class ChapterAdapter (private var ds2: ArrayList<ChapterModel>)
    : RecyclerView.Adapter<ChapterAdapter.chapterViewHolder>(){
    class chapterViewHolder(val binding: LayoutChapterBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): chapterViewHolder {
        val binding = LayoutChapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return chapterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: chapterViewHolder, position: Int) {
        val tenChapter = ds2[position].title
        holder.binding.tvChapterTitle.text = tenChapter
    }

    override fun getItemCount(): Int {
        return ds2.size
    }
}