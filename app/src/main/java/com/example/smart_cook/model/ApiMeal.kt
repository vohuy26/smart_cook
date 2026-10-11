package com.example.smart_cook.model


import com.google.gson.annotations.SerializedName


data class ApiMeal(


    @SerializedName("idMeal")
    val id:String,


    @SerializedName("strMeal")
    val title:String?,


    @SerializedName("strMealThumb")
    val image:String,


    @SerializedName("strCategory")
    val category:String?,


    @SerializedName("strArea")
    val area:String?,


    @SerializedName("strInstructions")
    val instructions:String?,

    @SerializedName("strYoutube")
    val youtube:String?

)