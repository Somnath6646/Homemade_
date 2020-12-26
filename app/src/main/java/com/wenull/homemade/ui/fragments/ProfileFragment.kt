package com.wenull.homemade.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.wenull.homemade.Pack
import com.wenull.homemade.R
import com.wenull.homemade.adapter.AvailablePacksAdapter
import com.wenull.homemade.adapter.UserPacksAdapter
import com.wenull.homemade.databinding.FragmentProfileBinding
import com.wenull.homemade.ui.activities.HomemadeActivityViewModel
import com.wenull.homemade.ui.fragments.base.BaseFragment


class ProfileFragment : BaseFragment<FragmentProfileBinding, HomemadeActivityViewModel>() {

    private lateinit var adapter: UserPacksAdapter

    override fun getLayout(): Int  = R.layout.fragment_profile

    override fun getViewModelClass(): Class<HomemadeActivityViewModel> = HomemadeActivityViewModel::class.java

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
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

    private fun displayPacks(){
        adapter.setList(listOf(Pack("https://www.youngisthan.in/wp-content/uploads/2017/04/feature-10.jpg", "First choice", "A pocket friendly daily meal package for hostellers.\n" +
                "The premium quality food is delivered at your doorstep\n" +
                "with no delivery charges. Daily meal is available only at \n" +
                "Rs. 47.", null),
                Pack("https://d4t7t8y8xqo0t.cloudfront.net/resized/750X436/eazytrendz%2F2771%2Ftrend20200325141849.jpg", "Second choice", "A pocket friendly daily meal package for hostellers.\n" +
                        "The premium quality food is delivered at your doorstep\n" +
                        "with no delivery charges. Daily meal is available only at \n" +
                        "Rs. 47.", null)))
    }

}