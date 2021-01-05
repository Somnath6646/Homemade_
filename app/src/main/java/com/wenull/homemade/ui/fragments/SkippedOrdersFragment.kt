package com.wenull.homemade.ui.fragments

import android.os.Bundle
import com.wenull.homemade.R
import com.wenull.homemade.databinding.FragmentSkippedOrdersBinding
import com.wenull.homemade.ui.fragments.base.BaseFragment
import com.wenull.homemade.ui.viewmodel.HomemadeViewModel

class SkippedOrdersFragment : BaseFragment<FragmentSkippedOrdersBinding, HomemadeViewModel>() {
    
    override fun getLayout(): Int = R.layout.fragment_skipped_orders

    override fun getViewModelClass(): Class<HomemadeViewModel> = HomemadeViewModel::class.java

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

    }
}