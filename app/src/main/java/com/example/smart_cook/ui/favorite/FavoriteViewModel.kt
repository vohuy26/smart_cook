package com.example.smart_cook.ui.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.smart_cook.model.FavoriteRecipe
import com.example.smart_cook.repository.FavoriteRepository

import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import javax.inject.Inject


@HiltViewModel
class FavoriteViewModel @Inject constructor(

    private val favoriteRepository:
    FavoriteRepository

) : ViewModel() {


    // =========================
    // ALL FAVORITES
    // =========================

    private var allFavorites:
            List<FavoriteRecipe> =
        emptyList()


    // =========================
    // UI STATE
    // =========================

    private val _uiState =
        MutableStateFlow(
            FavoriteUiState()
        )

    val uiState:
            StateFlow<FavoriteUiState> =
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
    // LOAD FAVORITES
    // =========================

    fun loadFavorites() {

        viewModelScope.launch {

            _uiState.value =
                _uiState.value.copy(
                    isLoading = true,
                    error = null
                )

            try {

                val favorites =
                    favoriteRepository
                        .getAllFavorites()

                allFavorites =
                    favorites

                applySearch(
                    _uiState.value.searchQuery
                )

            } catch (e: Exception) {

                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        favorites = emptyList(),
                        isEmpty = true,
                        error =
                            e.message
                                ?: "Unable to load favorites"
                    )
            }
        }
    }


    // =========================
    // SEARCH
    // =========================

    fun searchFavorite(
        keyword: String
    ) {

        val cleanKeyword =
            keyword.trim()

        _uiState.value =
            _uiState.value.copy(
                searchQuery = cleanKeyword
            )

        applySearch(
            cleanKeyword
        )
    }


    private fun applySearch(
        keyword: String
    ) {

        val filteredList =

            if (keyword.isBlank()) {

                allFavorites

            } else {

                allFavorites.filter {

                    it.title.contains(
                        keyword,
                        ignoreCase = true
                    )
                }
            }


        _uiState.value =
            _uiState.value.copy(
                isLoading = false,
                favorites = filteredList,
                searchQuery = keyword,
                isEmpty = filteredList.isEmpty(),
                error = null
            )
    }


    // =========================
    // DELETE FAVORITE
    // =========================

    fun deleteFavorite(
        recipe: FavoriteRecipe
    ) {

        viewModelScope.launch {

            try {

                favoriteRepository
                    .deleteFavorite(
                        recipe
                    )

                allFavorites =
                    allFavorites.filterNot {
                        it.id == recipe.id
                    }

                applySearch(
                    _uiState.value.searchQuery
                )

                _message.emit(
                    "Removed from favorite ❤️"
                )

            } catch (e: Exception) {

                _message.emit(
                    e.message
                        ?: "Unable to remove favorite"
                )
            }
        }
    }
}