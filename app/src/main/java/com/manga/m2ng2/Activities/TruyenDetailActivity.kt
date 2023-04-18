package com.manga.m2ng2.Activities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.manga.m2ng2.R
import com.manga.m2ng2.adapter.ChapterAdapter
import com.manga.m2ng2.databinding.ActivityTruyenDetailBinding
import com.manga.m2ng2.model.ChapterModel
import com.manga.m2ng2.tools.Constrains
import com.manga.m2ng2.tools.Helper
import com.squareup.picasso.Picasso

class TruyenDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTruyenDetailBinding
    private lateinit var ds2: ArrayList<ChapterModel>
    private lateinit var adapter: ChapterAdapter
    private lateinit var dbRef: DatabaseReference
    private lateinit var storageReference: StorageReference
    private lateinit var auth: FirebaseAuth
    private var hashMap: HashMap<String, Any> = HashMap()
    private var timestamp = System.currentTimeMillis()
    private var timeString = timestamp.toString()
    private var truyenid: String? = null
    private var tenchapter = ""
    private var pdfUri: Uri? = null
    private var isInMyFav = false
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTruyenDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        val firebaseUser = auth.currentUser
        if(firebaseUser != null) {
            kiemTraTheoDoi()
        }
        setValues()
        intent.extras?.let {
            truyenid = it.getString("truyenid")
        }
        loadChapterList()
        if (Constrains.userRole == "admin") {
            binding.btnThemChapter.setOnClickListener {
                openThemChapterDialog(
                    truyenid.toString(),
                    intent.getStringExtra("truyentitle").toString()
                )
            }
        } else {
            binding.btnThemChapter.visibility = View.GONE
        }
        binding.btnBack.setOnClickListener {
            finish()
        }
        binding.btnTheoDoi.setOnClickListener {
            if (firebaseUser == null) {
                Toast.makeText(this, "Bạn cần đăng nhập để theo dõi truyện", Toast.LENGTH_SHORT).show()
            } else {
                if(isInMyFav) {
                    //remove from fav
                    Helper().huyTheoDoiTruyen(this, truyenid!!)
                } else {
                    //add to fav
                    Helper().theoDoiTruyen(this, truyenid!!)
                }
            }
        }
    }

    private fun kiemTraTheoDoi() {
        dbRef = FirebaseDatabase.getInstance().getReference("Users")
        dbRef.child(auth.currentUser!!.uid).child("TheoDoi").addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {
                isInMyFav = snapshot.child(truyenid!!).exists()
                if (isInMyFav) {
                    binding.btnTheoDoi.text = "Huỷ Theo Dõi"
                    binding.btnTheoDoi.setTextColor(Color.parseColor("#FFFFFF"))
                    binding.btnTheoDoi.setBackgroundColor(Color.parseColor("#FF0000"))
                    binding.btnTheoDoi.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_heart_broken_24, 0, 0, 0)
                } else {
                    binding.btnTheoDoi.text = "Theo Dõi"
                    binding.btnTheoDoi.setBackgroundColor(Color.parseColor("#8BC34A"))
                    binding.btnTheoDoi.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_favorite_24, 0, 0, 0)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@TruyenDetailActivity, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadChapterList() {
        binding.rvListChapter.layoutManager = LinearLayoutManager(this)
        ds2 = arrayListOf<ChapterModel>()
        dbRef = FirebaseDatabase.getInstance().getReference("Chapter/$truyenid")
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                ds2.clear()
                for (data in snapshot.children) {
                    val chapter = data.getValue(ChapterModel::class.java)
                    if (chapter?.truyenId == truyenid) {
                        ds2.add(chapter!!)
                    }
                }
                adapter = ChapterAdapter(ds2)
                binding.rvListChapter.adapter = adapter

                //lang nghe su kien click item recyclerview
                adapter.setOnItemClickListener(object : ChapterAdapter.OnItemClickListener {
                    override fun onItemClick(position: Int) {
                        val intent = Intent(this@TruyenDetailActivity, ChapterViewActivity::class.java)
                        intent.putExtra("chapterid", ds2[position].id)
                        intent.putExtra("truyenid", ds2[position].truyenId)
                        intent.putExtra("chaptertitle", ds2[position].title)
                        startActivity(intent)
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@TruyenDetailActivity, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun openThemChapterDialog(truyenid: String, truyentitle: String) {
        val mDialog = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val mDialogView = inflater.inflate(R.layout.them_chapter, null)
        mDialog.setView(mDialogView)
        //update id truyen to dialog
        val edtTruyenCha = mDialogView.findViewById<EditText>(R.id.edtTruyenCha)
        val btn_themChapter = mDialogView.findViewById<View>(R.id.btn_themChapter)
        val edtChapterName = mDialogView.findViewById<EditText>(R.id.edtChapterName)
        val chapterPDF = mDialogView.findViewById<ImageButton>(R.id.chapterPDF)
        truyentitle.let { edtTruyenCha.setText(it) }
        mDialog.setTitle("Thêm Chapter")
        val alertDialog = mDialog.create()
        alertDialog.show()
        //click button
        chapterPDF.setOnClickListener {
            chonPDF()
        }
        btn_themChapter.setOnClickListener {
            Log.d(TAG, "validateData: BẮT ĐẦU VALIDATE DATA")
            tenchapter = edtChapterName.text.toString().trim()
            if (tenchapter.isEmpty()) {
                edtChapterName.error = "Tên chapter không được để trống"
                return@setOnClickListener
            } else if (pdfUri == null) {
                Toast.makeText(this, "Vui lòng chọn file PDF", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                Log.d(TAG, "uploadPDF: BẮT ĐẦU UPLOAD PDF")
                var filePathAndName = "Chapter/$truyenid/$timeString"
                storageReference = FirebaseStorage.getInstance().getReference(filePathAndName)
                storageReference.putFile(pdfUri!!).addOnSuccessListener {
                    Log.d(TAG, "onSuccess: PDF ĐÃ ĐƯỢC UPLOAD")
                    val uriTask = it.storage.downloadUrl
                    while (!uriTask.isSuccessful);
                    val downloadUri = uriTask.result
                    uploadPDFToDb(downloadUri, timestamp, tenchapter)
                }
                alertDialog.dismiss()
            }
        }
    }

    private fun chonPDF() {
        Log.d(TAG, "chonPDF: BẮT ĐẦU CHỌN PDF")
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "application/pdf"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(
            Intent.createChooser(intent, "Chọn file PDF"),
            100
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            pdfUri = data.data
            Log.d(TAG, "onActivityResult: PDF URI: $pdfUri")
        } else {
            Toast.makeText(this, "Vui lòng chọn file PDF", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadPDFToDb(downloadUri: Uri?, timestamp: Long, tenchapter: String) {
        Log.d(TAG, "uploadPDFToDb: BẮT ĐẦU UPLOAD PDF TO DB")
        var uid = auth.uid
        hashMap["uid"] = uid.toString()
        hashMap["id"] = timeString
        hashMap["title"] = tenchapter
        hashMap["truyenId"] = this.truyenid.toString()
        hashMap["pdfUrl"] = downloadUri.toString()
        hashMap["timestamp"] = timestamp
        //db>Chapter>truyenId>timeString
        dbRef = FirebaseDatabase.getInstance().getReference("Chapter/$truyenid")
        dbRef.child(timestamp.toString()).setValue(hashMap)
            .addOnSuccessListener {
                Log.d(TAG, "onSuccess: PDF ĐÃ ĐƯỢC UPLOAD TO DB")
                Toast.makeText(this, "Thêm chapter thành công", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Log.d(TAG, "onFailure: PDF KHÔNG ĐƯỢC UPLOAD TO DB")
                Toast.makeText(this, "Thêm chapter thất bại", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setValues() {
        binding.tvTenTruyen.text = intent.getStringExtra("truyentitle")
        binding.tvDate.text = intent.getStringExtra("truyenDate")
        binding.tvDesc.text = intent.getStringExtra("truyendesc")
        if (intent.getStringExtra("imageUrl") != null) {
            Picasso.get().load(intent.getStringExtra("imageUrl")).into(binding.imgTruyen)
        } else {
            Log.e("TruyenDetailActivity", "setValues: imageUrl is null")
            binding.imgTruyen.setImageResource(R.drawable.baseline_more_vert_24)
        }
    }
}

