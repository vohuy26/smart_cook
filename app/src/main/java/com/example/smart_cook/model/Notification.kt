package com.example.smart_cook.model

data class Notification(

    val title: String = "",

    val message: String = "",

    val createdAt: Long =
        System.currentTimeMillis()
)