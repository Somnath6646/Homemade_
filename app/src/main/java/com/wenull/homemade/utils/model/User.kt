package com.wenull.homemade.utils.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    val uid: String,
    val phoneNumber: String,
    val firstName: String,
    val lastName: String,
    val address: UserAddress,
    val packsEnrolled: ArrayList<Long>,
    val imageName: String
) : Parcelable