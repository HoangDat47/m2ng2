package com.manga.m2ng2.tools

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.manga.m2ng2.Activities.ThemTruyenActivity
import com.manga.m2ng2.Activities.TruyenDetailActivity
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

    fun theoDoiTruyen(context: Context, truyenid: String) {
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
                }.addOnFailureListener {
                    Toast.makeText(context, "Lỗi: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    fun huyTheoDoiTruyen(context: Context, truyenid: String) {
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
                }.addOnFailureListener {
                    Toast.makeText(context, "Lỗi: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    fun themBinhLuan(truyenDetailActivity: TruyenDetailActivity, truyenid: String, comment: String) {
        var dbRef: DatabaseReference
        val auth = FirebaseAuth.getInstance()
        val firebaseUser = auth.currentUser
        if (firebaseUser == null) {
            Toast.makeText(truyenDetailActivity, "Bạn chưa đăng nhập", Toast.LENGTH_SHORT).show()
        } else {
            //lay thong tin user hien tai
            var userRef = FirebaseDatabase.getInstance().getReference("Users")
            //lay ten user tu userref
            userRef.child(firebaseUser.uid).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val username = snapshot.child("name").value.toString()
                    val useravatar = snapshot.child("profileImage").value.toString()
                    //them vao db
                    val hashMap = HashMap<String, Any>()
                    hashMap["id"] = System.currentTimeMillis().toString()
                    hashMap["truyenid"] = truyenid
                    hashMap["comment"] = comment
                    hashMap["timestamp"] = System.currentTimeMillis()
                    hashMap["uid"] = firebaseUser.uid
                    hashMap["name"] = username
                    hashMap["profileImage"] = useravatar
                    dbRef = FirebaseDatabase.getInstance().getReference("Truyen/${truyenid}/BinhLuan")
                    dbRef.child(System.currentTimeMillis().toString()).setValue(hashMap)
                        .addOnSuccessListener {
                            Toast.makeText(truyenDetailActivity, "Đã thêm bình luận", Toast.LENGTH_SHORT).show()
                        }.addOnFailureListener {
                            Toast.makeText(truyenDetailActivity, "Lỗi: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(truyenDetailActivity, "Lỗi: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    fun xoaTruyen(truyenDetailActivity: TruyenDetailActivity, truyenid: String) {
        val truyenRef: DatabaseReference
        val auth = FirebaseAuth.getInstance()
        val firebaseUser = auth.currentUser
        val filePathAndName = "Truyen/${truyenid}/"
        if (firebaseUser != null && Constrains.userRole == "admin") {
            truyenRef = FirebaseDatabase.getInstance().getReference("Chapter")
            truyenRef.child(truyenid).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        Toast.makeText(truyenDetailActivity, "Truyện này có chương, không thể xóa", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        //xoa truyen
                        FirebaseDatabase.getInstance().getReference("Truyen").child(truyenid).removeValue()
                        val storageReference = FirebaseStorage.getInstance().reference.child(filePathAndName)
                        storageReference.delete().addOnSuccessListener {
                            Toast.makeText(truyenDetailActivity, "Đã xóa truyện", Toast.LENGTH_SHORT).show()
                            truyenDetailActivity.finish()
                        }.addOnFailureListener {
                            Toast.makeText(truyenDetailActivity, "Lỗi: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(truyenDetailActivity, "Lỗi: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(truyenDetailActivity, "Bạn chưa đăng nhập", Toast.LENGTH_SHORT).show()
        }
    }
}