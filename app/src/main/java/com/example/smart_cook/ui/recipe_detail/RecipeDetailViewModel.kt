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
import com.example.smart_cook.repository.ReviewRepository

import com.example.smart_cook.model.FavoriteRecipe

import javax.inject.Inject


@HiltViewModel
class RecipeDetailViewModel @Inject constructor(

    private val mealRepository:
    MealRepository,

    private val favoriteRepository:
    FavoriteRepository,

    private val reviewRepository:
    ReviewRepository

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
                loadReviewSection(
                    meal.id
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

    private suspend fun loadReviewSection(
        recipeId: String
    ) {

        _uiState.value =
            _uiState.value.copy(
                isCommentsLoading = true
            )

        try {

            val reviews =
                reviewRepository
                    .getReviews(
                        recipeId
                    )

            val myReview =
                reviewRepository
                    .getMyReview(
                        recipeId
                    )

            val ratingSummary =
                reviewRepository
                    .getRatingSummary(
                        recipeId
                    )


            _uiState.value =
                _uiState.value.copy(

                    reviews =
                        reviews,

                    myReview =
                        myReview,

                    commentInput =
                        myReview
                            ?.comment
                            .orEmpty(),

                    myRating =
                        myReview
                            ?.rating
                            ?: 0f,

                    averageRating =
                        ratingSummary.averageRating,

                    ratingCount =
                        ratingSummary.ratingCount,

                    isCommentsLoading =
                        false
                )


        } catch (e: Exception) {

            _uiState.value =
                _uiState.value.copy(
                    isCommentsLoading = false
                )

            _message.emit(
                e.message
                    ?: "Không thể tải bình luận"
            )
        }
    }

    fun onCommentChanged(
        value: String
    ) {

        _uiState.value =
            _uiState.value.copy(
                commentInput = value
            )
    }

    fun submitComment() {

        val meal =
            _uiState.value.meal
                ?: return


        val comment =
            _uiState.value
                .commentInput
                .trim()


        if (comment.isBlank()) {

            viewModelScope.launch {

                _message.emit(
                    "Vui lòng nhập nội dung bình luận"
                )
            }

            return
        }


        viewModelScope.launch {

            _uiState.value =
                _uiState.value.copy(
                    isSubmittingComment = true
                )

            try {

                reviewRepository
                    .saveComment(

                        recipeId =
                            meal.id,

                        recipeName =
                            meal.title,

                        comment =
                            comment
                    )


                loadReviewSection(
                    meal.id
                )


                _uiState.value =
                    _uiState.value.copy(
                        isSubmittingComment = false
                    )


                _message.emit(
                    "Đã lưu bình luận"
                )


            } catch (e: Exception) {

                _uiState.value =
                    _uiState.value.copy(
                        isSubmittingComment = false
                    )

                _message.emit(
                    e.message
                        ?: "Không thể lưu bình luận"
                )
            }
        }
    }

    fun deleteMyComment() {

        val meal =
            _uiState.value.meal
                ?: return


        val myReview =
            _uiState.value.myReview
                ?: return


        if (
            myReview.comment.isBlank()
        ) {

            return
        }


        viewModelScope.launch {

            _uiState.value =
                _uiState.value.copy(
                    isSubmittingComment = true
                )

            try {

                reviewRepository
                    .deleteMyComment(
                        meal.id
                    )


                loadReviewSection(
                    meal.id
                )


                _uiState.value =
                    _uiState.value.copy(
                        commentInput = "",
                        isSubmittingComment = false
                    )


                _message.emit(
                    "Đã xóa bình luận"
                )


            } catch (e: Exception) {

                _uiState.value =
                    _uiState.value.copy(
                        isSubmittingComment = false
                    )

                _message.emit(
                    e.message
                        ?: "Không thể xóa bình luận"
                )
            }
        }
    }

    fun refreshComments() {

        val meal =
            _uiState.value.meal
                ?: return


        viewModelScope.launch {

            loadReviewSection(
                meal.id
            )
        }
    }

    fun submitRating(
        rating: Float
    ) {

        val meal =
            _uiState.value.meal
                ?: return

        if (
            rating < 1f ||
            rating > 5f
        ) {

            viewModelScope.launch {
                _message.emit(
                    "Vui lòng chọn từ 1 đến 5 sao"
                )
            }

            return
        }

        viewModelScope.launch {

            _uiState.value =
                _uiState.value.copy(
                    isSubmittingRating = true
                )

            try {

                reviewRepository
                    .saveRating(
                        recipeId =
                            meal.id,

                        recipeName =
                            meal.title,

                        rating =
                            rating
                    )

                loadReviewSection(
                    meal.id
                )

                _uiState.value =
                    _uiState.value.copy(
                        isSubmittingRating = false
                    )

                _message.emit(
                    "Đã đánh giá ${rating.toInt()} sao ⭐"
                )

            } catch (e: Exception) {

                _uiState.value =
                    _uiState.value.copy(
                        isSubmittingRating = false
                    )

                _message.emit(
                    e.message
                        ?: "Không thể lưu đánh giá"
                )
            }
        }
    }

    fun removeRating() {

        val meal =
            _uiState.value.meal
                ?: return

        viewModelScope.launch {

            _uiState.value =
                _uiState.value.copy(
                    isSubmittingRating = true
                )

            try {

                reviewRepository
                    .removeRating(
                        meal.id
                    )

                loadReviewSection(
                    meal.id
                )

                _uiState.value =
                    _uiState.value.copy(
                        isSubmittingRating = false
                    )

                _message.emit(
                    "Đã xóa đánh giá"
                )

            } catch (e: Exception) {

                _uiState.value =
                    _uiState.value.copy(
                        isSubmittingRating = false
                    )

                _message.emit(
                    e.message
                        ?: "Không thể xóa đánh giá"
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