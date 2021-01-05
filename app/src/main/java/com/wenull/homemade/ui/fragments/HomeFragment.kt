package com.wenull.homemade.ui.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.wenull.homemade.R
import com.wenull.homemade.adapter.AvailableFoodsAdapter
import com.wenull.homemade.adapter.AvailablePacksAdapter
import com.wenull.homemade.databinding.FragmentHomeBinding
import com.wenull.homemade.ui.activities.HomemadeActivity
import com.wenull.homemade.ui.fragments.base.BaseFragment
import com.wenull.homemade.ui.viewmodel.HomemadeViewModel
import com.wenull.homemade.utils.helper.FragmentActions
import com.wenull.homemade.utils.model.FoodPack


class HomeFragment : BaseFragment<FragmentHomeBinding, HomemadeViewModel>(), FragmentActions{


    private lateinit var packsAdapter: AvailablePacksAdapter
    private lateinit var foodsAdapter: AvailableFoodsAdapter

    override fun getLayout(): Int = R.layout.fragment_home

    override fun getViewModelClass(): Class<HomemadeViewModel> = HomemadeViewModel::class.java

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.lifecycleOwner = requireActivity()

        viewModel.setFirebaseSourceCallback()

        (activity as HomemadeActivity).setAction(this)

        viewModel.toastMessage.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled().let {
                if (it != null)
                    Toast.makeText(activity, it.toString(), Toast.LENGTH_SHORT).show()
            }
        })

        binding.layoutDrawer.profileBtnSidenav.setOnClickListener {
            if(getDrawerState() != 0.0f) onBackPressed()
            val action = HomeFragmentDirections.actionHomeFragmentToProfileFragment()
            findNavController().navigate(action)
        }

        binding.layoutDrawer.skippedBtnSidenav.setOnClickListener {
            if(getDrawerState() != 0.0f) onBackPressed()
            val action = HomeFragmentDirections.actionHomeFragmentToSkippedOrdersFragment()
            findNavController().navigate(action)
        }


        defineLayout()



    }


    private fun defineLayout(){
        setUpRecyclerView()

        /*binding.layoutContent.navIcon.setOnClickListener {
            if (binding.layoutContent.homeFragmentContainer.radius == 15f) {
                binding.layoutContent.homeFragmentContainer.radius = 0f
            } else {
                binding.layoutContent.homeFragmentContainer.radius = 15f
            }
        }*/
            binding.motionLayout.addTransitionListener(object : MotionLayout.TransitionListener{
                override fun onTransitionTrigger(
                    p0: MotionLayout?,
                    p1: Int,
                    p2: Boolean,
                    p3: Float
                ) {
                    Log.i("Transition", "triggered")

                }

                override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {
                    Log.i("Transition", "Started")
                    if (binding.layoutContent.homeFragmentContainer.radius == 20f) {
                        binding.layoutContent.homeFragmentContainer.radius = 0f
                        setWindowColor(R.color.dull_white)
                    } else {
                        binding.layoutContent.homeFragmentContainer.radius = 20f
                        setWindowColor(R.color.colorPrimary)
                    }
                }

                override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {
                    Log.i("Transition", "Change")
                }

                override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                    Log.i("Transition", "ended")



                }
            })



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

        if(getDrawerState() != 0.0f) onBackPressed()

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

    private fun setWindowColor(colorId: Int){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = requireActivity().window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.setStatusBarColor(resources.getColor(colorId))
            window.navigationBarColor = resources.getColor(colorId)
        }
    }

    override fun onBackPressed() {
        binding.motionLayout.transitionToStart()

    }

    override fun getDrawerState(): Float = binding.motionLayout.progress

}
