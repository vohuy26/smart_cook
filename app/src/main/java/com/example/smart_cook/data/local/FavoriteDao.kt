package com.example.smart_cook.data.local


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

import com.example.smart_cook.model.FavoriteRecipe



@Dao
interface FavoriteDao {


    @Insert(
        onConflict = OnConflictStrategy.REPLACE
    )
    suspend fun insertFavorite(
        recipe: FavoriteRecipe
    )


    @Delete
    suspend fun deleteFavorite(
        recipe: FavoriteRecipe
    )


    @Query(
        "SELECT * FROM favorite_recipes"
    )
    suspend fun getAllFavorites():
            List<FavoriteRecipe>


    @Query(
        "SELECT EXISTS(SELECT * FROM favorite_recipes WHERE id = :recipeId)"
    )
    suspend fun isFavorite(

        recipeId:String

    ):Boolean


    @Query(
        "SELECT id FROM favorite_recipes"
    )
    suspend fun getAllFavoriteIds():
            List<String>


}