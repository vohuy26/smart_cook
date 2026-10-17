package com.example.smart_cook.ui.favorite

import com.example.smart_cook.model.FavoriteRecipe

data class FavoriteUiState(

    val isLoading: Boolean = false,

    val favorites: List<FavoriteRecipe> =
        emptyList(),

    val searchQuery: String = "",

    val isEmpty: Boolean = false,

    val error: String? = null
)