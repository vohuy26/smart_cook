package com.example.smart_cook.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smart_cook.model.FavoriteRecipe
import com.example.smart_cook.model.Meal
import com.example.smart_cook.repository.CloudFavoriteRepository
import com.example.smart_cook.repository.FavoriteRepository
import com.example.smart_cook.repository.MealRepository
import com.example.smart_cook.repository.RecommendationRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.smart_cook.model.RecentlyViewed
import com.example.smart_cook.repository.RecentlyViewedRepository
import com.example.smart_cook.repository.UserRepository

class HomeViewModel(

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

) : ViewModel() {

    private val _uiState =
        MutableStateFlow(HomeUiState())

    val uiState:
            StateFlow<HomeUiState> =
        _uiState.asStateFlow()


    private val _favoriteMessage =
        MutableSharedFlow<String>()

    val favoriteMessage:
            SharedFlow<String> =
        _favoriteMessage.asSharedFlow()


    fun loadHome() {

        if (_uiState.value.isLoading)
            return

        _uiState.value =
            _uiState.value.copy(
                isLoading = true,
                error = null
            )

        viewModelScope.launch {

            try {

                val apiMeals =
                    mealRepository.getApiMeals()

                mealRepository.getFirebaseMeals(

                    onSuccess = { firebaseMeals ->

                        val allMeals =
                            mutableListOf<Meal>()

                        allMeals.addAll(
                            apiMeals
                        )

                        allMeals.addAll(
                            firebaseMeals
                        )

                        val sortedMeals =
                            allMeals.sortedByDescending {
                                it.createdAt
                            }

                        publishMeals(
                            sortedMeals
                        )
                    },

                    onError = {

                        publishMeals(
                            apiMeals
                        )
                    }
                )

            } catch (e: Exception) {

                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        error =
                            e.message
                                ?: "Unable to load recipes"
                    )
            }
        }
    }

    fun loadUsername() {

        viewModelScope.launch {

            try {

                val username =
                    userRepository
                        .getUsername()

                _uiState.value =
                    _uiState.value.copy(
                        username = username
                    )

            } catch (e: Exception) {

                _uiState.value =
                    _uiState.value.copy(
                        username = "Smart Cook"
                    )
            }
        }
    }


    fun loadFavorites() {

        viewModelScope.launch {

            try {

                val ids =
                    favoriteRepository
                        .getAllFavoriteIds()

                _uiState.value =
                    _uiState.value.copy(
                        favoriteIds = ids
                    )

            } catch (e: Exception) {

                e.printStackTrace()
            }
        }
    }


    fun toggleFavorite(
        meal: Meal
    ) {

        viewModelScope.launch {

            try {

                val favoriteRecipe =
                    FavoriteRecipe(

                        id = meal.id,

                        title =
                            meal.title
                                ?: "Unknown Recipe",

                        image =
                            meal.image
                    )

                val isFavorite =
                    favoriteRepository
                        .isFavorite(
                            meal.id
                        )


                if (isFavorite) {

                    favoriteRepository
                        .deleteFavorite(
                            favoriteRecipe
                        )

                    _uiState.value =
                        _uiState.value.copy(

                            favoriteIds =
                                _uiState.value
                                    .favoriteIds -
                                        meal.id
                        )

                    try {

                        cloudFavoriteRepository
                            .deleteFavorite(
                                meal
                            )

                        _favoriteMessage.emit(
                            "Removed from favorite"
                        )

                    } catch (e: Exception) {

                        _favoriteMessage.emit(
                            "Removed locally, but Firebase sync failed"
                        )
                    }

                } else {

                    favoriteRepository
                        .insertFavorite(
                            favoriteRecipe
                        )

                    _uiState.value =
                        _uiState.value.copy(

                            favoriteIds =
                                _uiState.value
                                    .favoriteIds +
                                        meal.id
                        )

                    try {

                        cloudFavoriteRepository
                            .addFavorite(
                                meal
                            )

                        _favoriteMessage.emit(
                            "${meal.title} added ❤️"
                        )

                    } catch (e: Exception) {

                        _favoriteMessage.emit(
                            "Saved locally, but Firebase sync failed"
                        )
                    }
                }

            } catch (e: Exception) {

                _favoriteMessage.emit(
                    e.message
                        ?: "Unable to update favorite"
                )
            }
        }
    }


    private fun publishMeals(
        meals: List<Meal>
    ) {

        viewModelScope.launch {

            try {

                val recommendations =
                    recommendationRepository
                        .generateRecommendations(
                            meals
                        )

                _uiState.value =
                    _uiState.value.copy(

                        isLoading = false,

                        meals = meals,

                        recommendedMeals =
                            recommendations,

                        error = null
                    )

            } catch (e: Exception) {

                _uiState.value =
                    _uiState.value.copy(

                        isLoading = false,

                        meals = meals,

                        recommendedMeals =
                            emptyList(),

                        error = null
                    )
            }
        }
    }

    fun loadRecentlyViewed() {

        viewModelScope.launch {

            try {

                val viewedList =
                    recentlyViewedRepository
                        .getAllViewed()

                _uiState.value =
                    _uiState.value.copy(
                        recentlyViewed = viewedList
                    )

            } catch (e: Exception) {

                e.printStackTrace()
            }
        }
    }


    fun addRecentlyViewed(
        meal: Meal
    ) {

        viewModelScope.launch {

            try {

                val viewedRecipe =
                    RecentlyViewed(
                        id = meal.id,
                        title =
                            meal.title
                                ?: "Unknown Recipe",
                        image = meal.image
                    )

                recentlyViewedRepository
                    .insertViewed(
                        viewedRecipe
                    )

                val updatedList =
                    recentlyViewedRepository
                        .getAllViewed()

                _uiState.value =
                    _uiState.value.copy(
                        recentlyViewed =
                            updatedList
                    )

            } catch (e: Exception) {

                e.printStackTrace()
            }
        }
    }

}