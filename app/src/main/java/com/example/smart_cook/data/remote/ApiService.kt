package com.example.smart_cook.data.remote

import com.example.smart_cook.model.MealResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    // =========================
    // RANDOM / DEFAULT MEALS
    // =========================

    @GET("search.php?s=")
    suspend fun getMeals():
            Response<MealResponse>

    // =========================
    // SEARCH MEALS
    // =========================

    @GET("search.php")
    suspend fun searchMeals(
        @Query("s")
        query: String
    ): Response<MealResponse>

    // =========================
    // DETAIL MEAL
    // =========================

    @GET("lookup.php")
    suspend fun getMealDetail(
        @Query("i")
        id: String
    ): Response<MealResponse>
}