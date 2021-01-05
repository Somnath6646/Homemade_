package com.wenull.homemade.ui.fragments

import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.wenull.homemade.R
import com.wenull.homemade.databinding.FragmentSplashBinding
import com.wenull.homemade.ui.viewmodel.HomemadeViewModel
import com.wenull.homemade.ui.fragments.base.BaseFragment

class SplashFragment : BaseFragment<FragmentSplashBinding, HomemadeViewModel>() {

    override fun getLayout(): Int = R.layout.fragment_splash

    override fun getViewModelClass(): Class<HomemadeViewModel> = HomemadeViewModel::class.java

    private lateinit var timer: CountDownTimer

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.setFirebaseSourceCallback()

        viewModel.toastMessage.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled().let {
                if (it != null)
                    Toast.makeText(activity, it.toString(), Toast.LENGTH_SHORT).show()
            }
        })

        val auth = FirebaseAuth.getInstance()

        timer = object : CountDownTimer(2000, 1000) {

            override fun onFinish() {
                navigateToSignUpOrLogInFragment()
            }

            override fun onTick(millisUntilFinished: Long) {}

        }

        if(auth.currentUser != null) {
            viewModel.checkIfUserExists(auth.currentUser!!.uid)
        } else {
            timer.start()
        }

        viewModel.userExists.observe(viewLifecycleOwner, Observer { event ->
            if (event != null) {
                event.getContentIfNotHandled().let { exists ->
                    if(exists != null) {
                        if (exists) {
                            navigateToHomeFragment()
                        } else {
                            navigateToCredentialsFragment()
                        }
                    }
                }
            }
        })

    }

    private fun navigateToHomeFragment() {
        val action = SplashFragmentDirections.actionSplashFragmentToHomeFragment()
        findNavController().navigate(action)
    }

    private fun navigateToCredentialsFragment() {
        findNavController().navigate(R.id.action_splashFragment_to_credentialsFragment)
    }

    private fun navigateToSignUpOrLogInFragment() {
        val action = SplashFragmentDirections.actionSplashFragmentToSignupLoginFragment()
        findNavController().navigate(action)
    }

}
