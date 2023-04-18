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
            // Truy vấn vào Firebase để lấy danh sách truyện có chứa thể loại đó
            val truyenRef = FirebaseDatabase.getInstance().getReference("Truyen")
            val query = truyenRef.orderByChild("theLoaiId").equalTo(id)
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Kiểm tra xem danh sách truyện có rỗng hay không
                    if (snapshot.exists() && snapshot.childrenCount > 0) {
                        // Nếu danh sách truyện không rỗng, hiển thị cảnh báo cho người dùng biết
                        val builder = AlertDialog.Builder(holder.itemView.context)
                        builder.setTitle("Xóa thể loại")
                        builder.setMessage("Thể loại này đang có ${snapshot.childrenCount} truyện. Bạn không thể xóa thể loại này!")
                        builder.setPositiveButton("OK") { _, _ -> }
                        builder.show()
                    } else {
                        // Nếu danh sách truyện rỗng, thực hiện hiện lựa chọn có không rồi mới xóa
                        val builder = AlertDialog.Builder(holder.itemView.context)
                        builder.setTitle("Xóa thể loại")
                        builder.setMessage("Bạn có chắc chắn muốn xóa?")
                        builder.setPositiveButton("Có") { _, _ ->
                            // Nếu người dùng đồng ý xóa thể loại, thực hiện xóa
                            val dbRef = FirebaseDatabase.getInstance().getReference("TheLoai")
                            dbRef.child(id!!).removeValue()
                            Toast.makeText(holder.itemView.context, "Đã xóa thể loại", Toast.LENGTH_SHORT).show()
                        }
                        builder.setNegativeButton("Không") { _, _ -> }
                        builder.show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Xử lý khi truy vấn không thành công
                    Toast.makeText(holder.itemView.context, "Lỗi: " + error.message, Toast.LENGTH_SHORT).show()
                }
            })
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
