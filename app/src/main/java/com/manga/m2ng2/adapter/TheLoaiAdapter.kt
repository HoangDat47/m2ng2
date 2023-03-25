package com.manga.m2ng2.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.manga.m2ng2.TruyenListAdminActivity
import com.manga.m2ng2.databinding.LayoutTheloaiBinding
import com.manga.m2ng2.model.TheLoaiModel

class TheLoaiAdapter(private var ds: ArrayList<TheLoaiModel>) :
    RecyclerView.Adapter<TheLoaiAdapter.theLoaiViewHolder>() {
    // Declare a public function to update the dataset with the filtered results
    @SuppressLint("NotifyDataSetChanged")
    fun updateList(filtered: ArrayList<TheLoaiModel>) {
        ds = filtered
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): theLoaiViewHolder {
        //binding layout_theloai.xml
        val binding = LayoutTheloaiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return theLoaiViewHolder(binding)
    }

    override fun onBindViewHolder(holder: theLoaiViewHolder, position: Int) {
        //get data
        val id = ds[position].id
        val theLoai = ds[position]
        val uid = ds[position].uid
        val timestamp = ds[position].timestamp
        //set data
        holder.binding.tvTheLoai.text = theLoai.theLoai
        holder.btn_deleteTheLoai.setOnClickListener {
            //delete theloai
            AlertDialog.Builder(holder.itemView.context)
                .setTitle("Xóa thể loại")
                .setMessage("Bạn có muốn xóa thể loại này không?")
                .setPositiveButton("Có") { dialog, which ->
                    val dbRef = FirebaseDatabase.getInstance().getReference("TheLoai")
                    dbRef.child(id!!).removeValue()
                    Toast.makeText(holder.itemView.context, "Đã xóa thể loại", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Không") { dialog, which ->
                    dialog.dismiss()
                }
                .show()
        }
        //item click go to TruyenListAdminActivity
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, TruyenListAdminActivity::class.java)
            intent.putExtra("theLoaiId", theLoai.id)
            intent.putExtra("tenTheLoai", theLoai.theLoai)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return ds.size
    }

    class theLoaiViewHolder(val binding: LayoutTheloaiBinding) : RecyclerView.ViewHolder(binding.root) {
        val btn_deleteTheLoai = binding.btnDeleteTheLoai
    }
}
