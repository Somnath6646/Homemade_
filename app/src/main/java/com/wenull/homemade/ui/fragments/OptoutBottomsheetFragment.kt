package com.wenull.homemade.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.wenull.homemade.R
import com.wenull.homemade.databinding.FragmentOptoutBottomsheetBinding

class OptoutBottomsheetFragment : BottomSheetDialogFragment(){
    private lateinit var binding: FragmentOptoutBottomsheetBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_optout_bottomsheet, container, false)
        return binding.root
    }
}