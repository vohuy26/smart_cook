package com.example.smart_cook.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.smart_cook.repository.CloudFavoriteRepository
import com.example.smart_cook.repository.FavoriteRepository
import com.example.smart_cook.repository.MealRepository
import com.example.smart_cook.repository.RecentlyViewedRepository
import com.example.smart_cook.repository.RecommendationRepository
import com.example.smart_cook.repository.UserRepository

class HomeViewModelFactory(

    private val mealRepository:
    MealRepository,

    private val recommendationRepository:
    RecommendationRepository,

    private val favoriteRepository:
    FavoriteRepository,

    private val cloudFavoriteRepository:
    CloudFavoriteRepository,

    private val recentlyViewedRepository:
    RecentlyViewedRepository,

    private val userRepository:
    UserRepository

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
                mealRepository = mealRepository,
                recommendationRepository = recommendationRepository,
                favoriteRepository = favoriteRepository,
                cloudFavoriteRepository = cloudFavoriteRepository,
                recentlyViewedRepository = recentlyViewedRepository,
                userRepository = userRepository
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}