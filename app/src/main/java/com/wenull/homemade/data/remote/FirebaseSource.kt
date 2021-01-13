package com.wenull.homemade.data.remote

import android.app.Activity
import android.net.Uri
import android.util.Log
import com.google.android.gms.tasks.TaskExecutors
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.wenull.homemade.utils.helper.Constants
import com.wenull.homemade.utils.model.*
import java.util.concurrent.TimeUnit

class FirebaseSource(private val activity: Activity) {

    private lateinit var storedVerificationId: String
    private lateinit var storedResendingToken: PhoneAuthProvider.ForceResendingToken

    private lateinit var firebaseSourceCallback: FirebaseSourceCallback

    private val firestore = FirebaseFirestore.getInstance()

    fun setFirebaseSourceCallback(listener: FirebaseSourceCallback) {
        this.firebaseSourceCallback = listener
    }

    private val auth by lazy {
        FirebaseAuth.getInstance()
    }

    // Auth starts

    private val phoneAuthCallback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            Log.d("TAG", "onVerificationCompleted:$credential")
            firebaseSourceCallback.onVerificationCompleted(credential)
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {

            Log.w("TAG", "onVerificationFailed", e)
            Log.i("LOOO!!!!!HEREEEEEEEE", e.stackTrace.toString())
            firebaseSourceCallback.onVerificationFailed(e)

        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            firebaseSourceCallback.onCodeSent(verificationId, token)
            storedVerificationId = verificationId
            storedResendingToken = token

        }
    }

    fun sendVerificationCode(phoneNumber: String) {

        val phoneAuthOptions = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber.trim())
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(phoneAuthCallback)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions)
    }

    fun verifyVerificationCode(code: String) {
        val credential = PhoneAuthProvider.getCredential(storedVerificationId, code)
        signInWithPhoneAuthCredential(credential)
    }

    fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential).addOnCompleteListener() { task ->
            if (task.isSuccessful) {
                firebaseSourceCallback.signInSuccess()
            } else {
                Log.i("Error", task.exception.toString())
                firebaseSourceCallback.signInFailed(task.exception!!)
            }
        }
    }

    fun resendCode(phoneNumber: String) {

        val phoneAuthOptions = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber.trim())
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(TaskExecutors.MAIN_THREAD as Activity)
            .setCallbacks(phoneAuthCallback)
            .setForceResendingToken(storedResendingToken)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions)
    }

    // Auth ends

    // Adding user credentials to database starts

    var haveCredentialsBeenUploaded = false
    var hasImageBeenUploaded = false

    fun addUserCredentials(user: User, uri: Uri) {

        firestore.collection(Constants.COLLECTION_USERS)
            .document(auth.currentUser!!.uid)
            .set(user)
            .addOnSuccessListener {
                haveCredentialsBeenUploaded = true
                if (haveCredentialsBeenUploaded && hasImageBeenUploaded) {
                    firebaseSourceCallback.userCredentialsAndImageUploadSuccessful()
                }
                Log.i("CredState", "Successful")
                firebaseSourceCallback.userCredentialsUploadSuccessful()
            }
            .addOnFailureListener {
                haveCredentialsBeenUploaded = false
                firebaseSourceCallback.userCredentialsUploadFailed()
            }

        val storageReference = Firebase.storage.reference

        val uploadTask = storageReference.child("users/${user.imageName}").putFile(uri)

        uploadTask.addOnSuccessListener {
            hasImageBeenUploaded = true
            if (haveCredentialsBeenUploaded && hasImageBeenUploaded) {
                firebaseSourceCallback.userCredentialsAndImageUploadSuccessful()
            }
            Log.i("ImageState", "Successful")
            firebaseSourceCallback.userImageUploadSuccessful()
        }.addOnFailureListener { exception ->
            hasImageBeenUploaded = false
            firebaseSourceCallback.userImageUploadFailed(exception)
        }

    }

    // Adding user credentials to database ends

    // Checking if user data already exists

    fun checkIfUserExists(uid: String) {

        firestore.collection(Constants.COLLECTION_USERS).document(uid).

        firestore.collection(Constants.COLLECTION_USERS).document(uid)
            .get()
            .addOnCompleteListener {
                if(it.isSuccessful) {

                    val data = it.result?.data
                    Log.i("User Data", "${data}")
                    if (data != null)
                        firebaseSourceCallback.checkIfUserExists(Constants.USER_DATA_EXISTS)
                    else
                        firebaseSourceCallback.checkIfUserExists(Constants.USER_DATA_DOES_NOT_EXIST)

                } else {
                    firebaseSourceCallback.checkIfUserExists(Constants.USER_DATA_DOES_NOT_EXIST)
                    Log.i("Exception", "User data check exist")
                    it.exception?.printStackTrace()
                }
            }

    }

    // Fetching user details

    fun fetchUserData(uid: String) {

        firestore.collection(Constants.COLLECTION_USERS).document(uid)
            .get()
            .addOnCompleteListener { task ->
                if(task.isSuccessful) {

                    val data = task.result?.data

                    if(data != null) {

                        val addressRef = data[Constants.FIELD_ADDRESS] as Map<*, *>

                        val address = UserAddress(
                            buildingNameOrNumber = addressRef[Constants.FIELD_BUILDING_NAME_OR_NUMBER] as String,
                            streetName = addressRef[Constants.FIELD_STREET_NAME] as String,
                            locality = addressRef[Constants.FIELD_LOCALITY] as String,
                            city = addressRef[Constants.FIELD_CITY] as String,
                            pincode = addressRef[Constants.FIELD_PINCODE] as String
                        )

                        val user = User(
                            uid = data[Constants.FIELD_UID] as String,
                            phoneNumber = data[Constants.FIELD_PHONE_NUMBER] as String,
                            firstName = data[Constants.FIELD_FIRST_NAME] as String,
                            lastName = data[Constants.FIELD_LAST_NAME] as String,
                            address = address,
                            packsEnrolled = data[Constants.FIELD_PACKS_ENROLLED] as ArrayList<Long>,
                            imageName = data[Constants.FIELD_IMAGE_NAME] as String
                        )

                        firebaseSourceCallback.fetchUserData(user)

                    } else {
                        Log.i("UserData", "null")
                    }

                } else {
                    Log.i("UserDataFetch", "Unsuccessful")
                }

            }

    }

    // Fetching packs data starts

    fun fetchPackDetails() {

        firestore.collection(Constants.COLLECTION_PACKS).get()
            .addOnSuccessListener { data ->

                if(!data.isEmpty) {

                    val documents = data.documents

                    val packs = ArrayList<FoodPack>()

                    documents.forEach { document ->

                        val pack = FoodPack(
                            id = document.data!![Constants.FIELD_ID] as Long,
                            name = document.data!![Constants.FIELD_NAME] as String,
                            description = document.data!![Constants.FIELD_DESCRIPTION]  as String,
                            imageName = document.data!![Constants.FIELD_IMAGE_NAME]  as String,
                            skipTimeLimit = document.data!![Constants.FIELD_SKIP_TME_LIMIT] as Long
                        )

                        packs.add(pack)
                    }

                    Log.i("Packs", packs.toString())

                    firebaseSourceCallback.packDetailsFetchSuccessful(packs)

                }

            }

    }

    // Fetching today's food details

    fun fetchTodayFoodDetails(day: String, packIds: ArrayList<Long>) {

        val foods = ArrayList<OrderServer>()

        packIds.forEach { packId ->

            firestore.collection(Constants.COLLECTION_PACKS)
                .document(packId.toString())
                .collection(Constants.COLLECTION_FOODS)
                .whereEqualTo(Constants.FIELD_DAY, day)
                .whereEqualTo(Constants.FIELD_PACK_ID, packId)
                .get()
                .addOnCompleteListener { task ->

                    if (task.isSuccessful) {

                        val documents = task.result?.documents

                        if (documents != null) {

                            if (documents.size > 0) {

                                val document = documents[0]

                                Log.i("TodayFood", "${documents[0]}")

                                val food = OrderServer(
                                    id = document!![Constants.FIELD_ID] as Long,
                                    name = document[Constants.FIELD_NAME] as String,
                                    description = document[Constants.FIELD_DESCRIPTION] as String,
                                    price = document[Constants.FIELD_PRICE] as String,
                                    day = document[Constants.FIELD_DAY] as String,
                                    imageName = document[Constants.FIELD_IMAGE_NAME] as String,
                                    packId = document[Constants.FIELD_PACK_ID] as Long
                                )

                                foods.add(food)

                                Log.i("TodayFood", "$food")

                                firebaseSourceCallback.fetchTodayFoodDetails(foods)

                            }

                        } else {
                            Log.i("TodayFoodGet", "Doc is null")
                        }

                    } else {
                        Log.i("TodayFoodGet", "Failed")
                        task.exception?.printStackTrace()
                    }

                }

        }

    }

    fun fetchPackFoodDetails(packId: Long) {

        Log.i("PackId firebaseSource", "$packId")

        firestore.collection(Constants.COLLECTION_PACKS).document(packId.toString())
            .collection(Constants.COLLECTION_FOODS).get()
            .addOnSuccessListener { snapshot ->

                val documents = snapshot.documents

                val foods = ArrayList<OrderServer>()

                documents.forEach { document ->

                    val food = OrderServer(
                        id = document!![Constants.FIELD_ID] as Long,
                        name = document[Constants.FIELD_NAME] as String,
                        description = document[Constants.FIELD_DESCRIPTION] as String,
                        price = document[Constants.FIELD_PRICE] as String,
                        day = document[Constants.FIELD_DAY] as String,
                        imageName = document[Constants.FIELD_IMAGE_NAME] as String,
                        packId = document[Constants.FIELD_PACK_ID] as Long
                    )

                    Log.i("Food", "$food")

                    foods.add(food)

                }

                firebaseSourceCallback.packFoodDetailsFetchSuccessful(foods)

            }.addOnFailureListener {
                Log.i("Exception", "In fetching food details")
                it.printStackTrace()
            }

    }

    fun enrollOrUnenroll(uid: String, newPackIds: ArrayList<Long>) {

        firestore.collection(Constants.COLLECTION_USERS)
            .document(uid)
            .update(Constants.FIELD_PACKS_ENROLLED, newPackIds)
            .addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    firebaseSourceCallback.packEnrolledDataChanged(newPackIds)
                } else {
                    Log.i("ExceptionEnroll", "In updating enroll data")
                    task.exception?.printStackTrace()
                }
            }

    }

    fun getUserSkippedData(uid: String) {

        firestore.collection(Constants.COLLECTION_SKIPPED).document(uid)
            .get()
            .addOnCompleteListener { task ->
                if(task.isSuccessful) {

                    val data = task.result?.data

                    if(data != null) {

                        val skippedMeals = ArrayList<OrderSkipped>()
                        (data[Constants.FIELD_SKIPPED_MEALS] as ArrayList<Map<String, *>>).forEach { meal ->
                            val skippedMeal = OrderSkipped(
                                date = meal[Constants.FIELD_DATE] as String,
                                day = meal[Constants.FIELD_DAY] as String,
                                foodId = meal[Constants.FIELD_FOOD_ID] as Long,
                                packId = meal[Constants.FIELD_PACK_ID] as Long,
                                skipLimit = meal[Constants.FIELD_SKIP_LIMIT] as Long
                            )
                            skippedMeals.add(skippedMeal)
                        }

                        val userSkippedData = UserSkippedData(
                            uid = data[Constants.FIELD_UID] as String,
                            skippedMeals = skippedMeals
                        )

                        firebaseSourceCallback.userSkippedMealDataFetchSuccessful(userSkippedData)

                        Log.i("UserSkippedData", "$data")

                    } else {
                        Log.i("UserSkippedData", "null")
                    }

                }
            }

    }

    // Getting skipped foods from skipped meal data

    fun getSkippedMeals(skippedMeals: ArrayList<OrderSkipped>) {

        val skippedFoods = ArrayList<OrderUnskip>()

        skippedMeals.forEach { meal ->

            firestore.collection(Constants.COLLECTION_PACKS)
                .document(meal.packId.toString())
                .collection(Constants.COLLECTION_FOODS)
                .whereEqualTo(Constants.FIELD_ID, meal.foodId)
                .get()
                .addOnCompleteListener { task ->

                    if (task.isSuccessful) {

                        val documents = task.result?.documents

                        if (documents != null) {

                            val document = documents[0]

                            val food = OrderServer(
                                id = document!![Constants.FIELD_ID] as Long,
                                name = document[Constants.FIELD_NAME] as String,
                                description = document[Constants.FIELD_DESCRIPTION] as String,
                                price = document[Constants.FIELD_PRICE] as String,
                                day = document[Constants.FIELD_DAY] as String,
                                imageName = document[Constants.FIELD_IMAGE_NAME] as String,
                                packId = document[Constants.FIELD_PACK_ID] as Long
                            )

                            Log.i("SkippedFoodMeal", "$food")

                            val orderUnskip = OrderUnskip(
                                food = food,
                                skippedMeal = meal
                            )

                            skippedFoods.add(orderUnskip)

                            firebaseSourceCallback.skippedMealsFetchSuccessful(skippedFoods)

                        }

                    }

                }

        }

        firebaseSourceCallback.skippedMealsFetchSuccessful(skippedFoods)

    }

    fun skipAMeal(uid: String, userSkippedData: UserSkippedData) {

        firestore.collection(Constants.COLLECTION_SKIPPED)
            .document(uid)
            .set(userSkippedData)
            .addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    Log.i("MealSkip", "Successful")
                } else {
                    Log.i("MealSkip", "Unsuccessful")
                }
            }

    }

    // Unskip a skipped meal

    fun unskipMeal(uid: String, skippedMeal: OrderSkipped) {

        firestore.collection(Constants.COLLECTION_SKIPPED).document(uid)
            .update(Constants.FIELD_SKIPPED_MEALS, FieldValue.arrayRemove(skippedMeal))
            .addOnCompleteListener { task ->
                firebaseSourceCallback.isUnskipSuccessful(task.isSuccessful)
            }

    }

    // Update user credentials

    fun updateUserCredentials(user: User) {

        firestore.collection(Constants.COLLECTION_USERS).document(user.uid)
            .set(user)
            .addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    firebaseSourceCallback.updateUserCredentialsSuccessful(task.isSuccessful)
                } else {
                    firebaseSourceCallback.updateUserCredentialsSuccessful(task.isSuccessful)
                    Log.i("Exception", "Update user credentials")
                    task.exception?.printStackTrace()
                }
            }

    }

}
