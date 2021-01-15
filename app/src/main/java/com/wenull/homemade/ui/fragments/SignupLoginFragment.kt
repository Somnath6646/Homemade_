package com.wenull.homemade.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.wenull.homemade.R
import com.wenull.homemade.databinding.FragmentSignupLoginBinding
import com.wenull.homemade.ui.viewmodel.HomemadeViewModel
import com.wenull.homemade.ui.fragments.base.BaseFragment
import com.wenull.homemade.utils.helper.Constants

class SignupLoginFragment : BaseFragment<FragmentSignupLoginBinding, HomemadeViewModel>() {

    override fun getLayout(): Int  = R.layout.fragment_signup_login

    override fun getViewModelClass(): Class<HomemadeViewModel>  = HomemadeViewModel::class.java

    fun navigateForward() {
        val action = SignupLoginFragmentDirections.actionSignupLoginFragmentToCredentialsFragment()
        findNavController().navigate(action)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.setFirebaseSourceCallback()

        viewModel.toastMessage.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled().let {
                if (it != null)
                    Toast.makeText(activity, it.toString(), Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.signInState.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled().let {
                if(it != null && it == Constants.SUCCESSFUL) {
                    navigateForward()
                }
            }
        })

        viewModel.progressBarState.observe(viewLifecycleOwner, Observer {
            if(it != null) {
                if (it) {
                    binding.progressBar.visibility = View.VISIBLE
                } else {
                    binding.progressBar.visibility = View.GONE
                }
            }
        })

   }

}
