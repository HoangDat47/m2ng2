package com.manga.m2ng2.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.manga.m2ng2.adapter.TheLoaiAdapter
import com.manga.m2ng2.adapter.ViewPageAdapter
import com.manga.m2ng2.databinding.ActivityTrangAdminBinding
import com.manga.m2ng2.tools.FilterTheLoaiAdmin
import com.manga.m2ng2.model.TheLoaiModel
import com.manga.m2ng2.tools.Constrains

class TrangAdminActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTrangAdminBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrangAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val vpAdapter = ViewPageAdapter(supportFragmentManager, lifecycle)
        binding.viewPager.adapter = vpAdapter   //set adapter cho viewpager
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Thể loại"
                1 -> tab.text = "Người dùng"
            }
        }.attach()

        //init firebase auth
        auth = FirebaseAuth.getInstance()
        checkUser()

        binding.btnLogOut.setOnClickListener {
            auth.signOut()
            checkUser()
        }
    }

    private fun checkUser() {
        val firebaseUser = auth.currentUser
        if (firebaseUser != null) {
            //user is already logged in
            val email = firebaseUser.email
            Toast.makeText(this, "Đăng nhập bằng $email", Toast.LENGTH_SHORT).show()
            var databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")
            /*databaseReference.child(firebaseUser!!.uid).get().addOnSuccessListener {
                Constrains.userRole = it.child("userType").value.toString()
                if (it.child("userType").value.toString() == "admin") {
                    binding.linearLayout.setVisibility(View.VISIBLE);
                } else {
                    binding.linearLayout.setVisibility(View.GONE);
                }
                loadTheLoai()
            }*/
        } else {
            //user not logged in, go to main activity
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}