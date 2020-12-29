package com.wenull.homemade.utils.model

data class OrderServer(
    val id: Long,
    val name: String,
    val description: String,
    val price: String,
    val day: String,
    val imageName: String,
    val packId: Long
)