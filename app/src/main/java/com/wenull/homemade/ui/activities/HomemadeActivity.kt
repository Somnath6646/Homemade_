package com.wenull.homemade.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.wenull.homemade.R
import com.wenull.homemade.data.remote.FirebaseSource
import com.wenull.homemade.databinding.ActivityHomemadeBinding
import com.wenull.homemade.repositories.HomemadeRepository
import com.wenull.homemade.ui.viewmodel.HomemadeViewModel
import com.wenull.homemade.ui.viewmodel.HomemadeViewModelFactory
import com.wenull.homemade.utils.helper.FragmentActions

class HomemadeActivity : AppCompatActivity() {

    private lateinit var viewModel: HomemadeViewModel
    private lateinit var binding: ActivityHomemadeBinding

    private lateinit var fragmentAction: FragmentActions

    fun setAction(fragmentActions: FragmentActions){
        this.fragmentAction = fragmentActions
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_homemade)

        val repository = HomemadeRepository(FirebaseSource(this))
        val factory = HomemadeViewModelFactory(repository)

        viewModel = ViewModelProvider(this, factory).get(HomemadeViewModel::class.java)

    }

    override fun onBackPressed() {
        if(fragmentAction.getDrawerState() == 0.0f){
            super.onBackPressed()
        }else {
            fragmentAction.onBackPressed()
        }
    }

}
