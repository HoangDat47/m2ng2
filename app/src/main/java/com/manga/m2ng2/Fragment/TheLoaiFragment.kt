package com.manga.m2ng2.Fragment

import android.content.Intent
import android.os.Bundle
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

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TheLoaiFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TheLoaiFragment : Fragment(R.layout.fragment_the_loai) {
    private lateinit var binding: FragmentTheLoaiBinding
    private lateinit var ds: ArrayList<TheLoaiModel>
    private lateinit var dbRef: DatabaseReference
    private lateinit var adapter: TheLoaiAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTheLoaiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnThemTheLoai.setOnClickListener {
            startActivity(Intent(requireContext(), ThemTheLoaiActivity::class.java))
        }
        binding.themTruyen.setOnClickListener {
            startActivity(Intent(requireContext(), ThemTruyenActivity::class.java))
        }
        loadTheLoai()
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
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
