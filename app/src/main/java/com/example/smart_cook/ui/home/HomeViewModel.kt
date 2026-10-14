package com.example.smart_cook.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smart_cook.model.Meal
import com.example.smart_cook.repository.MealRepository
import com.example.smart_cook.repository.RecommendationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val mealRepository: MealRepository,
    private val recommendationRepository: RecommendationRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(HomeUiState())

    val uiState: StateFlow<HomeUiState> =
        _uiState.asStateFlow()

    fun loadHome() {

        if (_uiState.value.isLoading) {
            return
        }

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
                            (apiMeals + firebaseMeals)
                                .sortedByDescending {
                                    it.createdAt
                                }

                        publishMeals(allMeals)
                    },

                    onError = {

                        // Firebase lỗi nhưng API vẫn chạy được
                        publishMeals(apiMeals)
                    }
                )

            } catch (e: Exception) {

                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        error = e.message
                            ?: "Unable to load recipes"
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
                    HomeUiState(
                        isLoading = false,
                        meals = meals,
                        recommendedMeals =
                            recommendations,
                        error = null
                    )

            } catch (e: Exception) {

                // Recommendation lỗi
                // vẫn hiển thị danh sách món ăn.

                _uiState.value =
                    HomeUiState(
                        isLoading = false,
                        meals = meals,
                        recommendedMeals =
                            emptyList(),
                        error = null
                    )
            }
        }
    }
}