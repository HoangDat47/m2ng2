package com.manga.m2ng2.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.manga.m2ng2.Activities.TruyenListAdminActivity
import com.manga.m2ng2.databinding.LayoutTheloaiBinding
import com.manga.m2ng2.model.TheLoaiModel
import com.manga.m2ng2.tools.Constrains

class TheLoaiAdapter(private var ds: ArrayList<TheLoaiModel>) :
    RecyclerView.Adapter<TheLoaiAdapter.theLoaiViewHolder>() {
    // Declare a public function to update the dataset with the filtered results
        init {
            ds.sortBy { it.theLoai }
        }
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
        //set data
        holder.binding.tvTheLoai.text = theLoai.theLoai
        if (Constrains.userRole == "admin") {
            holder.btn_deleteTheLoai.setOnClickListener {
                val truyenRef = FirebaseDatabase.getInstance().getReference("Truyen")
                val query = truyenRef.orderByChild("theLoaiId").equalTo(id)
                query.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists() && snapshot.childrenCount > 0) {
                            val builder = AlertDialog.Builder(holder.itemView.context)
                            builder.setTitle("Xóa thể loại")
                            builder.setMessage("Thể loại này đang có ${snapshot.childrenCount} truyện. Bạn không thể xóa thể loại này!")
                            builder.setPositiveButton("OK") { _, _ -> }
                            builder.show()
                        } else {
                            val builder = AlertDialog.Builder(holder.itemView.context)
                            builder.setTitle("Xóa thể loại")
                            builder.setMessage("Bạn có chắc chắn muốn xóa?")
                            builder.setPositiveButton("Có") { _, _ ->
                                val dbRef = FirebaseDatabase.getInstance().getReference("TheLoai")
                                dbRef.child(id!!).removeValue()
                                Toast.makeText(holder.itemView.context, "Đã xóa thể loại", Toast.LENGTH_SHORT).show()
                            }
                            builder.setNegativeButton("Không") { _, _ -> }
                            builder.show()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(holder.itemView.context, "Lỗi: " + error.message, Toast.LENGTH_SHORT).show()
                    }
                })
            }
        } else {
            holder.btn_deleteTheLoai.visibility = android.view.View.GONE
            holder.btn_editTheLoai.visibility = android.view.View.GONE
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
        val btn_editTheLoai = binding.btnEditTheLoai
    }
}
