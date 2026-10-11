package com.example.smart_cook.data.remote

import com.example.smart_cook.model.MealResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MealApi {

    // RANDOM / DEFAULT MEALS

    @GET("search.php?s=")
    suspend fun getMeals():
            Response<MealResponse>

    // SEARCH MEALS

    @GET("search.php")
    suspend fun searchMeals(
        @Query("s") query: String
    ): Response<MealResponse>

    // DETAIL MEAL

    @GET("lookup.php")
    suspend fun getMealDetail(
        @Query("i") id: String
    ): Response<MealResponse>

    @GET("filter.php")
    suspend fun getMealsByCategory(
        @Query("c") category:String
    ): Response<MealResponse>

    @GET("random.php")
    suspend fun getRandomMeal(): Response<MealResponse>
}