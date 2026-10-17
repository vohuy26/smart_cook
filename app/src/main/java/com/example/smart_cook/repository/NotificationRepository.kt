package com.example.smart_cook.repository

import com.example.smart_cook.model.Notification

import com.google.firebase.firestore.FirebaseFirestore

import kotlinx.coroutines.tasks.await


class NotificationRepository {

    private val firestore =
        FirebaseFirestore.getInstance()


    // =========================
    // GET NOTIFICATIONS
    // =========================

    suspend fun getNotifications():
            List<Notification> {

        val snapshot =
            firestore
                .collection("notifications")
                .get()
                .await()


        return snapshot
            .toObjects(
                Notification::class.java
            )
            .sortedByDescending {
                it.createdAt
            }
    }
}