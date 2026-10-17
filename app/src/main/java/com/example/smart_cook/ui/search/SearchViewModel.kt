package com.example.smart_cook.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smart_cook.model.FavoriteRecipe
import com.example.smart_cook.model.Meal
import com.example.smart_cook.repository.FavoriteRepository
import com.example.smart_cook.repository.MealRepository
import com.example.smart_cook.repository.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
    private val searchRepository: SearchRepository,
    private val mealRepository: MealRepository
) : ViewModel() {
    private val _uiState =
        MutableStateFlow(
            SearchUiState()
        )

    val uiState:
            StateFlow<SearchUiState> =
        _uiState.asStateFlow()


    private val _message =
        MutableSharedFlow<String>()

    val message:
            SharedFlow<String> =
        _message.asSharedFlow()

    private var searchJob: Job? = null

    private var defaultMealsCache:
            List<Meal> = emptyList()


    fun loadFavorites() {

        viewModelScope.launch {

            try {

                val favoriteIds =
                    favoriteRepository
                        .getAllFavoriteIds()

                _uiState.value =
                    _uiState.value.copy(
                        favoriteIds =
                            favoriteIds
                    )

            } catch (e: Exception) {

                e.printStackTrace()
            }
        }
    }


    fun loadRecentSearches() {

        viewModelScope.launch {

            try {

                val recentSearches =
                    searchRepository
                        .getRecentKeywords()

                _uiState.value =
                    _uiState.value.copy(
                        recentSearches =
                            recentSearches
                    )

            } catch (e: Exception) {

                e.printStackTrace()
            }
        }
    }

    fun saveRecentSearch(
        keyword: String
    ) {

        if (keyword.isBlank()) {
            return
        }

        viewModelScope.launch {

            try {

                searchRepository
                    .saveSearch(
                        keyword
                    )

                val recentSearches =
                    searchRepository
                        .getRecentKeywords()

                _uiState.value =
                    _uiState.value.copy(
                        recentSearches =
                            recentSearches
                    )

            } catch (e: Exception) {

                _message.emit(
                    e.message
                        ?: "Unable to save search history"
                )
            }
        }
    }


    fun toggleFavorite(
        meal: Meal
    ) {

        viewModelScope.launch {

            try {

                val isFavorite =
                    favoriteRepository
                        .isFavorite(
                            meal.id
                        )

                val favorite =
                    FavoriteRecipe(
                        id = meal.id,
                        title = meal.title,
                        image = meal.image
                    )

                if (isFavorite) {

                    favoriteRepository
                        .deleteFavorite(
                            favorite
                        )

                    _uiState.value =
                        _uiState.value.copy(
                            favoriteIds =
                                _uiState
                                    .value
                                    .favoriteIds - meal.id
                        )

                    _message.emit(
                        "Removed Favorite"
                    )

                } else {

                    favoriteRepository
                        .insertFavorite(
                            favorite
                        )

                    _uiState.value =
                        _uiState.value.copy(
                            favoriteIds =
                                _uiState
                                    .value
                                    .favoriteIds + meal.id
                        )

                    _message.emit(
                        "Added Favorite ❤️"
                    )
                }

            } catch (e: Exception) {

                _message.emit(
                    e.message
                        ?: "Favorite error"
                )
            }
        }
    }

    fun onQueryChanged(
        query: String
    ) {

        searchJob?.cancel()

        val keyword =
            query.trim()

        if (keyword.isBlank()) {

            showDefaultRecipes()

            return
        }

        searchJob =
            viewModelScope.launch {

                delay(350)

                performSearch(
                    keyword
                )
            }
    }

    fun searchMeals(
        keyword: String
    ) {

        searchJob?.cancel()

        val cleanKeyword =
            keyword.trim()

        if (cleanKeyword.isBlank()) {

            showDefaultRecipes()

            return
        }

        searchJob =
            viewModelScope.launch {

                performSearch(
                    cleanKeyword
                )
            }
    }

    private suspend fun performSearch(
        keyword: String
    ) {

        _uiState.value =
            _uiState.value.copy(
                isLoading = true,
                error = null
            )

        try {

            val meals =
                mealRepository
                    .searchMeals(
                        keyword
                    )

            _uiState.value =
                _uiState.value.copy(
                    isLoading = false,
                    meals = meals,
                    isEmpty =
                        meals.isEmpty(),
                    error = null
                )

        } catch (e: Exception) {

            _uiState.value =
                _uiState.value.copy(
                    isLoading = false,
                    meals =
                        emptyList(),
                    isEmpty = true,
                    error =
                        e.message
                            ?: "Search failed"
                )
        }
    }

    fun loadDefaultRecipes() {

        if (defaultMealsCache.isNotEmpty()) {

            showDefaultRecipes()

            return
        }

        viewModelScope.launch {

            _uiState.value =
                _uiState.value.copy(
                    isLoading = true,
                    isEmpty = false,
                    error = null
                )

            try {

                val meals =
                    mealRepository
                        .getRandomMeals(
                            count = 10
                        )

                defaultMealsCache =
                    meals

                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        meals = meals,
                        isEmpty =
                            meals.isEmpty(),
                        error = null
                    )

            } catch (e: Exception) {

                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        meals =
                            emptyList(),
                        isEmpty = true,
                        error =
                            e.message
                                ?: "Unable to load recipes"
                    )
            }
        }
    }

    private fun showDefaultRecipes() {

        _uiState.value =
            _uiState.value.copy(
                isLoading = false,
                meals =
                    defaultMealsCache,
                isEmpty =
                    defaultMealsCache.isEmpty(),
                error = null
            )
    }
}