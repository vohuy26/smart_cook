package com.example.smart_cook.repository

import com.example.smart_cook.model.RecipeStatus
import com.example.smart_cook.model.UserRecipe

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

import kotlinx.coroutines.tasks.await

import java.util.UUID


class RecipeSubmissionRepository {

    private val firestore =
        FirebaseFirestore.getInstance()

    private val auth =
        FirebaseAuth.getInstance()


    // =========================
    // SUBMIT RECIPE
    // =========================

    suspend fun submitRecipe(
        recipe: UserRecipe
    ): String {

        val user =
            auth.currentUser
                ?: throw IllegalStateException(
                    "You must be logged in to submit a recipe."
                )


        // Prefix vn_ để MealRepository
        // nhận biết đây là Firebase recipe.
        val recipeId =
            "vn_${UUID.randomUUID()}"


        val submittedRecipe =
            recipe.copy(

                id = recipeId,

                ownerId = user.uid,

                source = "firebase",

                creatorType = "USER",

                status =
                    RecipeStatus.PENDING.name,

                approvedBy = "",

                approvedAt = null,

                rejectionReason = "",

                favoriteCount = 0,

                averageRating = 0.0,

                ratingCount = 0,

                commentCount = 0,

                createdAt =
                    System.currentTimeMillis()
            )


        firestore
            .collection("recipes")
            .document(recipeId)
            .set(submittedRecipe)
            .await()


        return recipeId
    }


    // =========================
    // MY SUBMISSIONS
    // =========================

    suspend fun getMyRecipes():
            List<UserRecipe> {

        val user =
            auth.currentUser
                ?: return emptyList()


        val snapshot =
            firestore
                .collection("recipes")
                .whereEqualTo(
                    "ownerId",
                    user.uid
                )
                .get()
                .await()


        return snapshot
            .documents
            .mapNotNull { document ->

                document
                    .toObject(
                        UserRecipe::class.java
                    )
                    ?.copy(
                        id = document.id
                    )
            }
            .sortedByDescending {
                it.createdAt
            }
    }
}