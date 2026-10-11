package com.example.smart_cook.repository

import com.example.smart_cook.model.Meal
import com.google.firebase.firestore.FirebaseFirestore

class MealRepository {

    private val db = FirebaseFirestore.getInstance()

    // =========================
    // GET ALL FIREBASE MEALS
    // =========================

    fun getFirebaseMeals(
        callback: (List<Meal>) -> Unit
    ) {

        db.collection("recipes")
            .get()
            .addOnSuccessListener {

                val meals = it.documents.map { doc ->

                    doc.toObject(Meal::class.java)!!.apply {

                        id = doc.id
                        source = "firebase"

                        createdAt =
                            doc.getLong("createdAt")
                                ?: 0L
                    }
                }

                callback(meals)
            }
    }

    // =========================
    // GET FIREBASE DETAIL
    // =========================

    fun getMealById(
        id: String,
        callback: (Meal?) -> Unit
    ) {

        db.collection("recipes")
            .document(id)
            .get()
            .addOnSuccessListener { doc ->

                if (doc.exists()) {

                    val meal =
                        doc.toObject(
                            Meal::class.java
                        )

                    meal?.id = doc.id
                    meal?.source = "firebase"

                    meal?.createdAt =
                        doc.getLong("createdAt")
                            ?: 0L

                    callback(meal)

                } else {

                    callback(null)
                }
            }
    }
}