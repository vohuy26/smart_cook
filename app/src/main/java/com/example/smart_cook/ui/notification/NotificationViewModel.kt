package com.example.smart_cook.ui.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.smart_cook.repository.NotificationRepository

import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import javax.inject.Inject


@HiltViewModel
class NotificationViewModel @Inject constructor(

    private val notificationRepository:
    NotificationRepository

) : ViewModel() {


    // =========================
    // UI STATE
    // =========================

    private val _uiState =
        MutableStateFlow(
            NotificationUiState()
        )

    val uiState:
            StateFlow<NotificationUiState> =
        _uiState.asStateFlow()


    // =========================
    // LOAD NOTIFICATIONS
    // =========================

    fun loadNotifications() {

        viewModelScope.launch {

            _uiState.value =
                _uiState.value.copy(
                    isLoading = true,
                    error = null
                )

            try {

                val notifications =
                    notificationRepository
                        .getNotifications()


                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        notifications =
                            notifications,
                        isEmpty =
                            notifications.isEmpty(),
                        error = null
                    )


            } catch (e: Exception) {

                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        notifications =
                            emptyList(),
                        isEmpty = true,
                        error =
                            e.message
                                ?: "Unable to load notifications"
                    )
            }
        }
    }
}