package com.manga.m2ng2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.manga.m2ng2.adapter.TheLoaiAdapter
import com.manga.m2ng2.databinding.ActivityTrangAdminBinding
import com.manga.m2ng2.tools.FilterTheLoaiAdmin
import com.manga.m2ng2.model.TheLoaiModel

class TrangAdminActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTrangAdminBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var ds: ArrayList<TheLoaiModel>
    private lateinit var dbRef: DatabaseReference
    private lateinit var adapter: TheLoaiAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrangAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init firebase auth
        auth = FirebaseAuth.getInstance()
        checkUser()
        loadTheLoai()
        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //called as and when user types each letter
                val originalList = ds.clone() as ArrayList<TheLoaiModel>
                val filter = FilterTheLoaiAdmin(originalList, adapter)
                filter.filter(s)
            }

            override fun afterTextChanged(s: Editable?) {
                // Do nothing
            }
        })
        binding.btnLogOut.setOnClickListener {
            auth.signOut()
            checkUser()
        }
        binding.btnThemTheLoai.setOnClickListener {
            startActivity(Intent(this, ThemTheLoaiActivity::class.java))
        }
        binding.addPdfTab.setOnClickListener {
            startActivity(Intent(this, ThemPDFActivity::class.java))
        }
    }

    private fun loadTheLoai() {
        binding.rvTheLoai.layoutManager = LinearLayoutManager(this)
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
                Toast.makeText(this@TrangAdminActivity, "${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun checkUser() {
        val firebaseUser = auth.currentUser
        if (firebaseUser != null) {
            //user is already logged in
            val email = firebaseUser.email
            Toast.makeText(this, "Đăng nhập bằng $email", Toast.LENGTH_SHORT).show()
        } else {
            //user not logged in, go to main activity
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}