package com.manga.m2ng2.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.manga.m2ng2.databinding.FragmentTrangChuBinding

class TrangChuFragment : Fragment() {
    private lateinit var binding: FragmentTrangChuBinding
    private val fragment_slide_show: SlideShowFragment = SlideShowFragment()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTrangChuBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadSlideView()
    }

    private fun loadSlideView() {
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(binding.frameSlideShow.id, fragment_slide_show)
        transaction.commit()
    }
}