package com.wenull.homemade.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wenull.homemade.R
import com.wenull.homemade.databinding.FragmentHomeBinding
import com.wenull.homemade.databinding.FragmentSplashBinding
import com.wenull.homemade.ui.activities.HomemadeActivityViewModel
import com.wenull.homemade.ui.fragments.base.BaseFragment

class HomeFragment : BaseFragment<FragmentHomeBinding, HomemadeActivityViewModel>() {
    override fun getLayout(): Int = R.layout.fragment_home

    override fun getViewModelClass(): Class<HomemadeActivityViewModel> = HomemadeActivityViewModel::class.java

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

}