package com.wenull.homemade.ui.fragments

import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.wenull.homemade.R
import com.wenull.homemade.adapter.AvailableFoodsAdapter
import com.wenull.homemade.adapter.AvailablePacksAdapter
import com.wenull.homemade.databinding.FragmentHomeBinding
import com.wenull.homemade.ui.viewmodel.HomemadeViewModel
import com.wenull.homemade.ui.fragments.base.BaseFragment
import com.wenull.homemade.utils.model.FoodPack

class HomeFragment : BaseFragment<FragmentHomeBinding, HomemadeViewModel>() {

    private lateinit var packsAdapter: AvailablePacksAdapter
    private lateinit var foodsAdapter: AvailableFoodsAdapter

    override fun getLayout(): Int = R.layout.fragment_home

    override fun getViewModelClass(): Class<HomemadeViewModel> = HomemadeViewModel::class.java

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.lifecycleOwner = requireActivity()

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

        setUpRecyclerView()

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

    fun packOnClick(pack: FoodPack) {
        updateFoods(pack.id)

        //navigating to its content
        val action = HomeFragmentDirections.actionHomeFragmentToPackContentFragment()
        findNavController().navigate(action)
    }

    private fun updateFoods(packId: Long) {
        viewModel.fetchPackFoodDetails(packId)
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
