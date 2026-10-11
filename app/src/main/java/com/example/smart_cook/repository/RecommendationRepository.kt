package com.example.smart_cook.repository

import android.util.Log
import com.example.smart_cook.data.local.SearchHistoryDao
import com.example.smart_cook.model.Meal
import com.example.smart_cook.utils.RecommendationEngine

class RecommendationRepository(
    private val searchDao: SearchHistoryDao
) {

    suspend fun generateRecommendations(
        allMeals: List<Meal>
    ): List<Meal> {

        val histories =
            searchDao.getAllSearchHistory()

        val keywords =
            histories.map {
                it.keyword
            }

        Log.d(
            "AI_RECOMMEND",
            "Keywords: $keywords"
        )

        val result =
            RecommendationEngine
                .getRecommendedMeals(
                    allMeals,
                    keywords
                )

        Log.d(
            "AI_RECOMMEND",
            "Result size = ${result.size}"
        )

        result.forEach {

            Log.d(
                "AI_RECOMMEND",
                "Recommended = ${it.title}"
            )
        }

        return result
    }
}