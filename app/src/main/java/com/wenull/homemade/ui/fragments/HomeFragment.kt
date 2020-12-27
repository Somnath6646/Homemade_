package com.wenull.homemade.ui.fragments

import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.wenull.homemade.R
import com.wenull.homemade.adapter.AvailablePacksAdapter
import com.wenull.homemade.databinding.FragmentHomeBinding
import com.wenull.homemade.ui.viewmodel.HomemadeViewModel
import com.wenull.homemade.ui.fragments.base.BaseFragment
import com.wenull.homemade.utils.helper.Constants

class HomeFragment : BaseFragment<FragmentHomeBinding, HomemadeViewModel>() {

    private lateinit var adapter: AvailablePacksAdapter

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

        setUpRecyclerView()

    }

    private fun updateLists() {
        viewModel.fetchPackDetails()
        viewModel.packs.observe(viewLifecycleOwner, Observer { packs ->
            adapter.setList(packs)
        })
    }

    private fun setUpRecyclerView() {
        adapter = AvailablePacksAdapter()
        binding.layoutContent.recylerViewAvailablePacks.adapter = adapter
        binding.layoutContent.recylerViewAvailablePacks.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        updateLists()
    }

    private fun displayPacks() {}

}
