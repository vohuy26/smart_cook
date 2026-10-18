package com.example.smart_cook.repository

import com.example.smart_cook.model.Feedback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await


class FeedbackRepository {

    private val auth =
        FirebaseAuth.getInstance()

    private val firestore =
        FirebaseFirestore.getInstance()


    // =========================
    // SUBMIT FEEDBACK
    // =========================

    suspend fun submitFeedback(
        rating: Float,
        comment: String
    ) {

        val currentUser =
            auth.currentUser
                ?: throw IllegalStateException(
                    "User not logged in"
                )


        // =========================
        // GET USER INFO
        // =========================

        val userDocument =
            firestore
                .collection("users")
                .document(currentUser.uid)
                .get()
                .await()


        val username =
            userDocument
                .getString("username")
                ?: userDocument.getString("name")
                ?: currentUser.displayName
                ?: currentUser.email
                ?: "Smart Cook User"


        // =========================
        // CREATE FEEDBACK
        // =========================

        val feedback =
            Feedback(
                uid =
                    currentUser.uid,
                username =
                    username,
                rating =
                    rating,
                comment =
                    comment.trim()
            )


        // =========================
        // SAVE FIRESTORE
        // =========================

        firestore
            .collection("feedbacks")
            .add(
                feedback
            )
            .await()
    }
}