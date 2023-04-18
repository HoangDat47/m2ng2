package com.manga.m2ng2.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.manga.m2ng2.databinding.ActivityThemTheLoaiBinding

class ThemTheLoaiActivity : AppCompatActivity() {
    private lateinit var binding: ActivityThemTheLoaiBinding
    private lateinit var auth: FirebaseAuth
    private var theLoai = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityThemTheLoaiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init firebase auth
        auth = FirebaseAuth.getInstance()

        binding.btnThemTheLoai.setOnClickListener {
            validateData()
        }
    }

    private fun validateData() {
        theLoai = binding.edtTheLoai.text.toString().trim()
        if (theLoai.isEmpty()) {
            binding.edtTheLoai.error = "Vui lòng nhập thể loại"
        } else {
            themTheLoaiFB()
        }
    }

    private fun themTheLoaiFB() {
        val timestamp = System.currentTimeMillis()
        var hashMap: HashMap<String, Any> = HashMap()
        hashMap.put("id", timestamp.toString())
        hashMap.put("theLoai", theLoai)
        hashMap.put("timestamp", timestamp)
        hashMap.put("uid", auth.uid.toString())

        //DB ROOT > THELOAI > THELOAIID >THELOAIINFO
        val ref = FirebaseDatabase.getInstance().getReference("TheLoai")
        ref.child(timestamp.toString()).setValue(hashMap)
            .addOnSuccessListener {
                binding.edtTheLoai.setText("")
                Toast.makeText(this, "Thêm thể loại thành công", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {e ->
                Toast.makeText(this, "Thêm thể loại thất bại (${e.message})", Toast.LENGTH_SHORT).show()
            }
    }
}