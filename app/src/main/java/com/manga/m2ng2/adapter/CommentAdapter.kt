package com.manga.m2ng2.adapter

import android.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.manga.m2ng2.databinding.RowCommentBinding
import com.manga.m2ng2.model.CommentModel
import com.squareup.picasso.Picasso

class CommentAdapter(private var ds: ArrayList<CommentModel>) :
    RecyclerView.Adapter<CommentAdapter.commentViewHolder>() {
    class commentViewHolder(val binding: RowCommentBinding) : RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): commentViewHolder {
        val binding = RowCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return commentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: commentViewHolder, position: Int) {
        val comment = ds[position].comment
        val name = ds[position].name
        val profileImage = ds[position].profileImage
        val timestamp = ds[position].timestamp
        val ngayGio = com.manga.m2ng2.tools.Helper().formatNgayGio(timestamp!!)
        holder.binding.comment.text = comment
        holder.binding.tenUser.text = name
        holder.binding.commentDate.text = ngayGio
        if (profileImage != null) {
            Picasso.get().load(profileImage).into(holder.binding.avatar)
        } else {
            Picasso.get().load(com.manga.m2ng2.R.drawable.baseline_person_24).into(holder.binding.avatar)
        }

        //btn xoa
        val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
        if (auth.currentUser != null && auth.currentUser!!.uid == ds[position].uid) {
            holder.binding.btnXoaComment.visibility = android.view.View.VISIBLE
        } else {
            holder.binding.btnXoaComment.visibility = android.view.View.GONE

        }
        //xoa comment
        holder.binding.btnXoaComment.setOnClickListener {
            val id = ds[position].id
            val truyenid = ds[position].truyenid
            val builder = AlertDialog.Builder(holder.itemView.context)
            builder.setTitle("Xóa comment")
            builder.setMessage("Bạn có chắc chắn muốn xóa?")
            builder.setPositiveButton("Có") { _, _ ->
                val dbRef = FirebaseDatabase.getInstance().getReference("Truyen/$truyenid/BinhLuan")
                dbRef.child(id!!).removeValue()
                Toast.makeText(holder.itemView.context, "Đã xóa comment", Toast.LENGTH_SHORT).show()
            }
            builder.setNegativeButton("Không") { _, _ -> }
            builder.show()
        }
    }

    override fun getItemCount(): Int {
        return ds.size
    }
}