package com.example.smart_cook.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.smart_cook.repository.CloudFavoriteRepository
import com.example.smart_cook.repository.FavoriteRepository
import com.example.smart_cook.repository.MealRepository
import com.example.smart_cook.repository.RecommendationRepository

class HomeViewModelFactory(

    private val mealRepository:
    MealRepository,

    private val recommendationRepository:
    RecommendationRepository,

    private val favoriteRepository:
    FavoriteRepository,

    private val cloudFavoriteRepository:
    CloudFavoriteRepository

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

                mealRepository =
                    mealRepository,

                recommendationRepository =
                    recommendationRepository,

                favoriteRepository =
                    favoriteRepository,

                cloudFavoriteRepository =
                    cloudFavoriteRepository

            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}