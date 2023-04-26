package com.manga.m2ng2.Activities

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.PopupMenu
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.manga.m2ng2.R
import com.manga.m2ng2.databinding.ActivityProfileBinding
import com.manga.m2ng2.tools.Helper

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var dbRef: DatabaseReference
    private lateinit var storageReference: StorageReference
    private var ImageUri: Uri? = null
    private var TAG = "PROFILE_TAG"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        loadThongTinUser()
        binding.btnBack.setOnClickListener {
            finish()
        }
        binding.btnEditProfile.setOnClickListener {
            showEditText()
        }
        binding.btnCancelProfile.setOnClickListener {
            cancelEditText()
        }
        binding.btnSaveProfile.setOnClickListener {
            saveEditText()
        }
        binding.imgProfile.setOnClickListener {
            showPopupMenu()
        }
        binding.btnLuuImage.setOnClickListener {
            uploadImage()
        }
    }

    private fun uploadImage() {
        if (ImageUri == null) {
            Toast.makeText(this, "Chưa chọn ảnh", Toast.LENGTH_SHORT).show()
            uploadProfile("")
        } else {
            Log.d(TAG, "uploadImage: ${ImageUri.toString()}")
            val filePathAndName = "Profile_Images/${auth.currentUser?.uid}"
            storageReference = FirebaseStorage.getInstance().reference.child(filePathAndName)

            // Delete old profile image
            storageReference.delete().addOnSuccessListener {
                // Upload new profile image
                storageReference.putFile(ImageUri!!).addOnSuccessListener {
                    Log.d(TAG, "uploadImage: ${it.metadata?.reference?.downloadUrl}")
                    val uriTask: Task<Uri> = it.storage.downloadUrl
                    while (!uriTask.isSuccessful);
                    val downloadUrl = uriTask.result
                    Log.d(TAG, "uploadImage: $downloadUrl")
                    uploadProfile(downloadUrl.toString())
                }.addOnFailureListener {
                    Toast.makeText(this, "Lỗi: ${it.message}", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                // If there is no old profile image, upload the new one directly
                storageReference.putFile(ImageUri!!).addOnSuccessListener {
                    Log.d(TAG, "uploadImage: ${it.metadata?.reference?.downloadUrl}")
                    val uriTask: Task<Uri> = it.storage.downloadUrl
                    while (!uriTask.isSuccessful);
                    val downloadUrl = uriTask.result
                    Log.d(TAG, "uploadImage: $downloadUrl")
                    uploadProfile(downloadUrl.toString())
                }.addOnFailureListener {
                    Toast.makeText(this, "Lỗi: ${it.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun uploadProfile(imageUrl: String) {
        if(imageUrl.isEmpty()) {
            dbRef = FirebaseDatabase.getInstance().getReference("Users")
            dbRef.child(auth.uid!!).child("profileImage").setValue("")
            binding.imgProfile.setImageResource(R.drawable.baseline_person_24)
            binding.btnLuuImage.visibility = View.GONE
            binding.btnEditProfile.visibility = View.VISIBLE
        } else {
            dbRef = FirebaseDatabase.getInstance().getReference("Users")
            dbRef.child(auth.uid!!).child("profileImage").setValue(imageUrl)
            Glide.with(this).load(imageUrl).into(binding.imgProfile)
            binding.btnLuuImage.visibility = View.GONE
            binding.btnEditProfile.visibility = View.VISIBLE
        }
    }

    private fun showPopupMenu() {
        binding.btnEditProfile.visibility = View.GONE
        binding.btnLuuImage.visibility = View.VISIBLE
        val popupMenu = PopupMenu(this, binding.imgProfile)
        popupMenu.menu.add(Menu.NONE, 0, 0, "Camera")
        popupMenu.menu.add(Menu.NONE, 1, 1, "Thư viện")
        popupMenu.show()

        //handle menu item click
        popupMenu.setOnMenuItemClickListener {
            if(it.itemId == 0) {
                openCamera()
            } else if(it.itemId == 1) {
                openGallery()
            }
            false
        }
    }

    private fun openCamera() {
        var values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture") // Title
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera") // Description
        ImageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, ImageUri)
        cameraLauncher.launch(cameraIntent)
    }

    private fun openGallery() {
        //open gallery
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(galleryIntent)
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Handle the result
            Log.d(TAG, "onActivityResult: "+ImageUri)
            val data: Intent? = result.data //no need if you don't need the data
            binding.imgProfile.setImageURI(ImageUri)
        } else {
            Toast.makeText(this, "Không thể mở camera", Toast.LENGTH_SHORT).show()
        }
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Handle the result
            Log.d(TAG, "onActivityResult: "+ImageUri)
            val data: Intent? = result.data
            ImageUri = data?.data
            binding.imgProfile.setImageURI(ImageUri)
        } else {
            Toast.makeText(this, "Không thể mở thư viện", Toast.LENGTH_SHORT).show()
        }
    }


    private fun saveEditText() {
        val name = binding.edtName.text.toString()
        if (name.isEmpty()) {
            binding.edtName.error = "Không được để trống"
            return
        }
        dbRef = FirebaseDatabase.getInstance().getReference("Users")
        dbRef.child(auth.uid!!).child("name").setValue(name)
        binding.tvName.text = name
        hideEditText()
    }

    private fun hideEditText() {
        binding.edtName.visibility = View.GONE
        binding.tvName.visibility = View.VISIBLE
        binding.llEditProfile.visibility = View.GONE
        binding.btnEditProfile.visibility = View.VISIBLE
        val params = binding.tvMail.layoutParams as RelativeLayout.LayoutParams
        params.addRule(RelativeLayout.BELOW, R.id.tvName)
    }

    private fun cancelEditText() {
        hideEditText()
        binding.edtName.setText(binding.tvName.text)
    }

    private fun showEditText() {
        binding.edtName.visibility = View.VISIBLE
        binding.tvName.visibility = View.GONE
        binding.llEditProfile.visibility = View.VISIBLE
        binding.btnEditProfile.visibility = View.GONE
        val params = binding.tvMail.layoutParams as RelativeLayout.LayoutParams
        params.addRule(RelativeLayout.BELOW, R.id.edtName)
    }

    private fun loadThongTinUser() {
        Log.d(TAG, "loadThongTinUser: ${auth.uid}")
        dbRef = FirebaseDatabase.getInstance().getReference("Users")
        dbRef.child(auth.uid!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d(TAG, "onDataChange: ${snapshot.value}")
                val email = snapshot.child("email").value.toString()
                val name = snapshot.child("name").value.toString()
                val profileImage = snapshot.child("profileImage").value.toString()
                val timestamp = snapshot.child("timestamp").value.toString()
                val uid = snapshot.child("uid").value.toString()
                val userType = snapshot.child("userType").value.toString()
                val formatDate = Helper().formatNgayGio(timestamp.toLong())

                binding.tvMail.text = email
                binding.tvName.text = name
                binding.tvMemberSince.text = formatDate
                binding.tvAccount.text = userType

                //set Image using glide
                Glide.with(this@ProfileActivity)
                    .load(profileImage)
                    .placeholder(R.drawable.baseline_person_24)
                    .into(binding.imgProfile)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "onCancelled: ${error.message}")
            }
        })
    }
}