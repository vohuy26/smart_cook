package com.example.smart_cook.repository

import com.example.smart_cook.model.FavoriteRecipe
import com.example.smart_cook.model.Meal
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class CloudFavoriteRepository {

    private val firestore =
        FirebaseFirestore.getInstance()

    private val auth =
        FirebaseAuth.getInstance()

    suspend fun addFavorite(
        meal: Meal
    ) {

        val uid =
            auth.currentUser?.uid ?: return

        val favoriteMap =
            hashMapOf(
                "mealId" to meal.id,
                "title" to (meal.title ?: "Unknown Recipe"),
                "image" to meal.image,
                "source" to meal.source
            )

        firestore
            .collection("favorites")
            .document(uid)
            .collection("meals")
            .document(meal.id)
            .set(favoriteMap)
            .await()

        if (meal.source == "firebase") {

            firestore
                .collection("recipes")
                .document(meal.id)
                .update(
                    "favoriteCount",
                    FieldValue.increment(1)
                )
                .await()
        }
    }

    suspend fun deleteFavorite(
        meal: Meal
    ) {

        val uid =
            auth.currentUser?.uid ?: return

        firestore
            .collection("favorites")
            .document(uid)
            .collection("meals")
            .document(meal.id)
            .delete()
            .await()

        if (meal.source == "firebase") {

            firestore
                .collection("recipes")
                .document(meal.id)
                .update(
                    "favoriteCount",
                    FieldValue.increment(-1)
                )
                .await()
        }
    }

    suspend fun getFavorites():
            List<FavoriteRecipe> {

        val uid =
            auth.currentUser?.uid
                ?: return emptyList()

        val snapshot =
            firestore
                .collection("favorites")
                .document(uid)
                .collection("meals")
                .get()
                .await()

        return snapshot.documents.mapNotNull { doc ->

            val id =
                doc.getString("mealId")
                    ?: doc.id

            val title =
                doc.getString("title")
                    ?: return@mapNotNull null

            val image =
                doc.getString("image")
                    ?: ""

            FavoriteRecipe(
                id = id,
                title = title,
                image = image
            )
        }
    }
}