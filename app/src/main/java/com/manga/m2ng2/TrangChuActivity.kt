package com.manga.m2ng2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.manga.m2ng2.databinding.ActivityTrangChuBinding

class TrangChuActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTrangChuBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrangChuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init firebase auth
        auth = FirebaseAuth.getInstance()
        checkUser()

        binding.btnLogOut.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun checkUser() {
        val firebaseUser = auth.currentUser
        if (firebaseUser != null) {
            //user is already logged in
            val email = firebaseUser.email
            Toast.makeText(this, "Đăng nhập với $email admin", Toast.LENGTH_SHORT).show()
        } else {
            //user not logged in, user can stay in this activity without login
            Toast.makeText(this, "Chưa đăng nhập", Toast.LENGTH_SHORT).show()
        }
    }
}