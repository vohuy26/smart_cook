package com.example.smart_cook.di

import android.content.Context
import com.example.smart_cook.data.local.AppDatabase
import com.example.smart_cook.data.local.FavoriteDao
import com.example.smart_cook.data.local.RecentlyViewedDao
import com.example.smart_cook.data.local.SearchHistoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {

        return AppDatabase
            .getDatabase(context)
    }

    @Provides
    fun provideFavoriteDao(
        database: AppDatabase
    ): FavoriteDao {

        return database.favoriteDao()
    }

    @Provides
    fun provideRecentlyViewedDao(
        database: AppDatabase
    ): RecentlyViewedDao {

        return database.recentlyViewedDao()
    }

    @Provides
    fun provideSearchHistoryDao(
        database: AppDatabase
    ): SearchHistoryDao {

        return database.searchHistoryDao()
    }
}