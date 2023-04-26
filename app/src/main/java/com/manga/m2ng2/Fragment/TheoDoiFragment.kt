package com.manga.m2ng2.Fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.manga.m2ng2.Activities.TruyenDetailActivity
import com.manga.m2ng2.adapter.TruyenAdapter
import com.manga.m2ng2.databinding.FragmentTheoDoiBinding
import com.manga.m2ng2.model.TruyenModel
import com.manga.m2ng2.tools.Helper

class TheoDoiFragment : Fragment() {
    private lateinit var binding: FragmentTheoDoiBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var dbRef: DatabaseReference
    private lateinit var userRef: DatabaseReference
    private lateinit var ds1: ArrayList<TruyenModel>
    private lateinit var adapter: TruyenAdapter
    private var truyenId: String? = null
    private val TAG = "TheoDoiFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTheoDoiBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadTruyenTheoDoi()
    }

    private fun loadTruyenTheoDoi() {
        userRef = FirebaseDatabase.getInstance().getReference("Users/${auth.currentUser?.uid}/TheoDoi")
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                truyenId = snapshot.children.map { it.key }.toString()
                loadTruyen(truyenId!!)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "onCancelled: ${error.message}")
            }
        })
    }

    private fun loadTruyen(truyenId: String) {
        binding.rvTruyenTheoDoi.layoutManager = LinearLayoutManager(context)
        ds1 = arrayListOf<TruyenModel>()
        dbRef = FirebaseDatabase.getInstance().getReference("Truyen")
        dbRef.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {
                ds1.clear()
                for (data in snapshot.children) {
                    val truyen = data.getValue(TruyenModel::class.java)
                    if (truyenId.contains(truyen?.id.toString())) {
                        ds1.add(truyen!!)
                    }
                }
                adapter = TruyenAdapter(ds1)
                binding.rvTruyenTheoDoi.adapter = adapter
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
                //them truyen count size
                binding.tvTruyenTheoDoi.text = String.format("Truyện theo dõi: (${ds1.size})")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "onCancelled: ${error.message}")
            }
        })
    }
}