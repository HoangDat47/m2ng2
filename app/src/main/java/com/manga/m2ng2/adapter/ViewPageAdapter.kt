package com.manga.m2ng2.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.firebase.auth.FirebaseAuth
import com.manga.m2ng2.Fragment.TheLoaiFragment
import com.manga.m2ng2.Fragment.TheoDoiFragment
import com.manga.m2ng2.Fragment.TrangChuFragment
import com.manga.m2ng2.Fragment.UserFragment

class ViewPageAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        val auth = FirebaseAuth.getInstance().currentUser
        return when (position) {
            0 -> {
                TrangChuFragment()
            }
            1 -> {
                TheLoaiFragment()
            }
            2 -> {
                TheoDoiFragment()
            }
            3 -> {
                if (auth != null) {
                    UserFragment()
                } else {
                    TrangChuFragment()
                }
            }
            else -> {
                TrangChuFragment()
            }
        }
    }
}

