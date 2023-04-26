package com.manga.m2ng2.adapter

import android.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.manga.m2ng2.databinding.LayoutChapterBinding
import com.manga.m2ng2.model.ChapterModel
import com.manga.m2ng2.tools.Constrains

class ChapterAdapter (private var ds2: ArrayList<ChapterModel>)
    : RecyclerView.Adapter<ChapterAdapter.chapterViewHolder>(){
    init {
        ds2.sortByDescending { it.title }
    }
    //lang nghe su kien click item recyclerview
    private var listener: OnItemClickListener? = null
    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }
    class chapterViewHolder(val binding: LayoutChapterBinding, listener: OnItemClickListener) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                listener?.onItemClick(adapterPosition)
            }
        }
        val btn_deleteChapter = binding.btnDeleteChapter
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): chapterViewHolder {
        val binding = LayoutChapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return chapterViewHolder(binding, listener!!)
    }

    override fun onBindViewHolder(holder: chapterViewHolder, position: Int) {
        //get data
        val id = ds2[position].id
        val truyenId = ds2[position].truyenId
        val filePathAndName = "Chapter/$truyenId/$id"
        //set data
        if(Constrains.userRole == "admin"){
            holder.btn_deleteChapter.setOnClickListener {
                val builder = AlertDialog.Builder(holder.itemView.context)
                builder.setTitle("Xóa chapter")
                builder.setMessage("Bạn có chắc chắn muốn xóa?")
                builder.setPositiveButton("Có") { _, _ ->
                    val dbRef = FirebaseDatabase.getInstance().getReference("Chapter/$truyenId")
                    dbRef.child(id!!).removeValue()
                    val storageReference = FirebaseStorage.getInstance().reference.child(filePathAndName)
                    storageReference.delete().addOnSuccessListener {
                        Log.d("TAG", "onBindViewHolder: Xóa thành công")
                    }.addOnFailureListener {
                        Log.d("TAG", "onBindViewHolder: Xóa thất bại")
                    }
                    Toast.makeText(holder.itemView.context, "Đã xóa chapter", Toast.LENGTH_SHORT).show()
                }
                builder.setNegativeButton("Không") { _, _ -> }
                builder.show()
            }
        }else{
            holder.btn_deleteChapter.visibility = View.GONE
        }
        val tenChapter = ds2[position].title
        holder.binding.tvChapterTitle.text = tenChapter
        holder.binding.tvViews.text = ds2[position].chapterView.toString()

    }

    override fun getItemCount(): Int {
        return ds2.size
    }
}