package com.example.smart_cook.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smart_cook.repository.AvatarRepository
import com.example.smart_cook.repository.FavoriteRepository
import com.example.smart_cook.repository.UserRepository
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
class ProfileViewModel @Inject constructor(

    private val userRepository:
    UserRepository,

    private val favoriteRepository:
    FavoriteRepository,

    private val avatarRepository:
    AvatarRepository

) : ViewModel() {


    // =========================
    // UI STATE
    // =========================

    private val _uiState =
        MutableStateFlow(
            ProfileUiState()
        )

    val uiState:
            StateFlow<ProfileUiState> =
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
    // LOAD PROFILE
    // =========================

    fun loadProfile() {

        viewModelScope.launch {

            _uiState.value =
                _uiState.value.copy(
                    isLoading = true,
                    error = null
                )

            try {

                val profile =
                    userRepository
                        .getUserProfile()

                val favoriteCount =
                    try {

                        favoriteRepository
                            .getAllFavorites()
                            .size

                    } catch (e: Exception) {

                        0
                    }


                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        username =
                            profile.username,
                        email =
                            profile.email,
                        avatarUrl =
                            profile.avatarUrl,
                        favoriteCount =
                            favoriteCount,
                        isLoggedIn =
                            profile.isLoggedIn,
                        error = null
                    )


            } catch (e: Exception) {

                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        error =
                            e.message
                                ?: "Unable to load profile"
                    )
            }
        }
    }

    // =========================
    // LOGOUT
    // =========================

    fun logout() {

        userRepository
            .logout()

        _uiState.value =
            _uiState.value.copy(
                isLoggedIn = false
            )
    }

    fun uploadAvatar(
        uriString: String
    ) {

        if (uriString.isBlank()) {
            return
        }


        viewModelScope.launch {

            _uiState.value =
                _uiState.value.copy()


            try {

                // =========================
                // UPLOAD CLOUDINARY
                // =========================

                val imageUrl =
                    avatarRepository
                        .uploadAvatar(
                            uriString
                        )


                // =========================
                // SAVE FIRESTORE
                // =========================

                userRepository
                    .saveAvatarUrl(
                        imageUrl
                    )


                // =========================
                // UPDATE UI STATE
                // =========================

                _uiState.value =
                    _uiState.value.copy(
                        avatarUrl =
                            imageUrl,
                    )


                _message.emit(
                    "Avatar updated"
                )


            } catch (e: Exception) {

                _uiState.value =
                    _uiState.value.copy(
                        isUploadingAvatar =
                            false
                    )

                _message.emit(
                    e.message
                        ?: "Unable to upload avatar"
                )
            }
        }
    }
}