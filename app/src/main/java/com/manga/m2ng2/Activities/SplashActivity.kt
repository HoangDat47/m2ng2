package com.manga.m2ng2.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.coroutines.delay
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.manga.m2ng2.R
import com.manga.m2ng2.tools.Constrains
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var backButtonPressed: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        auth = FirebaseAuth.getInstance()

        lifecycleScope.launch {
            delay(1000L)
            if (!backButtonPressed) {
                checkUser()
            }
        }
    }

    override fun onBackPressed() {
        backButtonPressed = true
        super.onBackPressed()
    }

    private fun checkUser() {
        val firebaseUser = auth.currentUser
        if (firebaseUser != null) {
            //user is already logged in
            var user = auth.currentUser
            //check db
            var databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")
            databaseReference.child(user!!.uid).get().addOnSuccessListener {
                Constrains.userRole = it.child("userType").value.toString()
                val intent = Intent(this, TrangAdminActivity::class.java)
                startActivity(intent)
                finish()
            }
        } else {
            //user not logged in, go to main activity
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
