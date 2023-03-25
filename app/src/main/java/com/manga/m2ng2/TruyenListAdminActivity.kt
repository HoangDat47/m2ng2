package com.manga.m2ng2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.manga.m2ng2.adapter.TruyenAdapter
import com.manga.m2ng2.databinding.ActivityTruyenListAdminBinding
import com.manga.m2ng2.model.TruyenModel

class TruyenListAdminActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTruyenListAdminBinding
    private lateinit var ds1: ArrayList<TruyenModel>
    private lateinit var dbRef: DatabaseReference
    private lateinit var adapter: TruyenAdapter
    private var theLoaiId: String? = null
    private var tenTheLoai: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTruyenListAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
        intent.extras?.let {
            theLoaiId = it.getString("theLoaiId")
            tenTheLoai = it.getString("tenTheLoai")
        }
        loadTruyenList()
    }

    private fun loadTruyenList() {
        binding.rvTruyen.layoutManager = LinearLayoutManager(this)
        ds1 = arrayListOf<TruyenModel>()
        dbRef = FirebaseDatabase.getInstance().getReference("truyens")
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                ds1.clear()
                for (data in snapshot.children) {
                    val truyen = data.getValue(TruyenModel::class.java)
                    if (truyen?.theLoaiId == theLoaiId) {
                        ds1.add(truyen!!)
                    }
                }
                adapter = TruyenAdapter(ds1)
                binding.rvTruyen.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                // Do nothing
            }
        })
    }
}