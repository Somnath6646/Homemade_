package com.wenull.homemade.ui.fragments

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.wenull.homemade.R
import com.wenull.homemade.adapter.AvailableFoodsAdapter
import com.wenull.homemade.adapter.UserPacksAdapter
import com.wenull.homemade.databinding.FragmentPackcontentBinding
import com.wenull.homemade.databinding.FragmentSplashBinding
import com.wenull.homemade.ui.fragments.base.BaseFragment
import com.wenull.homemade.ui.viewmodel.HomemadeViewModel

class PackContentFragment : BaseFragment<FragmentPackcontentBinding, HomemadeViewModel>() {

    private lateinit var foodsAdapter: AvailableFoodsAdapter

    override fun getLayout(): Int = R.layout.fragment_packcontent

    override fun getViewModelClass(): Class<HomemadeViewModel> = HomemadeViewModel::class.java


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.lifecycleOwner = requireActivity()

        setUpRecycerView()
    }


    private fun setUpRecycerView(){
        foodsAdapter = AvailableFoodsAdapter()
        binding.recyclerViewFragmentPackContent.adapter = foodsAdapter
        binding.recyclerViewFragmentPackContent.layoutManager = LinearLayoutManager(context)
        displayAvailableFoodList()
    }

    private fun displayAvailableFoodList(){
        viewModel.packFoods.observe(viewLifecycleOwner, Observer { packFoods ->
            Log.i("MYTAG", packFoods.toString())
            foodsAdapter.setList(packFoods)
        })
    }




}