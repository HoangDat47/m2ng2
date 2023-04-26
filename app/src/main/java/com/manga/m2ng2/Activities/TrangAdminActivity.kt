package com.manga.m2ng2.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.manga.m2ng2.adapter.ViewPageAdapter
import com.manga.m2ng2.databinding.ActivityTrangAdminBinding
import com.manga.m2ng2.R

class TrangAdminActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTrangAdminBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrangAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupTabLayout()

        //init firebase auth
        auth = FirebaseAuth.getInstance()
    }

    private fun setupTabLayout() {
        val vpAdapter = ViewPageAdapter(supportFragmentManager, lifecycle)
        binding.viewPager.adapter = vpAdapter   //set adapter cho viewpager
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            /*when (position) {
                0 -> tab.text = "Trang chủ"
                1 -> tab.text = "Thể loại"
                2 -> tab.text = "Theo dõi"
                3 -> tab.text = "User"
            }*/
            tab.icon = ContextCompat.getDrawable(this, getTabIcon(position))
        }.attach()
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                binding.viewPager.currentItem = tab?.position ?: 0
                binding.viewPager.isUserInputEnabled = false
                binding.viewPager.postDelayed({
                    binding.viewPager.isUserInputEnabled = true
                }, 300)

                // Thêm dòng này để tắt hoạt ảnh smoothScroll
                binding.viewPager.setCurrentItem(tab?.position ?: 0, false)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // ...
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // ...
            }
        })
    }

    private fun getTabIcon(position: Int): Int {
        return when (position) {
            0 -> {
                R.drawable.tab_trang_chu
            }
            1 -> {
                R.drawable.tab_the_loai
            }
            2 -> {
                R.drawable.tab_theo_doi
            }
            3 -> {
                R.drawable.tab_user
            }
            else -> {
                R.drawable.ic_launcher_background
            }
        }
    }
}