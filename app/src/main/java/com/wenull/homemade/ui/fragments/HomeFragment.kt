package com.wenull.homemade.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.wenull.homemade.R
import com.wenull.homemade.adapter.AvailableFoodsAdapter
import com.wenull.homemade.adapter.AvailablePacksAdapter
import com.wenull.homemade.databinding.FragmentHomeBinding
import com.wenull.homemade.ui.viewmodel.HomemadeViewModel
import com.wenull.homemade.ui.fragments.base.BaseFragment
import com.wenull.homemade.utils.helper.Constants
import com.wenull.homemade.utils.model.FoodPack
import com.wenull.homemade.utils.model.OrderServer
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : BaseFragment<FragmentHomeBinding, HomemadeViewModel>() {

    private lateinit var packsAdapter: AvailablePacksAdapter
    private lateinit var foodsAdapter: AvailableFoodsAdapter

    private val auth = FirebaseAuth.getInstance()

    override fun getLayout(): Int = R.layout.fragment_home

    override fun getViewModelClass(): Class<HomemadeViewModel> = HomemadeViewModel::class.java

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

        binding.layoutDrawer.profileBtnSidenav.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToProfileFragment()
            findNavController().navigate(action)
        }

        fetchUserData()

    }

    private fun updateLists() {
        viewModel.fetchPackDetails()
        viewModel.packs.observe(viewLifecycleOwner, Observer { packs ->
            packsAdapter.setList(packs)
        })
        viewModel.packFoods.observe(viewLifecycleOwner, Observer { packFoods ->
            foodsAdapter.setList(packFoods)
        })
    }

    private fun fetchUserData() {

        viewModel.fetchUserData(auth.currentUser!!.uid)

        viewModel.userData.observe(viewLifecycleOwner, Observer { event ->

            if(event != null) {

                event.getContentIfNotHandled().let { user ->

                    val pack = user!!.packsEnrolled

                    if(pack.size > 0) {
                        setUserPackAndFoodRecyclerView(pack[0])
                    } else {
                        setUpRecyclerView()
                    }

                }

            }

        })

    }

    private fun setUserPackAndFoodRecyclerView(packId: Long) {

        binding.layoutContent.labelFoods.visibility = View.VISIBLE
        binding.layoutContent.labelFoods.text = Constants.YOUR_FOODS
        binding.layoutContent.recyclerViewAvailableFoods.visibility = View.VISIBLE
        binding.layoutContent.labelPacks.text = Constants.YOUR_PACKS

        packsAdapter = AvailablePacksAdapter { pack -> packOnClick(pack) }
        binding.layoutContent.recylerViewAvailablePacks.adapter = packsAdapter
        binding.layoutContent.recylerViewAvailablePacks.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        updateLists()
        // Foods adapter
        foodsAdapter = AvailableFoodsAdapter()
        binding.layoutContent.recyclerViewAvailableFoods.adapter = foodsAdapter
        binding.layoutContent.recyclerViewAvailableFoods.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        updateFoods(packId)

    }

    private fun packOnClick(pack: FoodPack) {
        //navigating to its content
        val action = HomeFragmentDirections.actionHomeFragmentToPackContentFragment()
        val bundle = bundleOf(Constants.FRAGMENT_PACK_ID to pack.id)
        findNavController().navigate(action.actionId, bundle)
    }

    private fun updateFoods(packId: Long) {
        val day = SimpleDateFormat(Constants.DAY_OF_WEEK, Locale.ENGLISH).format(System.currentTimeMillis())
        Log.i("Day of week", day)
        viewModel.fetchTodayFoodDetails(day, packId)
        viewModel.todayFood.observe(viewLifecycleOwner, Observer { food ->
            foodsAdapter.setList(mutableListOf(food) as ArrayList<OrderServer>)
        })
    }

    private fun setUpRecyclerView() {
        // Packs adapter
        packsAdapter = AvailablePacksAdapter { pack -> packOnClick(pack) }
        binding.layoutContent.recylerViewAvailablePacks.adapter = packsAdapter
        binding.layoutContent.recylerViewAvailablePacks.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        updateLists()
        // Foods adapter
        foodsAdapter = AvailableFoodsAdapter()
        binding.layoutContent.recyclerViewAvailableFoods.adapter = foodsAdapter
        binding.layoutContent.recyclerViewAvailableFoods.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

}
