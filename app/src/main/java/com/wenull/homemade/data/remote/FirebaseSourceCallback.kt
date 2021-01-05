package com.wenull.homemade.data.remote

import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.wenull.homemade.utils.model.FoodPack
import com.wenull.homemade.utils.model.OrderServer
import com.wenull.homemade.utils.model.User
import java.lang.Exception

interface FirebaseSourceCallback {

    // Authentication methods

    fun onVerificationCompleted(credential: PhoneAuthCredential)
    fun onVerificationFailed(e: FirebaseException)
    fun onCodeSent(verificationId: String, resendingToken: PhoneAuthProvider.ForceResendingToken)
    fun signInSuccess()
    fun signInFailed(e: Exception)

    // Adding user credentials to database

    fun userCredentialsUploadSuccessful()
    fun userCredentialsUploadFailed()
    fun userImageUploadSuccessful()
    fun userImageUploadFailed(e: Exception)
    fun userCredentialsAndImageUploadSuccessful()

    // Check if user data already exists

    fun checkIfUserExists(exists: Boolean)

    // Getting user details

    fun fetchUserData(user: User)

    // Getting today's food details

    fun fetchTodayFoodDetails(food: OrderServer)

    // Getting pack details

    fun packDetailsFetchSuccessful(packs: ArrayList<FoodPack>)
    fun packFoodDetailsFetchSuccessful(foods: ArrayList<OrderServer>)

    // Updating packs enrolled

    fun packEnrolledDataChanged(newPackIds: ArrayList<Long>)

}
