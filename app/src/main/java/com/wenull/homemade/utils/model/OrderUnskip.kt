package com.wenull.homemade.utils.model

data class OrderUnskip(
    val food: OrderServer,
    val skippedMeal: OrderSkipped
)