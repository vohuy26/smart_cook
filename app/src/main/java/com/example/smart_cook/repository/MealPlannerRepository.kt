package com.example.smart_cook.repository

import com.example.smart_cook.model.MealPlan
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class MealPlannerRepository {

    private val firestore =
        FirebaseFirestore.getInstance()

    private val auth =
        FirebaseAuth.getInstance()

    suspend fun addMealPlan(
        mealPlan: MealPlan
    ) {

        val uid =
            auth.currentUser?.uid ?: return

        firestore.collection("meal_planner")
            .document(uid)
            .collection("plans")
            .add(mealPlan)
            .await()
    }

    suspend fun getMealPlans():
            List<MealPlan> {

        val uid =
            auth.currentUser?.uid ?: return emptyList()

        val snapshot =
            firestore.collection("meal_planner")
                .document(uid)
                .collection("plans")
                .get()
                .await()

        return snapshot.toObjects(
            MealPlan::class.java
        )
    }
}