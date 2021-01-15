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
import com.wenull.homemade.utils.helper.CredState
import com.wenull.homemade.utils.helper.Event
import com.wenull.homemade.utils.model.*
import java.lang.Exception
import kotlin.reflect.typeOf

class HomemadeRepository(private val firebaseSource: FirebaseSource) {

    val eventIndicator: MutableLiveData<Event<String>> = MutableLiveData()
    val progressBarState: MutableLiveData<Boolean> = MutableLiveData(false)

    val signInState: MutableLiveData<Event<Boolean>> = MutableLiveData(Event(Constants.FAILED))
    val credentialsState: MutableLiveData<Event<Boolean>> = MutableLiveData(Event(Constants.FAILED))
    val imageState: MutableLiveData<Event<Boolean>> = MutableLiveData()

    val credentialsAndImageState: MutableLiveData<Event<CredState>> = MutableLiveData()

    val packsLiveData: MutableLiveData<ArrayList<FoodPack>> = MutableLiveData()
    val packFoodsLiveData: MutableLiveData<ArrayList<OrderServer>> = MutableLiveData()
    val todayFoodLiveData: MutableLiveData<ArrayList<OrderServer>> = MutableLiveData()

    val userExists: MutableLiveData<Event<Boolean>> = MutableLiveData()

    val userData: MutableLiveData<User> = MutableLiveData()

    val userPacksEnrolledLiveData: MutableLiveData<ArrayList<Long>> = MutableLiveData()

    val userSkippedLiveData: MutableLiveData<UserSkippedData> = MutableLiveData()

    val skippedFoodsLiveData: MutableLiveData<ArrayList<OrderUnskip>> = MutableLiveData()

    val isUnskipSuccessfulLiveData: MutableLiveData<Boolean> = MutableLiveData()

    val userCredentialsUpdateLiveData: MutableLiveData<Boolean> = MutableLiveData()

    private var haveCredentialsBeenUploaded = false
    private var hasImageBeenUploaded = false

    private fun credentialsAndImageUploadSuccessful() {
        credentialsAndImageState.value = Event(CredState.SUCESSFUL)
    }

    private val firebaseSourceCallback = object : FirebaseSourceCallback {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            Log.i("MYTAG", "Verification complete")
            progressBarState.value = false

        }

        override fun onVerificationFailed(e: FirebaseException) {
            Log.i("MYTAG", "Verification failed")
            progressBarState.value = false
            eventIndicator.value = Event(e.message.toString())
        }

        override fun onCodeSent(verificationId: String, resendingToken: PhoneAuthProvider.ForceResendingToken) {
            eventIndicator.value = Event(Constants.CODE_SENT)
            progressBarState.value = false
            Log.i("MYTAG", "Code sent")
        }

        override fun signInSuccess() {
            progressBarState.value = false
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
            credentialsAndImageState.value = Event(CredState.SUCESSFUL)
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

        override fun updateUserCredentialsSuccessful(isSuccessful: Boolean) {
            userCredentialsUpdateLiveData.value = isSuccessful
        }

    }

    fun setFirebaseSourceCallback() =
        firebaseSource.setFirebaseSourceCallback(firebaseSourceCallback)

    // Auth starts

    fun sendOTP(phoneNumber: String) {
        firebaseSource.sendVerificationCode(phoneNumber)
        progressBarState.value = true
    }


    fun verifyVerificationCode(code: String) {
        firebaseSource.verifyVerificationCode(code)
    }


    // Auth ends

    fun addUserCredentials(user: User, uri: Uri) {
        firebaseSource.addUserCredentials(user, uri)
        credentialsAndImageState.value = Event(CredState.LOADING)
    }

    fun fetchPackDetails() {
        firebaseSource.fetchPackDetails()
    }

    fun fetchPackDetails(packIds: ArrayList<Long>){
        firebaseSource.fetchPackDetailByPackId(packIds)
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

    fun updateUserCredentials(user: User) {
        firebaseSource.updateUserCredentials(user)
    }

    fun signOut(){
        firebaseSource.signOut()
    }

}
