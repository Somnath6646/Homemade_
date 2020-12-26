package com.wenull.homemade.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.wenull.homemade.R
import com.wenull.homemade.databinding.FragmentSignupLoginBinding
import com.wenull.homemade.ui.activities.HomemadeActivityViewModel
import com.wenull.homemade.ui.fragments.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_signup_login.*

class SignupLoginFragment : BaseFragment<FragmentSignupLoginBinding, HomemadeActivityViewModel>(){
    override fun getLayout(): Int  = R.layout.fragment_signup_login

    override fun getViewModelClass(): Class<HomemadeActivityViewModel>  = HomemadeActivityViewModel::class.java

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.btnSigninfrag.setOnClickListener {
            if (binding.btnSigninfragText.text.toString().equals("Sign In")) {
                val action =
                    SignupLoginFragmentDirections.actionSignupLoginFragmentToCredentialsFragment()
                findNavController().navigate(action)
            }else{
                binding.btnSigninfragText.text = "Sign In"
            }
        }
   }
}