package com.example.smart_cook.repository

import com.example.smart_cook.model.UserProfile

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

import kotlinx.coroutines.tasks.await


class UserRepository {

    private val auth =
        FirebaseAuth.getInstance()

    private val firestore =
        FirebaseFirestore.getInstance()


    // =========================
    // GET USERNAME
    // =========================

    suspend fun getUsername(): String {

        val currentUser =
            auth.currentUser
                ?: return "Smart Cook"

        val document =
            firestore
                .collection("users")
                .document(currentUser.uid)
                .get()
                .await()

        return document
            .getString("username")
            ?: document.getString("name")
            ?: currentUser.displayName
            ?: "Smart Cook"
    }


    // =========================
    // GET USER PROFILE
    // =========================

    suspend fun getUserProfile():
            UserProfile {

        val currentUser =
            auth.currentUser

        if (currentUser == null) {

            return UserProfile(
                username = "Guest",
                email = "No Email",
                avatarUrl = null,
                isLoggedIn = false
            )
        }


        val document =
            firestore
                .collection("users")
                .document(currentUser.uid)
                .get()
                .await()


        val username =
            document
                .getString("username")
                ?: document.getString("name")
                ?: currentUser.displayName
                ?: "Smart Cook User"


        val avatarUrl =
            document
                .getString("avatar")


        return UserProfile(
            username = username,
            email =
                currentUser.email
                    ?: "No Email",
            avatarUrl = avatarUrl,
            isLoggedIn = true
        )
    }


    // =========================
    // SAVE AVATAR
    // =========================

    suspend fun saveAvatarUrl(
        url: String
    ) {

        val currentUser =
            auth.currentUser
                ?: throw IllegalStateException(
                    "User not logged in"
                )


        val updates =
            hashMapOf<String, Any>(
                "avatar" to url
            )


        firestore
            .collection("users")
            .document(currentUser.uid)
            .set(
                updates,
                SetOptions.merge()
            )
            .await()
    }


    // =========================
    // LOGIN STATUS
    // =========================

    fun isLoggedIn(): Boolean {

        return auth.currentUser != null
    }


    // =========================
    // LOGOUT
    // =========================

    fun logout() {

        auth.signOut()
    }
}