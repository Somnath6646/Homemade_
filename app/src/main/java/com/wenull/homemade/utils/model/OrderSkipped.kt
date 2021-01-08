package com.wenull.homemade.utils.model

data class OrderSkipped(
    val date: String,
    val day: String,
    val foodId: Long,
    val packId: Long,
    val skipLimit: Long
)