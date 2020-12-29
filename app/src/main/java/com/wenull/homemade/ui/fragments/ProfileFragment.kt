package com.wenull.homemade.ui.fragments

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.wenull.homemade.R
import com.wenull.homemade.adapter.UserPacksAdapter
import com.wenull.homemade.databinding.FragmentProfileBinding
import com.wenull.homemade.ui.viewmodel.HomemadeViewModel
import com.wenull.homemade.ui.fragments.base.BaseFragment


class ProfileFragment : BaseFragment<FragmentProfileBinding, HomemadeViewModel>() {

    private lateinit var adapter: UserPacksAdapter

    override fun getLayout(): Int  = R.layout.fragment_profile

    override fun getViewModelClass(): Class<HomemadeViewModel> = HomemadeViewModel::class.java

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.lifecycleOwner = this
        setUpRecyclerView()
        binding.profileBackBtn.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setUpRecyclerView(){
        adapter = UserPacksAdapter()

        binding.recylerViewYourPacks.adapter = adapter
        binding.recylerViewYourPacks.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        displayPacks()
    }

    private fun displayPacks() {}

}