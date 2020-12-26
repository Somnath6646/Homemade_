package com.wenull.homemade.utils.model

data class User(
    val uid: String,
    val phoneNumber: String,
    val firstName: String,
    val lastName: String,
    val address: UserAddress,
    @field:JvmField
    val isEnrolled: Boolean,
    val imageName: String
)