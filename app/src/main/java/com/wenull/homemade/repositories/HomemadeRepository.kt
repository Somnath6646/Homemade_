package com.wenull.homemade.repositories

import android.graphics.Bitmap
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
import com.wenull.homemade.utils.model.User
import java.lang.Exception

class HomemadeRepository(private val firebaseSource: FirebaseSource) {

    val eventIndicator: MutableLiveData<Event<String>> = MutableLiveData()

    val signInState: MutableLiveData<Event<Boolean>> = MutableLiveData(Event(Constants.FAILED))
    val credentialsState: MutableLiveData<Event<Boolean>> = MutableLiveData(Event(Constants.FAILED))
    val imageState: MutableLiveData<Event<Boolean>> = MutableLiveData(Event(Constants.FAILED))

    val credentialsAndImageState: MutableLiveData<Event<Boolean>> = MutableLiveData(Event(Constants.FAILED))

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

}
