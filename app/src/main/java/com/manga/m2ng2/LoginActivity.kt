package com.manga.m2ng2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.manga.m2ng2.databinding.ActivityLoginBinding
import com.manga.m2ng2.tools.Constrains

class LoginActivity : AppCompatActivity() {
    private var email = ""
    private var password = ""
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        binding.btnLogin.setOnClickListener {
            validateData()
        }
        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun validateData() {
        email = binding.etEmail.text.toString().trim()
        password = binding.etPassword.text.toString().trim()
        if (email.isBlank()) {
            binding.etEmail.error = "Làm ơn nhập email của bạn"
            return
        } else if (password.isBlank()) {
            binding.etPassword.error = "Làm ơn nhập mật khẩu của bạn"
            return
        } else {
            performLogin()
        }
    }

    private fun performLogin() {
        email = binding.etEmail.text.toString().trim()
        password = binding.etPassword.text.toString().trim()
        Toast.makeText(this, "Đang đăng nhập", Toast.LENGTH_SHORT).show()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show()
                    checkRole()
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show()
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()

                }
            }
    }

    private fun checkRole() {
        var user = auth.currentUser
        //check db
        var databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")
        databaseReference.child(user!!.uid).get().addOnSuccessListener {
            Constrains.userRole = it.child("userType").value.toString()
            val intent = Intent(this, TrangAdminActivity::class.java)
            startActivity(intent)
        }
    }
}