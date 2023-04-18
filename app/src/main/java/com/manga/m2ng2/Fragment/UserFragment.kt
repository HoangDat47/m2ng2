package com.manga.m2ng2.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.manga.m2ng2.Activities.ProfileActivity
import com.manga.m2ng2.Activities.ThemTheLoaiActivity
import com.manga.m2ng2.Activities.ThemTruyenActivity
import com.manga.m2ng2.R
import com.manga.m2ng2.databinding.FragmentUserBinding
import com.manga.m2ng2.tools.Constrains

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [UserFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UserFragment : Fragment(R.layout.fragment_user) {
    private lateinit var binding: FragmentUserBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.profileBtn.setOnClickListener {
            startActivity(Intent(requireContext(), ProfileActivity::class.java))
        }
    }

}