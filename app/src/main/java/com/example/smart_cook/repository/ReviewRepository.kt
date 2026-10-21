package com.example.smart_cook.repository

import com.example.smart_cook.model.Review

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

import kotlinx.coroutines.tasks.await
import com.example.smart_cook.model.RatingSummary
import com.google.firebase.firestore.DocumentSnapshot

import javax.inject.Inject


class ReviewRepository @Inject constructor(

    private val userRepository:
    UserRepository

) {

    private val firestore =
        FirebaseFirestore.getInstance()

    private val auth =
        FirebaseAuth.getInstance()


    // =========================
    // REVIEW ID
    // =========================

    private fun reviewId(
        recipeId: String,
        userId: String
    ): String {

        return "${recipeId}_${userId}"
    }


    // =========================
    // GET COMMENTS
    // =========================

    suspend fun getReviews(
        recipeId: String
    ): List<Review> {

        if (recipeId.isBlank()) {
            return emptyList()
        }


        val snapshot =
            firestore
                .collection("reviews")
                .whereEqualTo(
                    "recipeId",
                    recipeId
                )
                .get()
                .await()


        return snapshot
            .documents
            .mapNotNull { document ->

                document
                    .toObject(
                        Review::class.java
                    )
                    ?.apply {

                        id =
                            document.id
                    }
            }

            // Phase 12 có thể tồn tại
            // rating nhưng không comment.
            .filter {
                it.comment.isNotBlank()
            }

            .sortedByDescending {
                if (it.updatedAt > 0L) {
                    it.updatedAt
                } else {
                    it.createdAt
                }
            }
    }

    private fun documentToReview(
        document: DocumentSnapshot
    ): Review {

        val ratingValue =
            document.get("rating")

        val rating =
            when (ratingValue) {

                is Number ->
                    ratingValue.toFloat()

                is String ->
                    ratingValue
                        .toFloatOrNull()
                        ?: 0f

                else ->
                    0f
            }


        val createdAt =
            when (
                val value =
                    document.get("createdAt")
            ) {

                is Number ->
                    value.toLong()

                is String ->
                    value.toLongOrNull()
                        ?: 0L

                else ->
                    0L
            }


        val updatedAt =
            when (
                val value =
                    document.get("updatedAt")
            ) {

                is Number ->
                    value.toLong()

                is String ->
                    value.toLongOrNull()
                        ?: 0L

                else ->
                    0L
            }


        return Review(

            id =
                document.id,

            userId =
                document
                    .getString("userId")
                    .orEmpty(),

            userName =
                document
                    .getString("userName")
                    .orEmpty(),

            recipeId =
                document
                    .getString("recipeId")
                    .orEmpty(),

            recipeName =
                document
                    .getString("recipeName")
                    .orEmpty(),

            rating =
                rating,

            comment =
                document
                    .getString("comment")
                    .orEmpty(),

            createdAt =
                createdAt,

            updatedAt =
                updatedAt
        )
    }


    // =========================
    // GET MY REVIEW
    // =========================

    suspend fun getMyReview(
        recipeId: String
    ): Review? {

        val user =
            auth.currentUser
                ?: return null


        val id =
            reviewId(
                recipeId,
                user.uid
            )


        val document =
            firestore
                .collection("reviews")
                .document(id)
                .get()
                .await()


        if (!document.exists()) {
            return null
        }


        return documentToReview(
            document
        )
    }


    // =========================
    // SAVE / UPDATE COMMENT
    // =========================

    suspend fun saveComment(
        recipeId: String,
        recipeName: String,
        comment: String
    ): Review {

        val user =
            auth.currentUser
                ?: throw IllegalStateException(
                    "Bạn cần đăng nhập để bình luận."
                )


        val cleanComment =
            comment.trim()


        if (cleanComment.isBlank()) {

            throw IllegalArgumentException(
                "Nội dung bình luận không được để trống."
            )
        }


        if (cleanComment.length > 1000) {

            throw IllegalArgumentException(
                "Bình luận tối đa 1000 ký tự."
            )
        }


        val id =
            reviewId(
                recipeId,
                user.uid
            )


        val reference =
            firestore
                .collection("reviews")
                .document(id)


        val existing =
            reference
                .get()
                .await()


        val now =
            System.currentTimeMillis()


        // Giữ rating cũ.
        // Phase 12 sẽ sử dụng field này.
        val currentRating =
            existing
                .getDouble("rating")
                ?.toFloat()
                ?: 0f


        val createdAt =
            existing
                .getLong("createdAt")
                ?: now


        val userName =
            userRepository
                .getUsername()
                .ifBlank {
                    user.email
                        ?.substringBefore("@")
                        ?: "User"
                }


        val review =
            Review(

                id = id,

                userId =
                    user.uid,

                userName =
                    userName,

                recipeId =
                    recipeId,

                recipeName =
                    recipeName,

                rating =
                    currentRating,

                comment =
                    cleanComment,

                createdAt =
                    createdAt,

                updatedAt =
                    now
            )


        reference
            .set(
                review,
                SetOptions.merge()
            )
            .await()


        return review
    }


    // =========================
    // DELETE MY COMMENT
    // =========================

    suspend fun deleteMyComment(
        recipeId: String
    ) {

        val user =
            auth.currentUser
                ?: throw IllegalStateException(
                    "Bạn chưa đăng nhập."
                )


        val id =
            reviewId(
                recipeId,
                user.uid
            )


        val reference =
            firestore
                .collection("reviews")
                .document(id)


        val snapshot =
            reference
                .get()
                .await()


        if (!snapshot.exists()) {
            return
        }


        val rating =
            snapshot
                .getDouble("rating")
                ?.toFloat()
                ?: 0f


        if (rating > 0f) {

            // Có rating thì chỉ xóa comment,
            // không xóa review vì Phase 12 cần rating.
            reference
                .update(
                    mapOf(

                        "comment" to "",

                        "updatedAt" to
                                System.currentTimeMillis()
                    )
                )
                .await()

        } else {

            // Chưa rating + xóa comment
            // => document không còn giá trị.
            reference
                .delete()
                .await()
        }
    }

    suspend fun getAllRecipeReviews(
        recipeId: String
    ): List<Review> {

        if (recipeId.isBlank()) {
            return emptyList()
        }

        val snapshot =
            firestore
                .collection("reviews")
                .whereEqualTo(
                    "recipeId",
                    recipeId
                )
                .get()
                .await()

        return snapshot
            .documents
            .map { document ->

                documentToReview(
                    document
                )
            }
    }

    suspend fun saveRating(
        recipeId: String,
        recipeName: String,
        rating: Float
    ): Review {

        val user =
            auth.currentUser
                ?: throw IllegalStateException(
                    "Bạn cần đăng nhập để đánh giá."
                )

        if (
            rating < 1f ||
            rating > 5f
        ) {

            throw IllegalArgumentException(
                "Đánh giá phải từ 1 đến 5 sao."
            )
        }

        val id =
            reviewId(
                recipeId,
                user.uid
            )

        val reference =
            firestore
                .collection("reviews")
                .document(id)

        val existing =
            reference
                .get()
                .await()

        val now =
            System.currentTimeMillis()

        val createdAt =
            existing
                .getLong("createdAt")
                ?: now

        // Giữ comment cũ nếu User đã bình luận.
        val currentComment =
            existing
                .getString("comment")
                .orEmpty()

        val userName =
            userRepository
                .getUsername()
                .ifBlank {
                    user.email
                        ?.substringBefore("@")
                        ?: "User"
                }

        val review =
            Review(
                id = id,
                userId = user.uid,
                userName = userName,
                recipeId = recipeId,
                recipeName = recipeName,
                rating = rating,
                comment = currentComment,
                createdAt = createdAt,
                updatedAt = now
            )

        reference
            .set(
                review,
                SetOptions.merge()
            )
            .await()

        return review
    }

    suspend fun removeRating(
        recipeId: String
    ) {

        val user =
            auth.currentUser
                ?: throw IllegalStateException(
                    "Bạn chưa đăng nhập."
                )

        val id =
            reviewId(
                recipeId,
                user.uid
            )

        val reference =
            firestore
                .collection("reviews")
                .document(id)

        val snapshot =
            reference
                .get()
                .await()

        if (!snapshot.exists()) {
            return
        }

        val comment =
            snapshot
                .getString("comment")
                .orEmpty()

        if (comment.isNotBlank()) {

            // Có comment thì giữ document,
            // chỉ xóa rating.
            reference
                .update(
                    mapOf(
                        "rating" to 0f,
                        "updatedAt" to
                                System.currentTimeMillis()
                    )
                )
                .await()

        } else {

            // Không comment + không rating
            // => document không còn giá trị.
            reference
                .delete()
                .await()
        }
    }

    suspend fun getRatingSummary(
        recipeId: String
    ): RatingSummary {

        if (recipeId.isBlank()) {
            return RatingSummary()
        }

        val reviews =
            getAllRecipeReviews(
                recipeId
            )

        val ratedReviews =
            reviews.filter {
                it.rating > 0f
            }

        if (ratedReviews.isEmpty()) {

            return RatingSummary(
                averageRating = 0f,
                ratingCount = 0
            )
        }

        val average =
            ratedReviews
                .map {
                    it.rating
                }
                .average()
                .toFloat()

        return RatingSummary(
            averageRating = average,
            ratingCount = ratedReviews.size
        )
    }

    suspend fun getAllReviews():
            List<Review> {

        val snapshot =
            firestore
                .collection("reviews")
                .get()
                .await()


        return snapshot
            .documents
            .map { document ->

                documentToReview(
                    document
                )
            }
    }
}