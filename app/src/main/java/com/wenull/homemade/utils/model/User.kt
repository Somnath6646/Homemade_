package com.wenull.homemade.utils.model

data class User(
    val uid: String,
    val phoneNumber: String,
    val firstName: String,
    val lastName: String,
    val address: UserAddress,
    val packsEnrolled: ArrayList<Long>,
    val imageName: String
)