package com.wenull.homemade.utils.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FoodPack(
    val id: Long,
    val name: String,
    val description: String,
    val imageName: String,
    // skipTimeLimit is the hour in 24 hour format after which today's meal cannot be skipped
    val skipTimeLimit: Long
) : Parcelable
