package com.example.smart_cook.repository

import com.example.smart_cook.data.remote.RetrofitClient
import com.example.smart_cook.model.Meal
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.example.smart_cook.model.RecipeStatus

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
            .whereEqualTo(
                "status",
                RecipeStatus.APPROVED.name
            )
            .get()
            .addOnSuccessListener { snapshot ->

                val meals =
                    snapshot.documents.mapNotNull { doc ->
                        doc.toObject(
                            Meal::class.java
                        )?.apply {

                            id = doc.id

                            source = "firebase"

                            createdAt =
                                doc.getLong(
                                    "createdAt"
                                ) ?: 0L
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

                if (
                    doc.exists() &&
                    doc.getString("status") ==
                    RecipeStatus.APPROVED.name
                ) {

                    val meal =
                        doc.toObject(
                            Meal::class.java
                        )

                    meal?.id =
                        doc.id

                    meal?.source =
                        "firebase"

                    meal?.createdAt =
                        doc.getLong(
                            "createdAt"
                        ) ?: 0L

                    callback(meal)

                } else {

                    callback(null)
                }
            }
            .addOnFailureListener {

                callback(null)
            }
    }

    suspend fun searchMeals(
        keyword: String
    ): List<Meal> {

        val response =
            RetrofitClient.api
                .searchMeals(keyword)

        val apiMeals =
            if (response.isSuccessful) {

                response.body()
                    ?.meals
                    ?.map {

                        Meal(
                            id = it.id,
                            title = it.title ?: "",
                            image = it.image,
                            category = it.category ?: "",
                            area = it.area ?: "",
                            instructions =
                                it.instructions ?: "",
                            youtube =
                                it.youtube ?: "",
                            source = "api"
                        )
                    }
                    ?: emptyList()

            } else {

                emptyList()
            }


        val firebaseSnapshot =
            db.collection("recipes")
                .whereEqualTo(
                    "status",
                    RecipeStatus.APPROVED.name
                )
                .get()
                .await()

        val firebaseMeals =
            firebaseSnapshot
                .documents
                .mapNotNull { document ->
                    document
                        .toObject(
                            Meal::class.java
                        )
                        ?.apply {

                            id =
                                document.id

                            source =
                                "firebase"

                            createdAt =
                                document.getLong(
                                    "createdAt"
                                ) ?: 0L
                        }
                }


        val firebaseResult =
            firebaseMeals.filter {

                it.title.contains(
                    keyword,
                    ignoreCase = true
                )
            }


        return apiMeals +
                firebaseResult
    }

    suspend fun getRandomMeals(
        count: Int = 10
    ): List<Meal> {

        val randomMeals =
            mutableListOf<Meal>()

        repeat(count) {

            val response =
                RetrofitClient.api
                    .getRandomMeal()

            if (response.isSuccessful) {

                response.body()
                    ?.meals
                    ?.firstOrNull()
                    ?.let {

                        randomMeals.add(

                            Meal(
                                id = it.id,
                                title =
                                    it.title ?: "",
                                image =
                                    it.image,
                                category =
                                    it.category ?: "",
                                area =
                                    it.area ?: "",
                                instructions =
                                    it.instructions
                                        ?: "",
                                youtube =
                                    it.youtube ?: "",
                                source = "api"
                            )
                        )
                    }
            }
        }

        return randomMeals
    }

    suspend fun getMealDetail(
        id: String
    ): Meal? {

        if (id.isBlank()) {
            return null
        }


        // =========================
        // THEMEALDB
        // =========================

        // TheMealDB dùng ID dạng số:
        // 52772, 53013, ...
        val isApiRecipe =
            id.toLongOrNull() != null


        if (isApiRecipe) {

            val response =
                RetrofitClient.api
                    .getMealDetail(id)


            if (!response.isSuccessful) {
                return null
            }


            val apiMeal =
                response.body()
                    ?.meals
                    ?.firstOrNull()


            return apiMeal?.let {

                Meal(
                    id = it.id,

                    title =
                        it.title ?: "",

                    image =
                        it.image,

                    category =
                        it.category ?: "",

                    area =
                        it.area ?: "",

                    instructions =
                        it.instructions ?: "",

                    youtube =
                        it.youtube ?: "",

                    source =
                        "api"
                )
            }
        }


        // =========================
        // FIREBASE
        // =========================

        // Firebase gồm:
        //
        // User recipe:
        // vn_xxxxx
        //
        // Admin recipe:
        // Firestore auto ID
        // a8Fxxx...
        //
        // Đều là ID không thuần số.

        val document =
            db.collection("recipes")
                .document(id)
                .get()
                .await()


        if (!document.exists()) {
            return null
        }


        return document
            .toObject(
                Meal::class.java
            )
            ?.apply {

                this.id =
                    document.id

                source =
                    "firebase"

                createdAt =
                    document.getLong(
                        "createdAt"
                    ) ?: 0L
            }
    }
}