package com.example.smart_cook.ui.home

import com.example.smart_cook.model.Meal

data class HomeUiState(

    val isLoading: Boolean = false,

    val meals: List<Meal> =
        emptyList(),

    val recommendedMeals: List<Meal> =
        emptyList(),

    val favoriteIds: Set<String> =
        emptySet(),

    val error: String? = null
)