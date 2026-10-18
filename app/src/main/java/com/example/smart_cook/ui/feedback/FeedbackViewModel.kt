package com.example.smart_cook.ui.feedback

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.smart_cook.repository.FeedbackRepository

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
class FeedbackViewModel @Inject constructor(

    private val feedbackRepository:
    FeedbackRepository

) : ViewModel() {


    // =========================
    // UI STATE
    // =========================

    private val _uiState =
        MutableStateFlow(
            FeedbackUiState()
        )

    val uiState:
            StateFlow<FeedbackUiState> =
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
    // UPDATE COMMENT
    // =========================

    fun onCommentChanged(
        comment: String
    ) {

        _uiState.value =
            _uiState.value.copy(
                comment = comment,
                error = null
            )
    }


    // =========================
    // UPDATE RATING
    // =========================

    fun onRatingChanged(
        rating: Float
    ) {

        _uiState.value =
            _uiState.value.copy(
                rating = rating,
                error = null
            )
    }


    // =========================
    // SUBMIT
    // =========================

    fun submitFeedback() {

        val currentState =
            _uiState.value

        val comment =
            currentState.comment.trim()

        val rating =
            currentState.rating


        // =========================
        // VALIDATION
        // =========================

        if (rating <= 0f) {

            _uiState.value =
                currentState.copy(
                    error =
                        "Please select a rating"
                )

            return
        }


        if (comment.isBlank()) {

            _uiState.value =
                currentState.copy(
                    error =
                        "Please enter your feedback"
                )

            return
        }


        if (currentState.isSubmitting) {
            return
        }


        // =========================
        // SUBMIT FIREBASE
        // =========================

        viewModelScope.launch {

            _uiState.value =
                _uiState.value.copy(
                    isSubmitting = true,
                    error = null
                )

            try {

                feedbackRepository
                    .submitFeedback(
                        rating = rating,
                        comment = comment
                    )


                _uiState.value =
                    FeedbackUiState(
                        isSubmitting = false,
                        isSubmitted = true
                    )


                _message.emit(
                    "Thank you for your feedback ❤️"
                )


            } catch (e: Exception) {

                _uiState.value =
                    _uiState.value.copy(
                        isSubmitting = false,
                        isSubmitted = false,
                        error =
                            e.message
                                ?: "Unable to submit feedback"
                    )
            }
        }
    }


    // =========================
    // CONSUME SUBMITTED STATE
    // =========================

    fun consumeSubmitted() {

        if (!_uiState.value.isSubmitted) {
            return
        }

        _uiState.value =
            _uiState.value.copy(
                isSubmitted = false
            )
    }
}