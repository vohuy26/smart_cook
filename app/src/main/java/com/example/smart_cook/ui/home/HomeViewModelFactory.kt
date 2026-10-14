package com.example.smart_cook.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.smart_cook.repository.MealRepository
import com.example.smart_cook.repository.RecommendationRepository

class HomeViewModelFactory(
    private val mealRepository: MealRepository,
    private val recommendationRepository:
        RecommendationRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (
            modelClass.isAssignableFrom(
                HomeViewModel::class.java
            )
        ) {

            return HomeViewModel(
                mealRepository,
                recommendationRepository
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}