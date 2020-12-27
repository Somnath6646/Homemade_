package com.wenull.homemade.ui.fragments

import android.os.Bundle
import android.os.CountDownTimer
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

        val auth = FirebaseAuth.getInstance()

        timer = object : CountDownTimer(2000, 1000){
            override fun onFinish() {

                if(auth.currentUser == null) {
                    val action = SplashFragmentDirections.actionSplashFragmentToSignupLoginFragment()
                    findNavController().navigate(action)
                } else {
                    val action = SplashFragmentDirections.actionSplashFragmentToHomeFragment()
                    findNavController().navigate(action)
                }

            }

            override fun onTick(millisUntilFinished: Long) {

            }
        }.start()
    }

}
