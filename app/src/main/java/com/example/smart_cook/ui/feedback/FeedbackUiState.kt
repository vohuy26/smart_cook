package com.example.smart_cook.ui.feedback

data class FeedbackUiState(

    val isSubmitting: Boolean = false,

    val comment: String = "",

    val rating: Float = 0f,

    val isSubmitted: Boolean = false,

    val error: String? = null
)