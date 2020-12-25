package com.wenull.homemade.ui.fragments

import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.wenull.homemade.R
import com.wenull.homemade.databinding.FragmentSplashBinding
import com.wenull.homemade.ui.activities.HomemadeActivityViewModel
import com.wenull.homemade.ui.fragments.base.BaseFragment

class SplashFragment : BaseFragment<FragmentSplashBinding, HomemadeActivityViewModel>() {
    override fun getLayout(): Int = R.layout.fragment_splash

    override fun getViewModelClass(): Class<HomemadeActivityViewModel> = HomemadeActivityViewModel::class.java

    private lateinit var timer: CountDownTimer

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        timer = object : CountDownTimer(2000, 1000){
            override fun onFinish() {

                val action = SplashFragmentDirections.actionSplashFragmentToSignupLoginFragment()
                findNavController().navigate(action)
            }

            override fun onTick(millisUntilFinished: Long) {

            }
        }.start()
    }

}