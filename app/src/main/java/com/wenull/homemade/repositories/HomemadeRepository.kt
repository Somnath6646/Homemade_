package com.wenull.homemade.repositories

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.wenull.homemade.data.remote.FirebaseSource
import com.wenull.homemade.data.remote.FirebaseSourceCallback
import com.wenull.homemade.utils.helper.Constants
import com.wenull.homemade.utils.helper.Event
import com.wenull.homemade.utils.model.*
import java.lang.Exception

class HomemadeRepository(private val firebaseSource: FirebaseSource) {

    val eventIndicator: MutableLiveData<Event<String>> = MutableLiveData()

    val signInState: MutableLiveData<Event<Boolean>> = MutableLiveData(Event(Constants.FAILED))
    val credentialsState: MutableLiveData<Event<Boolean>> = MutableLiveData(Event(Constants.FAILED))
    val imageState: MutableLiveData<Event<Boolean>> = MutableLiveData()

    val credentialsAndImageState: MutableLiveData<Event<Boolean>> = MutableLiveData(Event(Constants.FAILED))

    val packsLiveData: MutableLiveData<ArrayList<FoodPack>> = MutableLiveData()
    val packFoodsLiveData: MutableLiveData<ArrayList<OrderServer>> = MutableLiveData()
    val todayFoodLiveData: MutableLiveData<ArrayList<OrderServer>> = MutableLiveData()

    val userExists: MutableLiveData<Event<Boolean>> = MutableLiveData()

    val userData: MutableLiveData<User> = MutableLiveData()

    val userPacksEnrolledLiveData: MutableLiveData<ArrayList<Long>> = MutableLiveData()

    val userSkippedLiveData: MutableLiveData<UserSkippedData> = MutableLiveData()

    val skippedFoodsLiveData: MutableLiveData<ArrayList<OrderUnskip>> = MutableLiveData()

    val isUnskipSuccessfulLiveData: MutableLiveData<Boolean> = MutableLiveData()

    private var haveCredentialsBeenUploaded = false
    private var hasImageBeenUploaded = false

    private fun credentialsAndImageUploadSuccessful() {
        credentialsAndImageState.value = Event(Constants.SUCCESSFUL)
    }

    private val firebaseSourceCallback = object : FirebaseSourceCallback {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {}

        override fun onVerificationFailed(e: FirebaseException) {}

        override fun onCodeSent(verificationId: String, resendingToken: PhoneAuthProvider.ForceResendingToken) {
            eventIndicator.value = Event(Constants.CODE_SENT)
        }

        override fun signInSuccess() {
            signInState.value = Event(Constants.SUCCESSFUL)
        }

        override fun signInFailed(e: Exception) {
            signInState.value = Event(Constants.FAILED)
        }

        override fun userCredentialsUploadSuccessful() {
            credentialsState.value = Event(Constants.SUCCESSFUL)
            hasImageBeenUploaded = true
            if(haveCredentialsBeenUploaded && hasImageBeenUploaded) {
                credentialsAndImageUploadSuccessful()
            }
        }

        override fun userCredentialsUploadFailed() {
            credentialsState.value = Event(Constants.FAILED)
        }

        override fun userImageUploadSuccessful() {
            imageState.value = Event(Constants.SUCCESSFUL)
            hasImageBeenUploaded = true
            if(haveCredentialsBeenUploaded && hasImageBeenUploaded) {
                credentialsAndImageUploadSuccessful()
            }
        }

        override fun userImageUploadFailed(e: Exception) {
            imageState.value = Event(Constants.FAILED)
            Log.i("ImageUpload", "Exception")
            e.printStackTrace()
        }

        override fun userCredentialsAndImageUploadSuccessful() {
            credentialsAndImageState.value = Event(Constants.SUCCESSFUL)
        }

        override fun checkIfUserExists(exists: Boolean) {
            userExists.value = Event(exists)
        }

        override fun fetchUserData(user: User) {
            userData.value = user
        }

        override fun fetchTodayFoodDetails(foods: ArrayList<OrderServer>) {
            todayFoodLiveData.value = foods
        }

        override fun packDetailsFetchSuccessful(packs: ArrayList<FoodPack>) {
            packsLiveData.value = packs
        }

        override fun packFoodDetailsFetchSuccessful(foods: ArrayList<OrderServer>) {
            packFoodsLiveData.value = foods
            Log.i("Foods in repo", "$foods")
        }

        override fun packEnrolledDataChanged(newPackIds: ArrayList<Long>) {
            userPacksEnrolledLiveData.value = newPackIds
        }

        override fun userSkippedMealDataFetchSuccessful(userSkippedData: UserSkippedData) {
            userSkippedLiveData.value = userSkippedData
        }

        override fun skippedMealsFetchSuccessful(ordersUnskip: ArrayList<OrderUnskip>) {
            skippedFoodsLiveData.value = ordersUnskip
        }

        override fun isUnskipSuccessful(isSuccessful: Boolean) {
            isUnskipSuccessfulLiveData.value = isSuccessful
        }

    }

    fun setFirebaseSourceCallback() =
        firebaseSource.setFirebaseSourceCallback(firebaseSourceCallback)

    // Auth starts

    fun sendOTP(phoneNumber: String) =
        firebaseSource.sendVerificationCode(phoneNumber)

    fun verifyVerificationCode(code: String) =
        firebaseSource.verifyVerificationCode(code)

    // Auth ends

    fun addUserCredentials(user: User, uri: Uri) {
        firebaseSource.addUserCredentials(user, uri)
    }

    fun fetchPackDetails() {
        firebaseSource.fetchPackDetails()
    }

    fun fetchPackFoodDetails(packId: Long) {
        firebaseSource.fetchPackFoodDetails(packId)
    }

    fun checkIfUserExists(uid: String) {
        firebaseSource.checkIfUserExists(uid)
    }

    fun fetchUserData(uid: String) {
        firebaseSource.fetchUserData(uid)
    }

    fun fetchTodayFoodDetails(day: String, packIds: ArrayList<Long>) {
        firebaseSource.fetchTodayFoodDetails(day, packIds)
    }

    fun enrollOrUnenroll(uid: String, newPackIds: ArrayList<Long>) {
        firebaseSource.enrollOrUnenroll(uid, newPackIds)
    }

    fun getUserSkippedData(uid: String) {
        firebaseSource.getUserSkippedData(uid)
    }

    fun skipAMeal(uid: String, userSkippedData: UserSkippedData) {
        firebaseSource.skipAMeal(uid, userSkippedData)
    }

    fun getSkippedMeals(skippedMeals: ArrayList<OrderSkipped>) {
        firebaseSource.getSkippedMeals(skippedMeals)
    }

    fun unskipMeal(uid: String, skippedMeal: OrderSkipped) {
        firebaseSource.unskipMeal(uid, skippedMeal)
    }

    fun createToast(message: String) {
        eventIndicator.value = Event(message)
    }

}
