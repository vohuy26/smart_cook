package com.example.smart_cook.ui.my_recipes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.smart_cook.repository.RecipeSubmissionRepository

import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

import kotlinx.coroutines.launch

import javax.inject.Inject


@HiltViewModel
class MyRecipesViewModel @Inject constructor(

    private val repository:
    RecipeSubmissionRepository

) : ViewModel() {


    private val _uiState =
        MutableStateFlow(
            MyRecipesUiState()
        )

    val uiState:
            StateFlow<MyRecipesUiState> =
        _uiState.asStateFlow()


    fun loadMyRecipes() {

        viewModelScope.launch {

            _uiState.value =
                _uiState.value.copy(
                    isLoading = true,
                    error = null
                )


            try {

                val recipes =
                    repository
                        .getMyRecipes()


                _uiState.value =
                    MyRecipesUiState(

                        isLoading = false,

                        recipes =
                            recipes,

                        isEmpty =
                            recipes.isEmpty()
                    )


            } catch (e: Exception) {

                _uiState.value =
                    _uiState.value.copy(

                        isLoading = false,

                        error =
                            e.message
                                ?: "Unable to load your recipes."
                    )
            }
        }
    }


    fun consumeError() {

        _uiState.value =
            _uiState.value.copy(
                error = null
            )
    }
}