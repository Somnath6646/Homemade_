package com.wenull.homemade.utils.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class UserSkippedData(
    val uid: String,
    val skippedMeals: @RawValue ArrayList<OrderSkipped>
) : Parcelable