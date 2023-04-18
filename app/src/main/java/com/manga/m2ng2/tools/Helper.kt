package com.manga.m2ng2.tools

import android.app.Application
import android.content.Context
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class Helper : Application() {
    override fun onCreate() {
        super.onCreate()
    }

    fun formatTimeStamp(timestamp: Long): String {
        val currentTimestamp = System.currentTimeMillis()
        val timeDiff = currentTimestamp - timestamp
        if (timeDiff < 0) {
            return "Vừa xong"
        }
        val day = timeDiff / 86400000
        val hour = (timeDiff % 86400000) / 3600000
        val minute = (timeDiff % 3600000) / 60000
        val second = (timeDiff % 60000) / 1000
        return when {
            day > 0 -> "$day ngày trước"
            hour > 0 -> "$hour giờ trước"
            minute > 0 -> "$minute phút trước"
            else -> "$second giây trước"
        }
    }

    fun formatNgayGio(timestamp: Long): String {
        //chuyen ve dd/MM/yyyy HH:mm vd 25/10/2003 20:30
        val currentTimestamp = System.currentTimeMillis()

        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp

        val currentTime = sdf.format(Date(currentTimestamp))
        val time = sdf.format(calendar.time)

        return if (currentTimestamp - timestamp < 86400000) { // 86400000 milliseconds = 1 day
            // if the timestamp is within 1 day, return only the time
            time.substring(time.indexOf(" ") + 1)
        } else if (currentTime.substring(0, 10) == time.substring(0, 10)) {
            // if the timestamp is on the same day as the current time, return only the date and time
            time
        } else {
            // otherwise, return the date and time
            time
        }
    }
    fun convertChuoi(chuoi: String): String {
        return chuoi.replace(" ", "-")
    }
    fun theoDoiTruyen(context: Context, truyenid:String) {
        var dbRef: DatabaseReference
        val auth = FirebaseAuth.getInstance()
        val firebaseUser = auth.currentUser
        if (firebaseUser == null) {
            Toast.makeText(context, "Bạn chưa đăng nhập", Toast.LENGTH_SHORT).show()
        } else {
            val hashMap = HashMap<String, Any>()
            hashMap["truyenid"] = truyenid
            hashMap["timestamp"] = System.currentTimeMillis()
            //them vao db
            dbRef = FirebaseDatabase.getInstance().getReference("Users")
            dbRef.child(firebaseUser!!.uid).child("TheoDoi").child(truyenid).setValue(hashMap)
                .addOnSuccessListener {
                    Toast.makeText(context, "Đã theo dõi truyện", Toast.LENGTH_SHORT).show()
                } .addOnFailureListener {
                    Toast.makeText(context, "Lỗi: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    fun huyTheoDoiTruyen (context: Context, truyenid:String) {
        var dbRef: DatabaseReference
        var auth = FirebaseAuth.getInstance()
        val firebaseUser = auth.currentUser
        if (firebaseUser == null) {
            Toast.makeText(context, "Bạn chưa đăng nhập", Toast.LENGTH_SHORT).show()
        } else {
            dbRef = FirebaseDatabase.getInstance().getReference("Users")
            dbRef.child(firebaseUser!!.uid).child("TheoDoi").child(truyenid).removeValue()
                .addOnSuccessListener {
                    Toast.makeText(context, "Đã hủy theo dõi truyện", Toast.LENGTH_SHORT).show()
                } .addOnFailureListener {
                    Toast.makeText(context, "Lỗi: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}