package com.wenull.homemade.ui.fragments.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wenull.homemade.data.remote.FirebaseSource
import com.wenull.homemade.repositories.HomemadeRepository
import com.wenull.homemade.ui.viewmodel.HomemadeViewModelFactory

abstract class BaseFragment<VB: ViewDataBinding, VM: ViewModel> : Fragment(){

    protected lateinit var binding: VB
    protected lateinit var viewModel: VM

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, getLayout(), container, false)

        val repository = HomemadeRepository(FirebaseSource(requireActivity()))

        val factory = HomemadeViewModelFactory(repository)

        viewModel = ViewModelProvider(this, factory).get(getViewModelClass())

        return binding.root
    }

    abstract fun getLayout(): Int
    abstract fun getViewModelClass(): Class<VM>
}