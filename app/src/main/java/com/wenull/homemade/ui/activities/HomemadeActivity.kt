package com.wenull.homemade.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBinderMapper
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wenull.homemade.R
import com.wenull.homemade.databinding.ActivityHomemadeBinding

class HomemadeActivity : AppCompatActivity() {
    private lateinit var viewModel: HomemadeActivityViewModel
    private lateinit var binding: ActivityHomemadeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_homemade)

        viewModel = ViewModelProvider(this).get(HomemadeActivityViewModel::class.java)
    }
}