package com.wenull.homemade.utils.model

data class OrderUser(
    val name: String,
    val contents: String,
    val price: String,
    @field:JvmField
    val hasSkipped: Boolean,
    val extras: Array<String>,
    val extrasPrice: Array<String>,
    val skippedDate: String
)