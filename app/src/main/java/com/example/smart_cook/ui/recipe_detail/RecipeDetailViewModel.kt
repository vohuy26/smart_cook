package com.example.smart_cook.ui.recipe_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.smart_cook.repository.FavoriteRepository
import com.example.smart_cook.repository.MealRepository

import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import com.example.smart_cook.model.FavoriteRecipe

import javax.inject.Inject


@HiltViewModel
class RecipeDetailViewModel @Inject constructor(

    private val mealRepository:
    MealRepository,

    private val favoriteRepository:
    FavoriteRepository

) : ViewModel() {


    // =========================
    // UI STATE
    // =========================

    private val _uiState =
        MutableStateFlow(
            RecipeDetailUiState()
        )

    val uiState:
            StateFlow<RecipeDetailUiState> =
        _uiState.asStateFlow()


    // =========================
    // MESSAGE
    // =========================

    private val _message =
        MutableSharedFlow<String>()

    val message:
            SharedFlow<String> =
        _message.asSharedFlow()


    // =========================
    // LOAD RECIPE DETAIL
    // =========================

    fun loadMealDetail(
        mealId: String
    ) {

        if (mealId.isBlank()) {

            _uiState.value =
                _uiState.value.copy(
                    isLoading = false,
                    error = "Invalid recipe ID"
                )

            return
        }


        viewModelScope.launch {

            _uiState.value =
                _uiState.value.copy(
                    isLoading = true,
                    error = null
                )

            try {

                val meal =
                    mealRepository
                        .getMealDetail(
                            mealId
                        )


                if (meal == null) {

                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            meal = null,
                            error = "Recipe not found"
                        )

                    return@launch
                }


                // =========================
                // CHECK FAVORITE
                // =========================

                val isFavorite =
                    favoriteRepository
                        .isFavorite(
                            meal.id
                        )


                // =========================
                // UPDATE STATE
                // =========================

                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        meal = meal,
                        isFavorite = isFavorite,
                        error = null
                    )


            } catch (e: Exception) {

                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        meal = null,
                        error =
                            e.message
                                ?: "Unable to load recipe"
                    )
            }
        }
    }

    fun toggleFavorite() {

        val meal =
            _uiState.value.meal
                ?: return

        viewModelScope.launch {

            try {

                val currentFavorite =
                    _uiState.value.isFavorite


                if (currentFavorite) {

                    // =========================
                    // REMOVE FAVORITE
                    // =========================

                    favoriteRepository
                        .deleteFavorite(
                            FavoriteRecipe(
                                id = meal.id,
                                title = meal.title,
                                image = meal.image
                            )
                        )

                    _uiState.value =
                        _uiState.value.copy(
                            isFavorite = false
                        )

                    _message.emit(
                        "Removed Favorite"
                    )

                } else {

                    // =========================
                    // ADD FAVORITE
                    // =========================

                    favoriteRepository
                        .insertFavorite(
                            FavoriteRecipe(
                                id = meal.id,
                                title = meal.title,
                                image = meal.image
                            )
                        )

                    _uiState.value =
                        _uiState.value.copy(
                            isFavorite = true
                        )

                    _message.emit(
                        "Added Favorite ❤️"
                    )
                }

            } catch (e: Exception) {

                _message.emit(
                    e.message
                        ?: "Unable to update favorite"
                )
            }
        }
    }

    fun refreshFavorite() {

        val meal =
            _uiState.value.meal
                ?: return

        viewModelScope.launch {

            try {

                val isFavorite =
                    favoriteRepository
                        .isFavorite(
                            meal.id
                        )

                _uiState.value =
                    _uiState.value.copy(
                        isFavorite = isFavorite
                    )

            } catch (e: Exception) {

                e.printStackTrace()
            }
        }
    }
}