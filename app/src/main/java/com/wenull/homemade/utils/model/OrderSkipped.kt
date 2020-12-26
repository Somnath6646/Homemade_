package com.wenull.homemade.utils.model

data class OrderSkipped(
    val name: String,
    val contents: String,
    val price: String,
    val urlToImage: String,
    val user: User,
    val skippedDate: String
)