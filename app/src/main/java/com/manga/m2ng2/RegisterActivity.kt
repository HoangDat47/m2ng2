package com.manga.m2ng2

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.manga.m2ng2.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private var name = ""
    private var email = ""
    private var password = ""
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        binding.btnRegister.setOnClickListener {
            validateData()
        }
    }

    private fun validateData() {
        name = binding.etName.text.toString().trim()
        email = binding.etEmail.text.toString().trim()
        password = binding.etPassword.text.toString().trim()
        var cPassWord = binding.etConfirmPassword.text.toString().trim()
        if(name.isBlank()) {
            binding.etName.error = "Làm ơn nhập tên của bạn"
            return
        } else if (email.isBlank()) {
            binding.etEmail.error = "Làm ơn nhập email của bạn"
            return
        } else if (password.isBlank()) {
            binding.etPassword.error = "Làm ơn nhập mật khẩu của bạn"
            return
        } else if (cPassWord.isBlank()) {
            binding.etConfirmPassword.error = "Làm ơn nhập lại mật khẩu của bạn"
            return
        } else if (password != cPassWord) {
            binding.etConfirmPassword.error = "Mật khẩu không khớp"
            return
        } else {
            performSignUp()
        }
    }

    private fun performSignUp() {
        name = binding.etName.text.toString().trim()
        email = binding.etEmail.text.toString().trim()
        password = binding.etPassword.text.toString().trim()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    luuThongTin()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Xác thực thất bại.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun luuThongTin() {
        Toast.makeText(this, "Lưu thông tin người dùng", Toast.LENGTH_SHORT).show()
        var timestamp = System.currentTimeMillis()
        var uid = auth.uid
        var hashMap: HashMap<String, Any> = HashMap()
        hashMap.put("uid", uid.toString())
        hashMap.put("name", name)
        hashMap.put("email", email)
        hashMap.put("password", password)
        hashMap.put("timestamp", timestamp)
        hashMap.put("userType", "user")
        hashMap.put("profileImage", "")

        // lưu thông tin người dùng vào realtime database
        var databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")
        databaseReference.child(uid.toString()).setValue(hashMap).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Đăng ký thất bại", Toast.LENGTH_SHORT).show()
            }
        }
    }
}