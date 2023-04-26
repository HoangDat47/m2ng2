package com.manga.m2ng2.Fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.manga.m2ng2.adapter.SlideShowAdapter
import com.manga.m2ng2.databinding.FragmentSlideShowBinding

class SlideShowFragment : Fragment() {
    private lateinit var binding: FragmentSlideShowBinding
    private lateinit var viewPager2: ViewPager2
    private lateinit var handler: Handler
    private lateinit var slideList: ArrayList<Int>
    private lateinit var adapter: SlideShowAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSlideShowBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        setupTranformer()
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                handler.removeCallbacks(slideRunnable)
                handler.postDelayed(slideRunnable, 4000)
                super.onPageSelected(position)
            }
        })
    }

    private val slideRunnable = Runnable {
        viewPager2.currentItem = viewPager2.currentItem + 1
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(slideRunnable)
    }

    override fun onResume() {
        super.onResume()
        handler.postDelayed(slideRunnable, 4000)
    }

    private fun setupTranformer() {
        val tranformer = CompositePageTransformer()
        tranformer.addTransformer(MarginPageTransformer(40))
        tranformer.addTransformer { page, position ->
            val r = 1 - Math.abs(position)
            page.scaleY = 0.85f + r * 0.15f
        }
        viewPager2.setPageTransformer(tranformer)
    }

    private fun init() {
        viewPager2 = binding.viewPager2
        handler = Handler(Looper.myLooper()!!)
        slideList = ArrayList()

        slideList.add(com.manga.m2ng2.R.drawable.slide_1)
        slideList.add(com.manga.m2ng2.R.drawable.slide_2)
        slideList.add(com.manga.m2ng2.R.drawable.slide_3)

        adapter = SlideShowAdapter(slideList, viewPager2)

        viewPager2.adapter = adapter
        viewPager2.offscreenPageLimit = 2
        viewPager2.clipToPadding = false
        viewPager2.clipChildren = false
        viewPager2.getChildAt(0).overScrollMode = View.OVER_SCROLL_NEVER
    }

}