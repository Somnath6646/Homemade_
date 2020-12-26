package com.wenull.homemade

import android.widget.ImageView

data class Pack(
        val imageUrl: String,
        val name: String,
        val shortDescription: String,
        val foodItems: List<FoodItem>?
)