package com.manga.m2ng2.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.manga.m2ng2.Fragment.TheLoaiFragment
import com.manga.m2ng2.Fragment.UserFragment

class ViewPageAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle)
    : FragmentStateAdapter(fragmentManager, lifecycle){
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> {
                TheLoaiFragment()
            }
            1 -> {
                UserFragment()
            }
            else -> {
                TheLoaiFragment()
            }
        }
    }
}
