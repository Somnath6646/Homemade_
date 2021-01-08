package com.wenull.homemade.ui.fragments

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.wenull.homemade.R
import com.wenull.homemade.adapter.AvailableFoodsAdapter
import com.wenull.homemade.databinding.FragmentPackcontentBinding
import com.wenull.homemade.ui.fragments.base.BaseFragment
import com.wenull.homemade.ui.viewmodel.HomemadeViewModel
import com.wenull.homemade.utils.helper.Constants
import kotlin.properties.Delegates

class PackContentFragment : BaseFragment<FragmentPackcontentBinding, HomemadeViewModel>() {

    private lateinit var foodsAdapter: AvailableFoodsAdapter

    private var packId = -1L

    override fun getLayout(): Int = R.layout.fragment_packcontent

    override fun getViewModelClass(): Class<HomemadeViewModel> = HomemadeViewModel::class.java

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        packId = requireArguments().getLong(Constants.FRAGMENT_PACK_ID)
        Log.i("Pack Id", "$packId")

        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.setFirebaseSourceCallback()

        viewModel.toastMessage.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled().let {
                if (it != null)
                    Toast.makeText(activity, it.toString(), Toast.LENGTH_SHORT).show()
            }
        })

        setUpRecyclerView()

        binding.backIcon.setOnClickListener {
            findNavController().popBackStack()
        }

    }

    private fun setUpRecyclerView() {
        foodsAdapter = AvailableFoodsAdapter()
        binding.recyclerViewFragmentPackContent.adapter = foodsAdapter
        binding.recyclerViewFragmentPackContent.layoutManager = LinearLayoutManager(context)
        viewModel.packFoods.observe(viewLifecycleOwner, Observer { packFoods ->
            Log.i("Foods in fragment", packFoods.toString())
            foodsAdapter.setList(packFoods)
        })
        displayAvailableFoodList()
    }

    private fun displayAvailableFoodList() {
        viewModel.fetchPackFoodDetails(packId)
    }

}
