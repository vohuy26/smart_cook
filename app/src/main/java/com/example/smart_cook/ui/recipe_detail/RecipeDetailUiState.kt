package com.example.smart_cook.ui.recipe_detail

import com.example.smart_cook.model.Meal
import com.example.smart_cook.model.Review

data class RecipeDetailUiState(

    val isLoading: Boolean = false,
    val meal: Meal? = null,
    val isFavorite: Boolean = false,

    // =========================
    // COMMENT
    // =========================

    val reviews: List<Review> = emptyList(),
    val myReview: Review? = null,
    val commentInput: String = "",
    val isCommentsLoading: Boolean = false,
    val isSubmittingComment: Boolean = false,

    // RATING
    val myRating: Float = 0f,
    val averageRating: Float = 0f,
    val ratingCount: Int = 0,
    val isSubmittingRating: Boolean = false,

    val error: String? = null
)