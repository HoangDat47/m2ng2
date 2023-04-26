package com.manga.m2ng2.Fragment

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.manga.m2ng2.Activities.ThemTheLoaiActivity
import com.manga.m2ng2.Activities.ThemTruyenActivity
import com.manga.m2ng2.R
import com.manga.m2ng2.adapter.TheLoaiAdapter
import com.manga.m2ng2.databinding.FragmentTheLoaiBinding
import com.manga.m2ng2.model.TheLoaiModel
import com.manga.m2ng2.tools.Constrains
import com.manga.m2ng2.tools.FilterTheLoaiAdmin
import com.manga.m2ng2.tools.FilterTruyenAdmin

class TheLoaiFragment : Fragment(R.layout.fragment_the_loai) {
    private lateinit var binding: FragmentTheLoaiBinding
    private lateinit var ds: ArrayList<TheLoaiModel>
    private lateinit var dbRef: DatabaseReference
    private lateinit var adapter: TheLoaiAdapter
    private lateinit var filter: FilterTheLoaiAdmin
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = TheLoaiAdapter(arrayListOf())
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTheLoaiBinding.inflate(inflater, container, false)
        loadTheLoai()
        if (Constrains.userRole == "admin") {
            binding.linearLayout.visibility = View.VISIBLE
            binding.btnThemTheLoai.setOnClickListener {
                startActivity(Intent(requireContext(), ThemTheLoaiActivity::class.java))
            }
            binding.themTruyen.setOnClickListener {
                startActivity(Intent(requireContext(), ThemTruyenActivity::class.java))
            }
        } else {
            binding.linearLayout.visibility = View.GONE
        }

        if (this::filter.isInitialized) {
            filterSearch()
        } else {
            filter = FilterTheLoaiAdmin(ds, adapter)
            filterSearch()
        }
        return binding.root
    }

    private fun filterSearch() {
        filter = FilterTheLoaiAdmin(ds, adapter)
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


    private fun loadTheLoai() {
        binding.rvTheLoai.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTheLoai.setHasFixedSize(true)
        ds = arrayListOf<TheLoaiModel>()
        //load theloai db>theloai
        dbRef = FirebaseDatabase.getInstance().getReference("TheLoai")
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //clear list before adding data into it
                ds.clear()
                for (data in snapshot.children) {
                    val theLoai = data.getValue(TheLoaiModel::class.java)
                    ds.add(theLoai!!)
                }
                //adapter
                adapter = TheLoaiAdapter(ds)
                //set adapter to recyclerview
                binding.rvTheLoai.adapter = adapter
                filter = FilterTheLoaiAdmin(ds, adapter)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
}

