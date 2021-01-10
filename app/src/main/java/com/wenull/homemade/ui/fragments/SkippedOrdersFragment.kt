package com.wenull.homemade.ui.fragments

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.wenull.homemade.R
import com.wenull.homemade.adapter.SkippedFoodsAdapter
import com.wenull.homemade.databinding.FragmentSkippedOrdersBinding
import com.wenull.homemade.ui.fragments.base.BaseFragment
import com.wenull.homemade.ui.viewmodel.HomemadeViewModel
import com.wenull.homemade.utils.helper.Constants
import com.wenull.homemade.utils.model.OrderUnskip
import com.wenull.homemade.utils.model.UserSkippedData
import kotlinx.android.synthetic.main.alert_dialog.*
import java.util.*
import kotlin.collections.ArrayList

class SkippedOrdersFragment : BaseFragment<FragmentSkippedOrdersBinding, HomemadeViewModel>() {

    private val auth = FirebaseAuth.getInstance()

    private lateinit var adapter: SkippedFoodsAdapter

    private var skippedMeals = ArrayList<OrderUnskip>()

    private lateinit var dialog: Dialog
    
    override fun getLayout(): Int = R.layout.fragment_skipped_orders

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

        adapter = SkippedFoodsAdapter { orderUnskip -> unskipOnClick(orderUnskip) }
        binding.recyclerViewFragmentSkippedOrders.adapter = adapter
        binding.recyclerViewFragmentSkippedOrders.layoutManager = LinearLayoutManager(context)

    }

    private fun unskipOnClick(orderUnskip: OrderUnskip) {

        val calendar = Calendar.getInstance()

        val currentMonth = calendar.get(Calendar.MONTH) + 1
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
        val currentYear = calendar.get(Calendar.YEAR)

        val date = "$currentDay/$currentMonth/$currentYear"

        val skipDate = orderUnskip.skippedMeal.date

        val skipDay = skipDate.substring(0, skipDate.indexOf("/")).toInt()
        val skipMonth = skipDate.substring(skipDate.indexOf("/") + 1, skipDate.lastIndexOf("/")).toInt()
        val skipYear = skipDate.substring(skipDate.lastIndexOf("/") + 1).toInt()

        Log.i("Dates", "$date, $skipDay/$skipMonth/$skipYear")

        val hour = calendar.get(Calendar.HOUR_OF_DAY)

        if(currentYear <= skipYear) {

            if (currentMonth <= skipMonth) {

                if (currentMonth == skipMonth) {

                    if (currentDay < skipDay) {
                        unskipMeal(orderUnskip)
                    } else if (currentDay == skipDay) {
                        if (hour < orderUnskip.skippedMeal.skipLimit) {
                            unskipOnClick(orderUnskip)
                        } else {
                            viewModel.createToast(Constants.UNSKIP_TIME_LIMIT_ENDED_MESSAGE)
                        }
                    }

                } else {
                    unskipMeal(orderUnskip)
                }

            } else {
                viewModel.createToast(Constants.UNSKIP_TIME_LIMIT_ENDED_MESSAGE)
            }

        } else {
            viewModel.createToast(Constants.UNSKIP_TIME_LIMIT_ENDED_MESSAGE)
        }

    }

    private fun unskipMeal(orderUnskip: OrderUnskip) {

        dialog = Dialog(requireContext())

        dialog.setContentView(R.layout.alert_dialog)

        dialog.dialog_main_message.text = Constants.UNSKIP_DIALOG_MAIN_MESSAGE
        dialog.dialog_sub_text.text = Constants.UNSKIP_DIALOG_SUB_MESSAGE

        dialog.button_no.setOnClickListener { dialog.dismiss() }

        dialog.button_yes.setOnClickListener {
            refreshMeals()
            viewModel.unskipMeal(auth.currentUser!!.uid, orderUnskip.skippedMeal)
            dialog.dismiss()
        }

        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.show()

    }

    private fun refreshMeals() {

        viewModel.isUnskipSuccessful.observe(viewLifecycleOwner, Observer { isSuccessful ->
            if(isSuccessful) {
                viewModel.createToast(Constants.UNSKIP_SUCCESSFUL)
                getSkippedMealsData()
            } else {
                viewModel.createToast(Constants.UNSKIP_UNSUCCESSFUL)
            }
        })

    }

}
