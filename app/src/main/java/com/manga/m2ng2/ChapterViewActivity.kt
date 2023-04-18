package com.manga.m2ng2

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.manga.m2ng2.databinding.ActivityChapterViewBinding
import com.manga.m2ng2.tools.Constrains

class ChapterViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChapterViewBinding
    private lateinit var dbRef: DatabaseReference
    private lateinit var storageReference: StorageReference
    private var chapterid: String? = null
    private var pdfUrl: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChapterViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.tvChapTitle.text = intent.getStringExtra("chaptertitle")
        chapterid = intent.getStringExtra("chapterid")
        loadChapter()
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun loadChapter() {
        dbRef = FirebaseDatabase.getInstance().getReference("Chapter/${intent.getStringExtra("truyenid")}")
        dbRef.child(chapterid.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                pdfUrl = dataSnapshot.child("pdfUrl").value.toString()
                loadChapterFromFirebase(pdfUrl.toString())
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Xử lý lỗi ở đây
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun loadChapterFromFirebase(pdfUrl: String) {
        storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl)
        storageReference.getBytes(Constrains.MAX_BYTES_PDF.toLong()).addOnSuccessListener {
            binding.pdfView.fromBytes(it).swipeHorizontal(false) //set false để đọc theo chiều dọc
                .onPageChange { page, pageCount ->
                    val currentPage = page + 1
                    binding.tvChapSubtitle.text = "Trang $currentPage/$pageCount"
                } .onError { t ->
                    Toast.makeText(this, ""+t.message, Toast.LENGTH_SHORT).show()
                } .onPageError { page, t ->
                    Toast.makeText(this, "Lỗi ở trang "+page +" "+t.message, Toast.LENGTH_SHORT).show()
                } .load()
            binding.progressBar.visibility = View.GONE
        } .addOnFailureListener {
            binding.progressBar.visibility = View.GONE
            Toast.makeText(this, "Lỗi tải truyện", Toast.LENGTH_SHORT).show()
        }
    }
}