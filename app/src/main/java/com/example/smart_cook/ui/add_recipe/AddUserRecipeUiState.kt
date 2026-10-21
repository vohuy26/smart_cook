package com.example.smart_cook.ui.add_recipe


data class AddUserRecipeUiState(

    // =========================
    // FORM
    // =========================

    val title: String = "",

    val category: String = "",

    val area: String = "",

    val description: String = "",

    val ingredientsText: String = "",

    val stepsText: String = "",

    val cookingTime: String = "",

    val servings: String = "1",

    val youtube: String = "",


    // =========================
    // IMAGE
    // =========================

    val imageUri: String? = null,

    val imageUrl: String? = null,


    // =========================
    // STATE
    // =========================

    val isSubmitting: Boolean = false,

    val isSubmitted: Boolean = false,

    val error: String? = null
)