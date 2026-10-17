package com.example.smart_cook.ui.profile

data class ProfileUiState(

    val isLoading: Boolean = false,

    val username: String = "Smart Cook",

    val email: String = "",

    val avatarUrl: String? = null,

    val favoriteCount: Int = 0,

    val isLoggedIn: Boolean = false,

    val isUploadingAvatar: Boolean = false,

    val error: String? = null
)