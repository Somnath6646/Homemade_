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

class HomemadeActivity : AppCompatActivity() {

    private lateinit var viewModel: HomemadeViewModel
    private lateinit var binding: ActivityHomemadeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_homemade)

        val repository = HomemadeRepository(FirebaseSource(this))
        val factory = HomemadeViewModelFactory(repository)

        viewModel = ViewModelProvider(this, factory).get(HomemadeViewModel::class.java)

    }

}
