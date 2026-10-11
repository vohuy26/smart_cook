package com.example.smart_cook.repository

import com.example.smart_cook.model.FavoriteRecipe
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class CloudFavoriteRepository {

    private val firestore =
        FirebaseFirestore.getInstance()

    private val auth =
        FirebaseAuth.getInstance()

    suspend fun addFavorite(
        recipe: FavoriteRecipe
    ) {

        val uid =
            auth.currentUser?.uid ?: return

        firestore.collection("favorites")
            .document(uid)
            .collection("items")
            .document(recipe.id)
            .set(recipe)
            .await()
    }

    suspend fun getFavorites():
            List<FavoriteRecipe> {

        val uid =
            auth.currentUser?.uid ?: return emptyList()

        val snapshot =
            firestore.collection("favorites")
                .document(uid)
                .collection("items")
                .get()
                .await()

        return snapshot.toObjects(
            FavoriteRecipe::class.java
        )
    }

    suspend fun deleteFavorite(
        recipeId: String
    ) {

        val uid =
            auth.currentUser?.uid ?: return

        firestore.collection("favorites")
            .document(uid)
            .collection("items")
            .document(recipeId)
            .delete()
            .await()
    }
}