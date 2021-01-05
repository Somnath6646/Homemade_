package com.wenull.homemade.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.wenull.homemade.R
import com.wenull.homemade.data.remote.FirebaseSource
import com.wenull.homemade.databinding.FragmentOptoutBottomsheetBinding
import com.wenull.homemade.repositories.HomemadeRepository
import com.wenull.homemade.ui.viewmodel.HomemadeViewModel
import com.wenull.homemade.ui.viewmodel.HomemadeViewModelFactory
import com.wenull.homemade.utils.helper.Constants
import com.wenull.homemade.utils.model.FoodPack
import java.util.*

class OptoutBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentOptoutBottomsheetBinding

    lateinit var pack: FoodPack

    lateinit var viewModel: HomemadeViewModel

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
        Log.i("Pack in bottom sheet", "$pack")
        setUpCalenderView()
    }

    private fun setUpCalenderView() {

        val calendar = Calendar.getInstance()

        println("${calendar.get(Calendar.HOUR_OF_DAY)} ${calendar.get(Calendar.HOUR)} ${calendar.get(Calendar.MONTH)}")

        if(pack.skipTimeLimit <= calendar.get(Calendar.HOUR_OF_DAY))
            binding.calenderView.minDate = calendar.timeInMillis + Constants.ONE_DAY_IN_MILLIS
        else
            binding.calenderView.minDate = calendar.timeInMillis

        binding.calenderView.setOnDateChangeListener { view1, year, month, dayOfMonth ->
            Log.i("Date to skip", "$dayOfMonth/${month + 1}/$year")
        }

        binding.cancelButton.setOnClickListener { dismiss() }

        binding.doneButton.setOnClickListener { dismiss() }

    }

}
