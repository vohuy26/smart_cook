package com.example.smart_cook.repository

import com.example.smart_cook.model.RecipeStatus
import com.example.smart_cook.model.UserRecipe

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

import kotlinx.coroutines.tasks.await


class AdminRecipeApprovalRepository {

    private val firestore =
        FirebaseFirestore.getInstance()

    private val auth =
        FirebaseAuth.getInstance()


    // =========================
    // PENDING RECIPES
    // =========================

    suspend fun getPendingRecipes():
            List<UserRecipe> {

        val snapshot =
            firestore
                .collection("recipes")
                .whereEqualTo(
                    "creatorType",
                    "USER"
                )
                .whereEqualTo(
                    "status",
                    RecipeStatus.PENDING.name
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


    // =========================
    // APPROVE
    // =========================

    suspend fun approveRecipe(
        recipeId: String
    ) {

        val admin =
            auth.currentUser
                ?: throw IllegalStateException(
                    "Admin is not logged in."
                )


        val recipeRef =
            firestore
                .collection("recipes")
                .document(recipeId)


        recipeRef
            .update(
                mapOf(

                    "status" to
                            RecipeStatus.APPROVED.name,

                    "approvedBy" to
                            admin.uid,

                    "approvedAt" to
                            System.currentTimeMillis(),

                    "rejectionReason" to
                            ""
                )
            )
            .await()


        saveAdminLog(
            action =
                "Approved user recipe $recipeId"
        )
    }


    // =========================
    // REJECT
    // =========================

    suspend fun rejectRecipe(
        recipeId: String,
        reason: String
    ) {

        val admin =
            auth.currentUser
                ?: throw IllegalStateException(
                    "Admin is not logged in."
                )


        if (reason.trim().isBlank()) {

            throw IllegalArgumentException(
                "Rejection reason is required."
            )
        }


        firestore
            .collection("recipes")
            .document(recipeId)
            .update(
                mapOf(

                    "status" to
                            RecipeStatus.REJECTED.name,

                    "approvedBy" to
                            "",

                    "approvedAt" to
                            null,

                    "rejectionReason" to
                            reason.trim()
                )
            )
            .await()


        saveAdminLog(
            action =
                "Rejected user recipe $recipeId by ${admin.uid}"
        )
    }


    // =========================
    // ADMIN LOG
    // =========================

    private suspend fun saveAdminLog(
        action: String
    ) {

        val log =
            hashMapOf<String, Any>(

                "title" to action,

                "actionType" to
                        "recipe_approval",

                "time" to
                        System.currentTimeMillis()
            )


        firestore
            .collection("admin_logs")
            .add(log)
            .await()
    }
}