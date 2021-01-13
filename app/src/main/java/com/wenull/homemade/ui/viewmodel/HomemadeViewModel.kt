package com.wenull.homemade.ui.viewmodel

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.wenull.homemade.utils.helper.Constants
import com.wenull.homemade.repositories.HomemadeRepository
import com.wenull.homemade.utils.helper.Event
import com.wenull.homemade.utils.model.*

class HomemadeViewModel(private val repository: HomemadeRepository): ViewModel(), Observable {

    // Toast message

    val toastMessage: LiveData<Event<String>>
        get() = repository.eventIndicator

    fun setFirebaseSourceCallback() = repository.setFirebaseSourceCallback()

    // Auth starts

    val signInState: LiveData<Event<Boolean>>
        get() = repository.signInState

    @Bindable
    val phoneNumber: MutableLiveData<String> = MutableLiveData()
    @Bindable
    val otp: MutableLiveData<String> = MutableLiveData()

    @Bindable
    val authButtonText: MutableLiveData<String> = MutableLiveData(Constants.MESSAGE_SEND_OTP)

    fun authenticate(): Unit {

        when(authButtonText.value!!) {

            Constants.MESSAGE_SEND_OTP -> {
                Log.i("PhoneNumber", "${phoneNumber.value}")
                if (phoneNumber.value != null && !phoneNumber.value.isNullOrEmpty()) {
                    authButtonText.value = Constants.MESSAGE_VERIFY
                    sendOTP(phoneNumber.value!!)
                    Log.i("Sending OTP to", "${phoneNumber.value}")
                    repository.eventIndicator.value = Event(Constants.SENDING_OTP)
                }
                else {
                    repository.eventIndicator.value = Event(Constants.ENTER_PHONE_NUMBER)
                }

                return
            }

            Constants.MESSAGE_VERIFY -> {
                Log.i("PhoneNumber", "${phoneNumber.value}")
                if(otp.value != null && !otp.value.isNullOrEmpty()) {
                    repository.verifyVerificationCode(otp.value!!)
                } else {
                    repository.eventIndicator.value = Event(Constants.ENTER_OTP)
                }
            }

        }
    }

    private fun sendOTP(phoneNumber: String) =
        repository.sendOTP(phoneNumber)

    // Auth ends

    // Add user data to database starts

    @Bindable
    val firstName = MutableLiveData<String>()
    @Bindable
    val lastName = MutableLiveData<String>()
    @Bindable
    val buildingName = MutableLiveData<String>()
    @Bindable
    val streetName = MutableLiveData<String>()
    @Bindable
    val locality = MutableLiveData<String>()
    @Bindable
    val city = MutableLiveData<String>()
    @Bindable
    val pincode = MutableLiveData<String>()

    var bitmap: Bitmap? = null

    var fileUri: Uri? = null

    val credentialsState: LiveData<Event<Boolean>>
        get() = repository.credentialsState
    val imageState: LiveData<Event<Boolean>>
        get() = repository.imageState
    val credentialsAndImageState: LiveData<Event<Boolean>>
        get() = repository.credentialsAndImageState

    fun addUserCredentialsToDatabase() {

        if(fileUri != null) {

            Log.i("FileURI", fileUri.toString())

            if((firstName.value != null && !firstName.value.isNullOrEmpty()) &&
                (lastName.value != null && !lastName.value.isNullOrEmpty()) &&
                (buildingName.value != null && !buildingName.value.isNullOrEmpty()) &&
                (streetName.value != null && !streetName.value.isNullOrEmpty()) &&
                (locality.value != null && !locality.value.isNullOrEmpty()) &&
                (city.value != null && !city.value.isNullOrEmpty()) &&
                (pincode.value != null && !pincode.value.isNullOrEmpty())) {

                repository.eventIndicator.value = Event("All credentials entered")

                val address = UserAddress(
                    buildingNameOrNumber = buildingName.value!!,
                    streetName = streetName.value!!,
                    locality = locality.value!!,
                    city = city.value!!,
                    pincode = pincode.value!!
                )

                val user = User(
                    uid = FirebaseAuth.getInstance().currentUser!!.uid,
                    phoneNumber = FirebaseAuth.getInstance().currentUser!!.phoneNumber!!,
                    firstName = firstName.value!!,
                    lastName = lastName.value!!,
                    address = address,
                    packsEnrolled = ArrayList<Long>(),
                    imageName = FirebaseAuth.getInstance().currentUser!!.uid
                )

                repository.addUserCredentials(user, fileUri!!)

            } else {
                repository.eventIndicator.value = Event(Constants.ENTER_ALL_CREDENTIALS)
            }

        } else {
            repository.eventIndicator.value = Event(Constants.ENTER_ALL_CREDENTIALS)
        }
    }

    // Checking if user data already exists

    val userExists: LiveData<Event<Boolean>>
        get() = repository.userExists

    fun checkIfUserExists(uid: String) {
        repository.checkIfUserExists(uid)
    }

    // Getting packs details from the user starts

    val packs: LiveData<ArrayList<FoodPack>>
        get() = repository.packsLiveData

    val packFoods: LiveData<ArrayList<OrderServer>>
        get() = repository.packFoodsLiveData

    fun fetchPackDetails() {
        repository.fetchPackDetails()
    }

    fun fetchPackFoodDetails(packId: Long) {
        repository.fetchPackFoodDetails(packId)
    }

    // Getting packs details from the user starts ends

    // Getting user data

    val userData: LiveData<User>
        get() = repository.userData

    fun fetchUserData(uid: String) {
        repository.fetchUserData(uid)
    }

    // Getting today's food details

    val todayFood: LiveData<ArrayList<OrderServer>>
        get() = repository.todayFoodLiveData

    fun fetchTodayFoodDetails(day: String, packIds: ArrayList<Long>) {
        repository.fetchTodayFoodDetails(day, packIds)
    }

    // Updating packs enrolled data

    val userPacksId: LiveData<ArrayList<Long>>
        get() = repository.userPacksEnrolledLiveData

    fun enrollOrUnenroll(uid: String, newPackIds: ArrayList<Long>) {
        repository.enrollOrUnenroll(uid, newPackIds)
    }

    // Getting user skipped meals data

    val userSkippedData: LiveData<UserSkippedData>
        get() = repository.userSkippedLiveData

    fun getUserSkippedData(uid: String) {
        repository.getUserSkippedData(uid)
    }

    // Skipping meal

    fun skipAMeal(uid: String, userSkippedData: UserSkippedData) {
        repository.skipAMeal(uid, userSkippedData)
    }

    // Getting skipped foods

    val skippedFoods: LiveData<ArrayList<OrderUnskip>>
        get() = repository.skippedFoodsLiveData

    fun getSkippedMeals(skippedMeals: ArrayList<OrderSkipped>) {
        repository.getSkippedMeals(skippedMeals)
    }

    // Unskip a meal

    val isUnskipSuccessful: LiveData<Boolean>
        get() = repository.isUnskipSuccessfulLiveData

    fun unskipMeal(uid: String, skippedMeal: OrderSkipped) {
        repository.unskipMeal(uid, skippedMeal)
    }

    fun createToast(message: String) {
        repository.createToast(message)
    }

    // Update user credentials

    val userCredentialsUpdateSuccessful: LiveData<Boolean>
        get() = repository.userCredentialsUpdateLiveData

    fun updateUserCredentials(user: User) {
        repository.updateUserCredentials(user)
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {}
    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {}

}
