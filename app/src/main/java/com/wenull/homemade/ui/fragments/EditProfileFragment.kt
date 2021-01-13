package com.wenull.homemade.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.wenull.homemade.R
import com.wenull.homemade.databinding.FragmentEditProfileBinding
import com.wenull.homemade.databinding.FragmentProfileBinding
import com.wenull.homemade.ui.fragments.base.BaseFragment
import com.wenull.homemade.ui.viewmodel.HomemadeViewModel
import com.wenull.homemade.utils.helper.Constants
import com.wenull.homemade.utils.model.User
import com.wenull.homemade.utils.model.UserAddress

class EditProfileFragment : BaseFragment<FragmentEditProfileBinding, HomemadeViewModel>() {

    lateinit var user: User

    override fun getLayout(): Int = R.layout.fragment_edit_profile

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

        user = arguments?.getParcelable<User>(Constants.USER)!!

        Log.i("Userin Edit", "$user")

        setUserCredentials(user)

    }

    private fun setUserCredentials(user: User) {

        binding.textFirstNameField.editText!!.setText(user.firstName)
        binding.textLastNameField.editText!!.setText(user.lastName)
        binding.textBuildingNameField.editText!!.setText(user.address.buildingNameOrNumber)
        binding.textStreetNameField.editText!!.setText(user.address.streetName)
        binding.textLocalityNameField.editText!!.setText(user.address.locality)
        binding.textCityNameField.editText!!.setText(user.address.city)
        binding.textPincodeField.editText!!.setText(user.address.pincode)

        updateButtonOnClickListener()

    }

    private fun updateButtonOnClickListener() {

        binding.proceedBtnCredfrag.setOnClickListener {

            if(checkIfCredentialsUnchanged(user)) {
                viewModel.createToast(Constants.TOAST_CREDENTIALS_IDENTICAL)
            } else {
                update()
            }

        }

    }

    private fun checkIfCredentialsUnchanged(user: User): Boolean {

        val firstName = binding.textFirstNameField.editText!!.text.toString()
        val lastName = binding.textLastNameField.editText!!.text.toString()
        val buildingNameOrNumber = binding.textBuildingNameField.editText!!.text.toString()
        val streetName = binding.textStreetNameField.editText!!.text.toString()
        val locality = binding.textLocalityNameField.editText!!.text.toString()
        val city = binding.textCityNameField.editText!!.text.toString()
        val pincode = binding.textPincodeField.editText!!.text.toString()

        if((firstName == user.firstName && lastName == user.lastName) &&
            (buildingNameOrNumber == user.address.buildingNameOrNumber && streetName == user.address.streetName && locality == user.address.locality && city == user.address.city && pincode == user.address.pincode)) {
            return true
        }

        return false
    }

    private fun update() {

        val firstName = binding.textFirstNameField.editText!!.text.toString()
        val lastName = binding.textLastNameField.editText!!.text.toString()
        val buildingNameOrNumber = binding.textBuildingNameField.editText!!.text.toString()
        val streetName = binding.textStreetNameField.editText!!.text.toString()
        val locality = binding.textLocalityNameField.editText!!.text.toString()
        val city = binding.textCityNameField.editText!!.text.toString()
        val pincode = binding.textPincodeField.editText!!.text.toString()

        if((firstName.isNullOrEmpty() || lastName.isNullOrEmpty()) ||
            (buildingNameOrNumber.isNullOrEmpty() || streetName.isNullOrEmpty() || locality.isNullOrEmpty() || city.isNullOrEmpty() || pincode.isNullOrEmpty())) {
            viewModel.createToast(Constants.ENTER_ALL_CREDENTIALS)
        } else {

            val address = UserAddress(
                buildingNameOrNumber = buildingNameOrNumber,
                streetName = streetName,
                locality = locality,
                city = city,
                pincode = pincode
            )

            val newUser = User(
                uid = user.uid,
                phoneNumber = user.phoneNumber,
                firstName = firstName,
                lastName = lastName,
                address = address,
                packsEnrolled = user.packsEnrolled,
                imageName = user.imageName
            )

            viewModel.updateUserCredentials(newUser)
            viewModel.userCredentialsUpdateSuccessful.observe(viewLifecycleOwner, Observer { isSuccessful ->
                if(isSuccessful) {
                    viewModel.createToast(Constants.TOAST_CREDENTIALS_UPDATE_SUCCESSFUL)
                    findNavController().popBackStack()
                } else {
                    viewModel.createToast(Constants.TOAST_CREDENTIALS_UPDATE_UNSUCCESSFUL)
                    findNavController().popBackStack()
                }
            })

        }

    }

}
