package com.example.smart_cook.ui.admin.model

data class AdminRecipe(

    val id: String = "",

    val title: String = "",

    val image: String = "",

    val category: String = "",

    val area: String = "",

    val instructions: String = "",

    val youtube: String = "",

    val source: String = "firebase"
)