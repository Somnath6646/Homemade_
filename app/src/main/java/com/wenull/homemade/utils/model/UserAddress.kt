package com.wenull.homemade.utils.model

data class UserAddress(
    val buildingNameOrNumber: String,
    val streetName: String,
    val locality: String,
    val city: String,
    val pincode: String
)