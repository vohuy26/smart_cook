package com.example.smart_cook.model

data class Feedback(

    val uid: String = "",

    val username: String = "",

    val rating: Float = 0f,

    val comment: String = "",

    val createdAt: Long =
        System.currentTimeMillis()
)