package com.manga.m2ng2.Activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.manga.m2ng2.databinding.ActivityThemTruyenBinding
import com.manga.m2ng2.model.TheLoaiModel

class ThemTruyenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityThemTruyenBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var dbRef: DatabaseReference
    private lateinit var ds: ArrayList<TheLoaiModel>
    private lateinit var storageReference: StorageReference
    private val TAG = "ThemANHActivity"
    private var hashMap: HashMap<String, Any> = HashMap()
    private var anhUri: Uri? = null
    private var selectedTheLoaiId: String? = null
    private var timestamp = System.currentTimeMillis()
    private var timeString = timestamp.toString()
    private var title = ""
    private var desc = ""
    private var theLoai = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityThemTruyenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init firebase auth
        auth = FirebaseAuth.getInstance()
        loadTheLoai()

        binding.attachBtn.setOnClickListener {
            chonAnh()
        }
        binding.theLoaiTV.setOnClickListener {
            theLoaiPickDialog()
        }
        binding.btnThemPDF.setOnClickListener {
            validateData()
        }
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun validateData() {
        Log.d(TAG, "validateData: BẮT ĐẦU VALIDATE DATA")
        title = binding.edtTruyenName.text.toString().trim()
        desc = binding.edtTruyenDescription.text.toString().trim()
        theLoai = binding.theLoaiTV.text.toString().trim()
        if(title.isEmpty()){
            binding.edtTruyenName.error = "Tên truyện không được để trống"
            return
        } else if(desc.isEmpty()){
            binding.edtTruyenDescription.error = "Mô tả không được để trống"
            return
        } else if(theLoai.isEmpty()){
            binding.theLoaiTV.error = "Thể loại không được để trống"
            return
        } else if(anhUri == null){
            Toast.makeText(this, "Vui lòng chọn file ẢNH", Toast.LENGTH_SHORT).show()
            return
        } else {
            uploadAnh()
        }
    }

    private fun uploadAnh() {
        Log.d(TAG, "uploadAnh: BẮT ĐẦU UPLOAD ẢNH")
        var filePathAndName = "Truyen/$timestamp"
        storageReference = FirebaseStorage.getInstance().getReference(filePathAndName)
        storageReference.putFile(anhUri!!).addOnSuccessListener {
            Log.d(TAG, "onSuccess: ẢNH ĐÃ ĐƯỢC UPLOAD")
            val uriTask = it.storage.downloadUrl
            while (!uriTask.isSuccessful);
            val downloadUri = uriTask.result
            uploadAnhToDb(downloadUri, timestamp)
        }
    }

    private fun uploadAnhToDb(downloadUri: Uri?, timestamp: Long) {
        Log.d(TAG, "uploadAnhToDb: BẮT ĐẦU UPLOAD ẢNH VÀO DB")
        var uid = auth.uid
        hashMap["uid"] = uid.toString()
        hashMap["id"] = timeString
        hashMap["title"] = title
        hashMap["desc"] = desc
        hashMap["theLoaiId"] = selectedTheLoaiId!! // Sử dụng giá trị của selectedTheLoaiId
        hashMap["imageUrl"] = downloadUri.toString()
        hashMap["timestamp"] = timestamp

        //db>truyens
        dbRef = FirebaseDatabase.getInstance().getReference("Truyen")
        dbRef.child(timestamp.toString()).setValue(hashMap)
            .addOnSuccessListener {
                Log.d(TAG, "onSuccess: Ảnh ĐÃ ĐƯỢC UPLOAD")
                Toast.makeText(this, "Ảnh ĐÃ ĐƯỢC UPLOAD", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Log.d(TAG, "onFailure: ${it.message}")
                Toast.makeText(this, "Lỗi: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadTheLoai() {
        Log.d(TAG, "loadTheLoai: BẮT ĐẦU LOAD THỂ LOẠI")
        ds = arrayListOf<TheLoaiModel>()
        //db>theLoai
        dbRef = FirebaseDatabase.getInstance().getReference("TheLoai")
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                ds.clear()
                for (data in snapshot.children) {
                    val theLoai = data.getValue(TheLoaiModel::class.java)
                    val theloaiId = data.key
                    if (theLoai != null) {
                        theLoai.id = theloaiId.toString() // set id cho đối tượng
                        ds.add(theLoai)
                        Log.d(TAG, "onDataChange: ${theLoai.id} - ${theLoai.theLoai}")
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "onCancelled: ${error.message}")
            }
        })
    }


    private fun theLoaiPickDialog() {
        val items = ds.map { "${it.id} - ${it.theLoai}" }.toTypedArray()
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Chọn thể loại")
            .setItems(items) { _, which ->
                binding.theLoaiTV.text = items[which]
                val theLoai = ds[which]
                selectedTheLoaiId = theLoai.id // Lưu giá trị của theLoaiId được chọn vào biến selectedTheLoaiId
                hashMap["theLoaiId"] = selectedTheLoaiId!! // Lưu giá trị của theLoaiId vào hashMap
                Log.d(TAG, "theLoaiPickDialog: id = ${theLoai.id}")
            }
        val dialog = builder.create()
        dialog.show()
    }

    private fun chonAnh() {
        Log.d(TAG, "chonAnh: BẮT ĐẦU CHỌN ẢNH")
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(intent, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            anhUri = data.data
            Log.e(TAG, "onActivityResult: $anhUri")
        }   else {
            Toast.makeText(this, "Vui lòng chọn file ảnh", Toast.LENGTH_SHORT).show()
        }
    }

    private companion object {
        const val TAG = "ADD_ANH_TAG"
        const val REQUEST_CODE = 1000
    }
}
