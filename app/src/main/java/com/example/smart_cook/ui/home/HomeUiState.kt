package com.example.smart_cook.ui.home

import com.example.smart_cook.model.Meal

import com.example.smart_cook.model.RecentlyViewed

import com.example.smart_cook.repository.RecentlyViewedRepository

data class HomeUiState(

    val isLoading: Boolean = false,

    val username: String = "Smart Cook",

    val meals: List<Meal> =
        emptyList(),

    val recommendedMeals: List<Meal> =
        emptyList(),

    val favoriteIds: Set<String> =
        emptySet(),

    val recentlyViewed: List<RecentlyViewed> =
        emptyList(),

    val error: String? = null
)