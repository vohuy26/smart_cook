package com.example.smart_cook.ui.my_recipes

import com.example.smart_cook.model.UserRecipe


data class MyRecipesUiState(

    val isLoading: Boolean = false,

    val recipes:
    List<UserRecipe> =
        emptyList(),

    val isEmpty: Boolean = false,

    val error: String? = null
)