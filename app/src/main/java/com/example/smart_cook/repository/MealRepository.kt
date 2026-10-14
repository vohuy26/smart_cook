package com.example.smart_cook.repository

import com.example.smart_cook.data.remote.RetrofitClient
import com.example.smart_cook.model.Meal
import com.google.firebase.firestore.FirebaseFirestore

class MealRepository {

    private val db = FirebaseFirestore.getInstance()

    suspend fun getApiMeals(): List<Meal> {

        val response = RetrofitClient.api.getMeals()

        if (!response.isSuccessful || response.body() == null) {
            return emptyList()
        }

        return response.body()
            ?.meals
            ?.map { apiMeal ->

                Meal(
                    id = apiMeal.id,
                    title = apiMeal.title ?: "Unknown Recipe",
                    image = apiMeal.image,
                    category = apiMeal.category ?: "",
                    area = apiMeal.area ?: "",
                    instructions = apiMeal.instructions ?: "",
                    youtube = apiMeal.youtube ?: "",
                    source = "api"
                )
            }
            ?: emptyList()
    }

    fun getFirebaseMeals(
        onError: (Exception) -> Unit = {},
        onSuccess: (List<Meal>) -> Unit
    ) {

        db.collection("recipes")
            .get()
            .addOnSuccessListener { snapshot ->

                val meals = snapshot.documents.mapNotNull { doc ->

                    doc.toObject(Meal::class.java)?.apply {

                        id = doc.id
                        source = "firebase"

                        createdAt =
                            doc.getLong("createdAt")
                                ?: 0L
                    }
                }

                onSuccess(meals)
            }
            .addOnFailureListener { exception ->

                onError(exception)
            }
    }

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
                        doc.toObject(Meal::class.java)

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