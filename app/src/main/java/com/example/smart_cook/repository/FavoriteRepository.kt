package com.example.smart_cook.repository

import com.example.smart_cook.data.local.FavoriteDao
import com.example.smart_cook.model.FavoriteRecipe

class FavoriteRepository(
    private val favoriteDao: FavoriteDao
) {

    suspend fun insertFavorite(
        recipe: FavoriteRecipe
    ) {
        favoriteDao.insertFavorite(recipe)
    }

    suspend fun deleteFavorite(
        recipe: FavoriteRecipe
    ) {
        favoriteDao.deleteFavorite(recipe)
    }

    suspend fun getAllFavorites(): List<FavoriteRecipe> {
        return favoriteDao.getAllFavorites()
    }

    suspend fun isFavorite(
        recipeId: String
    ): Boolean {
        return favoriteDao.isFavorite(recipeId)
    }

    suspend fun getAllFavoriteIds(): Set<String> {

        return favoriteDao
            .getAllFavoriteIds()
            .toSet()
    }
}