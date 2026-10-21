package com.example.smart_cook.model

data class RecipeRanking(

    val recipeId: String = "",

    val recipeName: String = "",

    val averageRating: Float = 0f,

    val ratingCount: Int = 0,

    val commentCount: Int = 0
)