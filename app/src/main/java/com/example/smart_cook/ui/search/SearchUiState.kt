package com.example.smart_cook.ui.search

import com.example.smart_cook.model.Meal

data class SearchUiState(

    val isLoading: Boolean = false,

    val meals: List<Meal> =
        emptyList(),

    val favoriteIds: Set<String> =
        emptySet(),

    val recentSearches: List<String> =
        emptyList(),

    val isEmpty: Boolean = false,

    val error: String? = null
)