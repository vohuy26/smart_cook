package com.example.smart_cook.model

data class UserProfile(

    val username: String = "Smart Cook User",

    val email: String = "",

    val avatarUrl: String? = null,

    val isLoggedIn: Boolean = false
)