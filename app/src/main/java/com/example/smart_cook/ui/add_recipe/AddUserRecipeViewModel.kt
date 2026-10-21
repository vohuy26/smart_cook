package com.example.smart_cook.ui.add_recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.smart_cook.model.UserRecipe
import com.example.smart_cook.repository.RecipeImageRepository
import com.example.smart_cook.repository.RecipeSubmissionRepository
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
class AddUserRecipeViewModel @Inject constructor(

    private val submissionRepository:
    RecipeSubmissionRepository,

    private val imageRepository:
    RecipeImageRepository,

    private val userRepository:
    UserRepository

) : ViewModel() {


    // =========================
    // UI STATE
    // =========================

    private val _uiState =
        MutableStateFlow(
            AddUserRecipeUiState()
        )

    val uiState:
            StateFlow<AddUserRecipeUiState> =
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
    // FORM
    // =========================

    fun onTitleChanged(
        value: String
    ) {

        _uiState.value =
            _uiState.value.copy(
                title = value
            )
    }


    fun onCategoryChanged(
        value: String
    ) {

        _uiState.value =
            _uiState.value.copy(
                category = value
            )
    }


    fun onAreaChanged(
        value: String
    ) {

        _uiState.value =
            _uiState.value.copy(
                area = value
            )
    }


    fun onDescriptionChanged(
        value: String
    ) {

        _uiState.value =
            _uiState.value.copy(
                description = value
            )
    }


    fun onIngredientsChanged(
        value: String
    ) {

        _uiState.value =
            _uiState.value.copy(
                ingredientsText = value
            )
    }


    fun onStepsChanged(
        value: String
    ) {

        _uiState.value =
            _uiState.value.copy(
                stepsText = value
            )
    }


    fun onCookingTimeChanged(
        value: String
    ) {

        _uiState.value =
            _uiState.value.copy(
                cookingTime = value
            )
    }


    fun onServingsChanged(
        value: String
    ) {

        _uiState.value =
            _uiState.value.copy(
                servings = value
            )
    }


    fun onYoutubeChanged(
        value: String
    ) {

        _uiState.value =
            _uiState.value.copy(
                youtube = value
            )
    }


    // =========================
    // IMAGE
    // =========================

    fun onImageSelected(
        uriString: String
    ) {

        _uiState.value =
            _uiState.value.copy(
                imageUri = uriString,
                error = null
            )
    }


    // =========================
    // SUBMIT
    // =========================

    fun submitRecipe() {

        if (
            _uiState.value.isSubmitting
        ) {
            return
        }


        val state =
            _uiState.value


        // =========================
        // VALIDATION
        // =========================

        if (
            state.title.trim().isBlank()
        ) {

            setError(
                "Please enter recipe name."
            )

            return
        }


        if (
            state.category.trim().isBlank()
        ) {

            setError(
                "Please select a category."
            )

            return
        }


        if (
            state.ingredientsText
                .trim()
                .isBlank()
        ) {

            setError(
                "Please enter ingredients."
            )

            return
        }


        if (
            state.stepsText
                .trim()
                .isBlank()
        ) {

            setError(
                "Please enter cooking steps."
            )

            return
        }


        if (
            state.imageUri.isNullOrBlank()
        ) {

            setError(
                "Please select a recipe image."
            )

            return
        }


        val cookingTime =
            state.cookingTime
                .trim()
                .toIntOrNull()


        if (
            cookingTime == null ||
            cookingTime <= 0
        ) {

            setError(
                "Cooking time must be greater than 0."
            )

            return
        }


        val servings =
            state.servings
                .trim()
                .toIntOrNull()


        if (
            servings == null ||
            servings <= 0
        ) {

            setError(
                "Servings must be greater than 0."
            )

            return
        }


        // =========================
        // START SUBMIT
        // =========================

        _uiState.value =
            state.copy(
                isSubmitting = true,
                error = null
            )


        viewModelScope.launch {

            try {

                // =========================
                // UPLOAD IMAGE
                // =========================

                val imageUrl =
                    imageRepository
                        .uploadRecipeImage(
                            state.imageUri
                        )


                // =========================
                // OWNER
                // =========================

                val ownerName =
                    userRepository
                        .getUsername()


                // =========================
                // INGREDIENTS
                // =========================

                val ingredients =
                    parseLines(
                        state.ingredientsText
                    )


                // =========================
                // STEPS
                // =========================

                val steps =
                    parseLines(
                        state.stepsText
                    )


                // Existing Recipe Detail
                // vẫn đọc instructions,
                // nên tạo instructions
                // từ danh sách steps.
                val instructions =
                    steps
                        .mapIndexed { index, step ->

                            "${index + 1}. $step"
                        }
                        .joinToString(
                            separator = "\n"
                        )


                // =========================
                // CREATE RECIPE
                // =========================

                val recipe =
                    UserRecipe(

                        ownerName =
                            ownerName,

                        title =
                            state.title.trim(),

                        category =
                            state.category.trim(),

                        area =
                            state.area.trim(),

                        image =
                            imageUrl,

                        instructions =
                            instructions,

                        youtube =
                            state.youtube.trim(),

                        description =
                            state.description.trim(),

                        ingredients =
                            ingredients,

                        steps =
                            steps,

                        cookingTime =
                            cookingTime,

                        servings =
                            servings
                    )


                // =========================
                // FIRESTORE
                // =========================

                submissionRepository
                    .submitRecipe(
                        recipe
                    )


                // =========================
                // SUCCESS
                // =========================

                _uiState.value =
                    AddUserRecipeUiState(
                        isSubmitted = true
                    )


                _message.emit(
                    "Recipe submitted successfully. Waiting for admin approval."
                )


            } catch (e: Exception) {

                _uiState.value =
                    _uiState.value.copy(
                        isSubmitting = false,
                        error =
                            e.message
                                ?: "Unable to submit recipe."
                    )
            }
        }
    }


    // =========================
    // PARSE MULTILINE
    // =========================

    private fun parseLines(
        value: String
    ): List<String> {

        return value
            .lines()
            .map {
                it.trim()
            }
            .filter {
                it.isNotBlank()
            }
    }


    // =========================
    // ERROR
    // =========================

    private fun setError(
        message: String
    ) {

        _uiState.value =
            _uiState.value.copy(
                error = message
            )
    }


    fun consumeError() {

        _uiState.value =
            _uiState.value.copy(
                error = null
            )
    }


    fun consumeSubmitted() {

        _uiState.value =
            _uiState.value.copy(
                isSubmitted = false
            )
    }
}