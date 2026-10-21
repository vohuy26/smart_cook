package com.example.smart_cook.ui.admin.approval

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.smart_cook.repository.AdminRecipeApprovalRepository

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
class AdminRecipeApprovalViewModel
@Inject constructor(

    private val repository:
    AdminRecipeApprovalRepository

) : ViewModel() {


    // =========================
    // STATE
    // =========================

    private val _uiState =
        MutableStateFlow(
            AdminRecipeApprovalUiState()
        )

    val uiState:
            StateFlow<AdminRecipeApprovalUiState> =
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
    // LOAD
    // =========================

    fun loadPendingRecipes() {

        viewModelScope.launch {

            _uiState.value =
                _uiState.value.copy(
                    isLoading = true,
                    error = null
                )


            try {

                val recipes =
                    repository
                        .getPendingRecipes()


                _uiState.value =
                    _uiState.value.copy(

                        isLoading = false,

                        pendingRecipes =
                            recipes,

                        isEmpty =
                            recipes.isEmpty(),

                        error = null
                    )


            } catch (e: Exception) {

                _uiState.value =
                    _uiState.value.copy(

                        isLoading = false,

                        error =
                            e.message
                                ?: "Unable to load pending recipes."
                    )
            }
        }
    }


    // =========================
    // APPROVE
    // =========================

    fun approveRecipe(
        recipeId: String
    ) {

        if (
            _uiState.value
                .processingRecipeId != null
        ) {
            return
        }


        viewModelScope.launch {

            _uiState.value =
                _uiState.value.copy(
                    processingRecipeId =
                        recipeId,
                    error = null
                )


            try {

                repository
                    .approveRecipe(
                        recipeId
                    )


                removeRecipeFromPending(
                    recipeId
                )


                _message.emit(
                    "Recipe approved successfully."
                )


            } catch (e: Exception) {

                _uiState.value =
                    _uiState.value.copy(

                        processingRecipeId =
                            null,

                        error =
                            e.message
                                ?: "Unable to approve recipe."
                    )
            }
        }
    }


    // =========================
    // REJECT
    // =========================

    fun rejectRecipe(
        recipeId: String,
        reason: String
    ) {

        if (
            _uiState.value
                .processingRecipeId != null
        ) {
            return
        }


        if (
            reason.trim().isBlank()
        ) {

            _uiState.value =
                _uiState.value.copy(
                    error =
                        "Please enter a rejection reason."
                )

            return
        }


        viewModelScope.launch {

            _uiState.value =
                _uiState.value.copy(
                    processingRecipeId =
                        recipeId,
                    error = null
                )


            try {

                repository
                    .rejectRecipe(
                        recipeId,
                        reason
                    )


                removeRecipeFromPending(
                    recipeId
                )


                _message.emit(
                    "Recipe rejected."
                )


            } catch (e: Exception) {

                _uiState.value =
                    _uiState.value.copy(

                        processingRecipeId =
                            null,

                        error =
                            e.message
                                ?: "Unable to reject recipe."
                    )
            }
        }
    }


    // =========================
    // REMOVE FROM PENDING
    // =========================

    private fun removeRecipeFromPending(
        recipeId: String
    ) {

        val newList =
            _uiState.value
                .pendingRecipes
                .filterNot {
                    it.id == recipeId
                }


        _uiState.value =
            _uiState.value.copy(

                pendingRecipes =
                    newList,

                processingRecipeId =
                    null,

                isEmpty =
                    newList.isEmpty(),

                error =
                    null
            )
    }


    // =========================
    // ERROR
    // =========================

    fun consumeError() {

        _uiState.value =
            _uiState.value.copy(
                error = null
            )
    }
}