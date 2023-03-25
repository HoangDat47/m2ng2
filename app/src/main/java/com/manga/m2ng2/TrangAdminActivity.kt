package com.manga.m2ng2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.manga.m2ng2.databinding.ActivityTrangAdminBinding

class TrangAdminActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTrangAdminBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrangAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init firebase auth
        auth = FirebaseAuth.getInstance()
        checkUser()

        binding.btnLogOut.setOnClickListener {
            auth.signOut()
            checkUser()
        }
        binding.btnThemTheLoai.setOnClickListener {
            startActivity(Intent(this, ThemTheLoaiActivity::class.java))
        }
    }

    private fun checkUser() {
        val firebaseUser = auth.currentUser
        if (firebaseUser != null) {
            //user is already logged in
            val email = firebaseUser.email
            Toast.makeText(this, "Đăng nhập với $email admin", Toast.LENGTH_SHORT).show()
        } else {
            //user not logged in, go to main activity
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}