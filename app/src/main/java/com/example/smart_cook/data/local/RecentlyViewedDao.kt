package com.example.smart_cook.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.smart_cook.model.RecentlyViewed

@Dao
interface RecentlyViewedDao {

    @Insert(
        onConflict = OnConflictStrategy.REPLACE
    )
    suspend fun insertViewed(
        recipe: RecentlyViewed
    )

    @Query(
        "SELECT * FROM recently_viewed ORDER BY id DESC"
    )
    suspend fun getAllViewed():
            List<RecentlyViewed>

    @Query(
        "DELETE FROM recently_viewed"
    )
    suspend fun clearAll()
}