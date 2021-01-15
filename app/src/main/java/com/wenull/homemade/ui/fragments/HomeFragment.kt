package com.wenull.homemade.ui.fragments

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.wenull.homemade.R
import com.wenull.homemade.adapter.AvailableFoodsAdapter
import com.wenull.homemade.adapter.AvailablePacksAdapter
import com.wenull.homemade.databinding.FragmentHomeBinding
import com.wenull.homemade.ui.activities.HomemadeActivity
import com.wenull.homemade.ui.fragments.base.BaseFragment
import com.wenull.homemade.ui.viewmodel.HomemadeViewModel
import com.wenull.homemade.utils.helper.Constants
import com.wenull.homemade.utils.helper.FragmentActions
import com.wenull.homemade.utils.model.FoodPack
import com.wenull.homemade.utils.model.OrderServer
import com.wenull.homemade.utils.model.User
import kotlinx.android.synthetic.main.alert_dialog.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment : BaseFragment<FragmentHomeBinding, HomemadeViewModel>(), FragmentActions{


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

        binding.layoutDrawer.signoutBtnSidenav.setOnClickListener {
            val dialog = Dialog(requireContext())

            dialog.setContentView(R.layout.alert_dialog)

            dialog.dialog_main_message.text = Constants.SIGN_OUT_MAIN_MESSAGE
            dialog.dialog_sub_text.text = Constants.SIGN_OUT_SUB_MESSAGE

            dialog.button_no.setOnClickListener { dialog.dismiss() }

            dialog.button_yes.setOnClickListener {
                dialog.show()
                viewModel.signOut()
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToSignupLoginFragment())
                dialog.dismiss()
            }

            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            dialog.show()

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

        viewModel.userData.observe(viewLifecycleOwner, Observer { user ->

            this.user = user

            val packs = user.packsEnrolled

            Log.i("Packs", "$packs")

            if(packs.size > 0) {
                setUserPackAndFoodRecyclerView(packs)
            } else {
                setUpRecyclerView(ArrayList())
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
            { newPackIds, type: String -> enrollButtonOnclick(newPackIds, type) }
        )
        binding.layoutContent.recylerViewAvailablePacks.adapter = packsAdapter
        binding.layoutContent.recylerViewAvailablePacks.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        updateLists(packIds)
        // Foods adapter
        foodsAdapter = AvailableFoodsAdapter()
        binding.layoutContent.recyclerViewAvailableFoods.adapter = foodsAdapter
        binding.layoutContent.recyclerViewAvailableFoods.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        updateFoods(packIds)

    }

    private fun packOnClick(pack: FoodPack) {
        //navigating to its content
        val action = HomeFragmentDirections.actionHomeFragmentToPackContentFragment()
        val bundle = bundleOf(Constants.FRAGMENT_PACK_ID to pack.id)
        findNavController().navigate(action.actionId, bundle)
    }

    private fun enrollButtonOnclick(newPackIds: ArrayList<Long>, type: String) {
        val dialog = Dialog(requireContext())

        dialog.setContentView(R.layout.alert_dialog)

        dialog.dialog_main_message.text = Constants.UNENROLL_MAIN_MESSAGE
        dialog.dialog_sub_text.text = Constants.UNENROLL_SUB_MESSAGE

        dialog.button_no.setOnClickListener { dialog.dismiss() }

        dialog.button_yes.setOnClickListener {
            viewModel.enrollOrUnenroll(auth.currentUser!!.uid, newPackIds)
            dialog.dismiss()
        }

        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        if(type.equals(Constants.ENROLLED)) {
            dialog.show()
        }else{
            viewModel.enrollOrUnenroll(auth.currentUser!!.uid, newPackIds)
        }

    }

    private fun updateFoods(packIds: ArrayList<Long>) {
        val day = SimpleDateFormat(Constants.DAY_OF_WEEK, Locale.ENGLISH).format(System.currentTimeMillis())
        Log.i("Day of week", day)
        viewModel.fetchTodayFoodDetails(day, packIds)
        viewModel.todayFood.observe(viewLifecycleOwner, Observer { foods ->
            foodsAdapter.setList(foods)
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
            { newPackIds,type -> enrollButtonOnclick(newPackIds, type) }
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



    override fun onBackPressed() {
        binding.motionLayout.transitionToStart()

    }

    override fun getDrawerState(): Float = binding.motionLayout.progress

}
