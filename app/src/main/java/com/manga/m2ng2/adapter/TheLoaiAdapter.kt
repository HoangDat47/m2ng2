package com.manga.m2ng2.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
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
            holder.binding.btnDeleteTheLoai.setOnClickListener {
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
            holder.binding.btnDeleteTheLoai.visibility = View.GONE
            holder.binding.btnEditTheLoai.visibility = View.GONE
        }

        holder.binding.btnEditTheLoai.setOnClickListener {
            showEditText(holder)
        }
        holder.binding.btnCancel.setOnClickListener {
            cancelEditText(holder)
        }
        holder.binding.btnDone.setOnClickListener {
            saveEditText(holder)
        }

        //item click go to TruyenListAdminActivity
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, TruyenListAdminActivity::class.java)
            intent.putExtra("theLoaiId", theLoai.id)
            intent.putExtra("tenTheLoai", theLoai.theLoai)
            holder.itemView.context.startActivity(intent)
        }
    }

    private fun saveEditText(holder: TheLoaiAdapter.theLoaiViewHolder) {
        val theLoai = holder.binding.edtTheLoai.text.toString().trim()
        if (theLoai.isEmpty()) {
            holder.binding.edtTheLoai.error = "Không được để trống"
            holder.binding.edtTheLoai.requestFocus()
            return
        }
        val dbRef = FirebaseDatabase.getInstance().getReference("TheLoai")
        dbRef.child(ds[holder.adapterPosition].id!!).child("theLoai").setValue(theLoai)
            .addOnSuccessListener {
                Toast.makeText(holder.itemView.context, "Đã lưu thay đổi", Toast.LENGTH_SHORT).show()
            } .addOnCanceledListener {
                Toast.makeText(holder.itemView.context, "Lưu thay đổi thất bại", Toast.LENGTH_SHORT).show()
            }
        hideEditText(holder)
    }

    private fun cancelEditText(holder: TheLoaiAdapter.theLoaiViewHolder) {
        hideEditText(holder)
        holder.binding.edtTheLoai.setText(holder.binding.tvTheLoai.text)
    }

    private fun hideEditText(holder: TheLoaiAdapter.theLoaiViewHolder) {
        holder.binding.edtTheLoai.visibility = View.GONE
        holder.binding.tvTheLoai.visibility = View.VISIBLE
        holder.binding.btnEditTheLoai.visibility = View.VISIBLE
        holder.binding.btnDeleteTheLoai.visibility = View.VISIBLE
        holder.binding.btnDone.visibility = View.GONE
        holder.binding.btnCancel.visibility = View.GONE
        holder.binding.edtTheLoai.setText(holder.binding.tvTheLoai.text)
    }

    private fun showEditText(holder: theLoaiViewHolder) {
        holder.binding.edtTheLoai.visibility = View.VISIBLE
        holder.binding.tvTheLoai.visibility = View.GONE
        holder.binding.btnEditTheLoai.visibility = View.GONE
        holder.binding.btnDeleteTheLoai.visibility = View.GONE
        holder.binding.btnDone.visibility = View.VISIBLE
        holder.binding.btnCancel.visibility = View.VISIBLE
        holder.binding.edtTheLoai.setText(holder.binding.tvTheLoai.text)
    }

    override fun getItemCount(): Int {
        return ds.size
    }

    class theLoaiViewHolder(val binding: LayoutTheloaiBinding) : RecyclerView.ViewHolder(binding.root)
}
