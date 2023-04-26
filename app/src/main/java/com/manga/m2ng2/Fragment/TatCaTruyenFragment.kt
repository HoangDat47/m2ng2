package com.manga.m2ng2.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.manga.m2ng2.Activities.TruyenDetailActivity
import com.manga.m2ng2.R
import com.manga.m2ng2.adapter.TruyenAdapter
import com.manga.m2ng2.databinding.FragmentTatCaTruyenBinding
import com.manga.m2ng2.model.TruyenModel
import com.manga.m2ng2.tools.Helper

class TatCaTruyenFragment : Fragment() {
    private lateinit var binding: FragmentTatCaTruyenBinding
    private lateinit var ds1: ArrayList<TruyenModel>
    private lateinit var dbRef: DatabaseReference
    private lateinit var adapter: TruyenAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTatCaTruyenBinding.inflate(inflater, container, false)
        loadTatCaTruyen()
        return binding.root
    }

    private fun loadTatCaTruyen() {
        binding.rvTruyen1.layoutManager = GridLayoutManager(context, 3)
        ds1 = arrayListOf<TruyenModel>()
        dbRef = FirebaseDatabase.getInstance().getReference("Truyen")
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                ds1.clear()
                for (data in snapshot.children) {
                    val truyen = data.getValue(TruyenModel::class.java)
                    ds1.add(truyen!!)
                }
                adapter = TruyenAdapter(ds1, 2)
                binding.rvTruyen1.adapter = adapter

                //lang nghe su kien click item recyclerview
                adapter.setOnItemClickListener(object : TruyenAdapter.OnItemClickListener {
                    override fun onItemClick(position: Int) {
                        val intent = android.content.Intent()
                        intent.setClass(requireContext(), TruyenDetailActivity::class.java)
                        intent.putExtra("truyenid", ds1[position].id)
                        intent.putExtra("truyentitle", ds1[position].title)
                        intent.putExtra("truyenDate", ds1[position].timestamp?.let { Helper().formatNgayGio(it) })
                        intent.putExtra("truyendesc", ds1[position].desc)
                        intent.putExtra("imageUrl", ds1[position].imageUrl)
                        startActivity(intent)
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                // Do nothing
            }
        })
    }
}