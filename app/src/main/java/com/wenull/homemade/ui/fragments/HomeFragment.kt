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
import com.wenull.homemade.utils.model.User
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : BaseFragment<FragmentHomeBinding, HomemadeViewModel>() {

    private lateinit var packsAdapter: AvailablePacksAdapter
    private lateinit var foodsAdapter: AvailableFoodsAdapter

    private val auth = FirebaseAuth.getInstance()

    private lateinit var user: User

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

        checkUserPacksData()

        fetchUserData()

    }

    private fun updateLists(packIds: ArrayList<Long>) {
        viewModel.fetchPackDetails()
        viewModel.packs.observe(viewLifecycleOwner, Observer { packs ->
            packsAdapter.setList(packs, packIds)
        })
        viewModel.packFoods.observe(viewLifecycleOwner, Observer { packFoods ->
            foodsAdapter.setList(packFoods)
        })
    }

    private fun fetchUserData() {

        viewModel.fetchUserData(auth.currentUser!!.uid)

        viewModel.userData.observe(viewLifecycleOwner, Observer { event ->

            event?.getContentIfNotHandled()?.let { user ->

                this.user = user

                val packs = user.packsEnrolled

                Log.i("Packs", "$packs")

                if(packs.size > 0) {
                    setUserPackAndFoodRecyclerView(packs)
                } else {
                    setUpRecyclerView(ArrayList())
                }

            }

        })

    }

    private fun setUserPackAndFoodRecyclerView(packIds: ArrayList<Long>) {

        binding.layoutContent.labelFoods.visibility = View.VISIBLE
        binding.layoutContent.labelFoods.text = Constants.TODAYS_MEAL
        binding.layoutContent.recyclerViewAvailableFoods.visibility = View.VISIBLE
        binding.layoutContent.labelPacks.text = Constants.ALL_PACKS

        packsAdapter = AvailablePacksAdapter(
            { pack -> packOnClick(pack) },
            { newPackIds -> enrollButtonOnclick(newPackIds) }
        )
        binding.layoutContent.recylerViewAvailablePacks.adapter = packsAdapter
        binding.layoutContent.recylerViewAvailablePacks.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        updateLists(packIds)
        // Foods adapter
        foodsAdapter = AvailableFoodsAdapter()
        binding.layoutContent.recyclerViewAvailableFoods.adapter = foodsAdapter
        binding.layoutContent.recyclerViewAvailableFoods.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        updateFoods(packIds[0])

    }

    private fun packOnClick(pack: FoodPack) {
        // navigating to its content
        val action = HomeFragmentDirections.actionHomeFragmentToPackContentFragment()
        val bundle = bundleOf(Constants.FRAGMENT_PACK_ID to pack.id)
        findNavController().navigate(action.actionId, bundle)
    }

    private fun enrollButtonOnclick(newPackIds: ArrayList<Long>) {
        viewModel.enrollOrUnenroll(auth.currentUser!!.uid, newPackIds)
    }

    private fun updateFoods(packId: Long) {
        val day = SimpleDateFormat(Constants.DAY_OF_WEEK, Locale.ENGLISH).format(System.currentTimeMillis())
        Log.i("Day of week", day)
        viewModel.fetchTodayFoodDetails(day, packId)
        viewModel.todayFood.observe(viewLifecycleOwner, Observer { food ->
            foodsAdapter.setList(mutableListOf(food) as ArrayList<OrderServer>)
        })
    }

    private fun setUpRecyclerView(packIds: ArrayList<Long>) {
        binding.layoutContent.labelFoods.visibility = View.GONE
        binding.layoutContent.labelFoods.text = Constants.TODAYS_MEAL
        binding.layoutContent.recyclerViewAvailableFoods.visibility = View.GONE
        binding.layoutContent.labelPacks.text = Constants.AVAILABLE_PACKS
        // Packs adapter
        packsAdapter = AvailablePacksAdapter(
            { pack -> packOnClick(pack) },
            { newPackIds -> enrollButtonOnclick(newPackIds) }
        )
        binding.layoutContent.recylerViewAvailablePacks.adapter = packsAdapter
        binding.layoutContent.recylerViewAvailablePacks.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        updateLists(packIds)
        // Foods adapter
        foodsAdapter = AvailableFoodsAdapter()
        binding.layoutContent.recyclerViewAvailableFoods.adapter = foodsAdapter
        binding.layoutContent.recyclerViewAvailableFoods.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    private fun checkUserPacksData() {

        viewModel.userPacksId.observe(viewLifecycleOwner, Observer { packIds ->
            fetchUserData()
        })

    }

}
