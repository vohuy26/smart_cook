package com.example.smart_cook.ui.notification

import com.example.smart_cook.model.Notification

data class NotificationUiState(

    val isLoading: Boolean = false,

    val notifications:
    List<Notification> = emptyList(),

    val isEmpty: Boolean = false,

    val error: String? = null
)