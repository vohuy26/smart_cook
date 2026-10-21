package com.example.smart_cook.ui.admin.approval

import com.example.smart_cook.model.UserRecipe


data class AdminRecipeApprovalUiState(

    val isLoading: Boolean = false,

    val pendingRecipes:
    List<UserRecipe> =
        emptyList(),

    val processingRecipeId:
    String? = null,

    val isEmpty: Boolean = false,

    val error: String? = null
)