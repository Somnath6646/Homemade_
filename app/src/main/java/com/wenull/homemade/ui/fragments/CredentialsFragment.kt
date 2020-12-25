package com.wenull.homemade.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.wenull.homemade.R
import com.wenull.homemade.databinding.FragmentCredentialsBinding
import com.wenull.homemade.databinding.FragmentSignupLoginBinding
import com.wenull.homemade.ui.activities.HomemadeActivityViewModel
import com.wenull.homemade.ui.fragments.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_credentials.*

class CredentialsFragment : BaseFragment<FragmentCredentialsBinding, HomemadeActivityViewModel>(){
    override fun getLayout(): Int  = R.layout.fragment_credentials

    override fun getViewModelClass(): Class<HomemadeActivityViewModel>  = HomemadeActivityViewModel::class.java

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.proceedBtnCredfrag.setOnClickListener {
            val action = CredentialsFragmentDirections.actionCredentialsFragmentToHomeFragment()
            findNavController().navigate(action)
        }
    }
}