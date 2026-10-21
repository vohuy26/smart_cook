package com.example.smart_cook.model

data class Review(

    var id: String = "",

    var userId: String = "",

    var userName: String = "",

    var recipeId: String = "",

    var recipeName: String = "",

    var rating: Float = 0f,

    var comment: String = "",

    var createdAt: Long = 0L,

    var updatedAt: Long = 0L
)