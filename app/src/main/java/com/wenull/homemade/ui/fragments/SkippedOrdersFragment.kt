package com.wenull.homemade.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.wenull.homemade.R
import com.wenull.homemade.adapter.SkippedFoodsAdapter
import com.wenull.homemade.databinding.FragmentSkippedOrdersBinding
import com.wenull.homemade.ui.fragments.base.BaseFragment
import com.wenull.homemade.ui.viewmodel.HomemadeViewModel
import com.wenull.homemade.utils.model.OrderServer
import com.wenull.homemade.utils.model.OrderSkipped
import com.wenull.homemade.utils.model.UserSkippedData

class SkippedOrdersFragment : BaseFragment<FragmentSkippedOrdersBinding, HomemadeViewModel>() {

    private val auth = FirebaseAuth.getInstance()

    private lateinit var adapter: SkippedFoodsAdapter

    private var skippedMeals = ArrayList<OrderServer>()
    
    override fun getLayout(): Int = R.layout.fragment_skipped_orders

    override fun getViewModelClass(): Class<HomemadeViewModel> = HomemadeViewModel::class.java

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.setFirebaseSourceCallback()

        binding.backIconSkippedOrders.setOnClickListener {
            findNavController().popBackStack()
        }

        setUpRecyclerView()

        getSkippedMealsData()

    }

    private fun getSkippedMealsData() {
        viewModel.getUserSkippedData(auth.currentUser!!.uid)
        viewModel.userSkippedData.observe(viewLifecycleOwner, Observer { userSkippedData ->
            getSkippedFoods(userSkippedData)
        })
    }

    private fun getSkippedFoods(userSkippedData: UserSkippedData) {
        viewModel.getSkippedMeals(userSkippedData.skippedMeals)
        viewModel.skippedFoods.observe(viewLifecycleOwner, Observer { skippedFoods ->
            skippedMeals = skippedFoods
            adapter.setList(skippedMeals)
            Log.i("UserSkippedMFoods", "$skippedFoods")
        })
    }

    private fun setUpRecyclerView() {

        adapter = SkippedFoodsAdapter()
        binding.recyclerViewFragmentSkippedOrders.adapter = adapter
        binding.recyclerViewFragmentSkippedOrders.layoutManager = LinearLayoutManager(context)

    }

}
