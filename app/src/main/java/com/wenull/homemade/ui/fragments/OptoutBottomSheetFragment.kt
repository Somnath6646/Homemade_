package com.wenull.homemade.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.wenull.homemade.R
import com.wenull.homemade.data.remote.FirebaseSource
import com.wenull.homemade.databinding.FragmentOptoutBottomsheetBinding
import com.wenull.homemade.repositories.HomemadeRepository
import com.wenull.homemade.ui.viewmodel.HomemadeViewModel
import com.wenull.homemade.ui.viewmodel.HomemadeViewModelFactory
import com.wenull.homemade.utils.helper.Constants
import com.wenull.homemade.utils.model.FoodPack
import com.wenull.homemade.utils.model.OrderSkipped
import com.wenull.homemade.utils.model.UserSkippedData
import java.util.*
import kotlin.collections.ArrayList

class OptoutBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentOptoutBottomsheetBinding

    lateinit var pack: FoodPack

    lateinit var userSkippedData: UserSkippedData

    lateinit var viewModel: HomemadeViewModel

    private val auth = FirebaseAuth.getInstance()

    private var mealToSkip: OrderSkipped = OrderSkipped("", "", -1L, -1L, 0L)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val repository = HomemadeRepository(FirebaseSource(requireActivity()))
        val factory = HomemadeViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(HomemadeViewModel::class.java)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_optout_bottomsheet, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pack = arguments?.getParcelable<FoodPack>(Constants.BUNDLE_FOOD_PACK)!!
        userSkippedData = arguments?.getParcelable(Constants.BUNDLE_USER_SKIPPED_DATA)!!
        Log.i("Pack in bottom sheet", "$pack")
        Log.i("Skip in bottom sheet", "$userSkippedData")
        setUpCalenderView()

        viewModel.toastMessage.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it.getContentIfNotHandled().let {
                if (it != null)
                    Toast.makeText(activity, it.toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setUpCalenderView() {

        val calendar = Calendar.getInstance()

        println("${calendar.get(Calendar.HOUR_OF_DAY)} ${calendar.get(Calendar.HOUR)} ${calendar.get(Calendar.MONTH)} ${calendar.get(Calendar.DAY_OF_WEEK)}")

        if(pack.skipTimeLimit <= calendar.get(Calendar.HOUR_OF_DAY))
            binding.calenderView.minDate = calendar.timeInMillis + Constants.ONE_DAY_IN_MILLIS
        else
            binding.calenderView.minDate = calendar.timeInMillis

        binding.calenderView.setDate(0L, false, false)

        binding.calenderView.setOnDateChangeListener { view1, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            println("${calendar.get(Calendar.HOUR_OF_DAY)} ${calendar.get(Calendar.HOUR)} ${calendar.get(Calendar.MONTH)} ${calendar.get(Calendar.DAY_OF_WEEK)}")
            val date = "$dayOfMonth/${month + 1}/$year"
            Log.i("Date to skip", date)

            mealToSkip = OrderSkipped(
                date = date,
                day = getDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK)),
                foodId = getFoodId(calendar.get(Calendar.DAY_OF_WEEK)),
                packId = pack.id,
                skipLimit = pack.skipTimeLimit
            )
        }

        binding.cancelButton.setOnClickListener { dismiss() }

        binding.doneButton.setOnClickListener {

            if(mealToSkip.foodId != -1L) {

                if (userSkippedData.skippedMeals.contains(mealToSkip)) {
                    viewModel.createToast(Constants.MEAL_ALREADY_SKIPPED)
                } else {
                    userSkippedData.skippedMeals.add(mealToSkip)
                    viewModel.skipAMeal(auth.currentUser!!.uid, userSkippedData)
                }

                dismiss()

            } else {
                viewModel.createToast(Constants.PLEASE_SELECT_A_DATE)
            }

        }

    }

    private fun getDayOfWeek(day: Int): String =
        when(day) {
            1 -> Constants.SUNDAY
            2 -> Constants.MONDAY
            3 -> Constants.TUESDAY
            4 -> Constants.WEDNESDAY
            5 -> Constants.THURSDAY
            6 -> Constants.FRIDAY
            7 -> Constants.SATURDAY
            else -> Constants.MONDAY
        }

    private fun getFoodId(day: Int): Long =
        when(day) {
            1 -> 6
            2 -> 0
            3 -> 1
            4 -> 2
            5 -> 3
            6 -> 4
            7 -> 5
            else -> 0
        }

}
