package com.example.smart_cook.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository {

    private val auth =
        FirebaseAuth.getInstance()

    private val firestore =
        FirebaseFirestore.getInstance()

    suspend fun getUsername(): String {

        val uid =
            auth.currentUser?.uid
                ?: return "Smart Cook"

        val document =
            firestore
                .collection("users")
                .document(uid)
                .get()
                .await()

        return document
            .getString("name")
            ?: "Smart Cook"
    }
}