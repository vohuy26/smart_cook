package com.example.smart_cook.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.smart_cook.model.FavoriteRecipe
import com.example.smart_cook.model.RecentlyViewed
import com.example.smart_cook.model.SearchHistory

@Database(
    entities = [
        FavoriteRecipe::class,
        SearchHistory::class,
        RecentlyViewed::class
    ],
    version = 4,
    exportSchema = false
)

abstract class AppDatabase : RoomDatabase() {

    abstract fun favoriteDao(): FavoriteDao

    abstract fun searchHistoryDao(): SearchHistoryDao

    abstract fun recentlyViewedDao(): RecentlyViewedDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(
            context: Context
        ): AppDatabase {

            return INSTANCE ?: synchronized(this) {

                val instance =
                    Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "smart_cook_db"
                    )

                        // IMPORTANT
                        .fallbackToDestructiveMigration()

                        .build()

                INSTANCE = instance

                instance
            }
        }
    }
}