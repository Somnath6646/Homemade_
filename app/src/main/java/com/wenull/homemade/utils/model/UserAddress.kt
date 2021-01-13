package com.wenull.homemade.utils.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserAddress(
    val buildingNameOrNumber: String,
    val streetName: String,
    val locality: String,
    val city: String,
    val pincode: String
) : Parcelable