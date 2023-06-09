package com.manga.m2ng2.Activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.manga.m2ng2.adapter.TheLoaiAdapter
import com.manga.m2ng2.adapter.TruyenAdapter
import com.manga.m2ng2.databinding.ActivityTruyenListAdminBinding
import com.manga.m2ng2.model.TruyenModel
import com.manga.m2ng2.tools.FilterTruyenAdmin
import com.manga.m2ng2.tools.Helper

class TruyenListAdminActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTruyenListAdminBinding
    private lateinit var ds1: ArrayList<TruyenModel>
    private lateinit var dbRef: DatabaseReference
    private lateinit var adapter: TruyenAdapter
    private lateinit var filter: FilterTruyenAdmin
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
        adapter = TruyenAdapter(arrayListOf(),1)
        loadTruyenList()
        if(this::filter.isInitialized){
            filterSearch()
        } else {
            filter = FilterTruyenAdmin(ds1, adapter)
            filterSearch()
        }
    }

    private fun filterSearch() {
        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do nothing
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filter.filter(s)
            }

            override fun afterTextChanged(s: Editable?) {
                // Do nothing
            }
        })
    }

    private fun loadTruyenList() {
        binding.rvTruyen.layoutManager = LinearLayoutManager(this)
        ds1 = arrayListOf<TruyenModel>()
        dbRef = FirebaseDatabase.getInstance().getReference("Truyen")
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                ds1.clear()
                for (data in snapshot.children) {
                    val truyen = data.getValue(TruyenModel::class.java)
                    if (truyen?.theLoaiId == theLoaiId) {
                        ds1.add(truyen!!)
                    }
                }
                adapter = TruyenAdapter(ds1,1)
                binding.rvTruyen.adapter = adapter

                //lang nghe su kien click item recyclerview
                adapter.setOnItemClickListener(object : TruyenAdapter.OnItemClickListener {
                    override fun onItemClick(position: Int) {
                        val intent = intent
                        intent.setClass(this@TruyenListAdminActivity, TruyenDetailActivity::class.java)
                        intent.putExtra("truyenid", ds1[position].id)
                        intent.putExtra("truyentitle", ds1[position].title)
                        intent.putExtra("truyenDate", ds1[position].timestamp?.let { Helper().formatNgayGio(it) })
                        intent.putExtra("truyendesc", ds1[position].desc)
                        intent.putExtra("imageUrl", ds1[position].imageUrl)
                        startActivity(intent)
                    }
                })
                filter = FilterTruyenAdmin(ds1, adapter)
            }

            override fun onCancelled(error: DatabaseError) {
                // Do nothing
            }
        })
    }
}