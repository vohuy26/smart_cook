package com.example.smart_cook.model

import com.google.gson.annotations.SerializedName

data class Recipe(

    @SerializedName("idMeal")
    val id: String,

    @SerializedName("strMeal")
    val title: String,

    @SerializedName("strMealThumb")
    val image: String,

    @SerializedName("strInstructions")
    val instructions: String? = "",

    @SerializedName("strYoutube")
    val youtube: String? = ""
)