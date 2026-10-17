package com.example.smart_cook.ui.recipe_detail

import com.example.smart_cook.model.Meal

data class RecipeDetailUiState(

    val isLoading: Boolean = false,

    val meal: Meal? = null,

    val isFavorite: Boolean = false,

    val error: String? = null
)