package com.example.smart_cook.di

import com.example.smart_cook.data.local.FavoriteDao
import com.example.smart_cook.data.local.RecentlyViewedDao
import com.example.smart_cook.data.local.SearchHistoryDao
import com.example.smart_cook.repository.CloudFavoriteRepository
import com.example.smart_cook.repository.FavoriteRepository
import com.example.smart_cook.repository.MealRepository
import com.example.smart_cook.repository.RecentlyViewedRepository
import com.example.smart_cook.repository.RecommendationRepository
import com.example.smart_cook.repository.SearchRepository
import com.example.smart_cook.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.example.smart_cook.repository.AvatarRepository
import com.example.smart_cook.repository.NotificationRepository

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideMealRepository():
            MealRepository {

        return MealRepository()
    }


    @Provides
    @Singleton
    fun provideFavoriteRepository(
        favoriteDao: FavoriteDao
    ): FavoriteRepository {

        return FavoriteRepository(
            favoriteDao
        )
    }


    @Provides
    @Singleton
    fun provideRecentlyViewedRepository(
        recentlyViewedDao: RecentlyViewedDao
    ): RecentlyViewedRepository {

        return RecentlyViewedRepository(
            recentlyViewedDao
        )
    }


    @Provides
    @Singleton
    fun provideRecommendationRepository(
        searchHistoryDao: SearchHistoryDao
    ): RecommendationRepository {

        return RecommendationRepository(
            searchHistoryDao
        )
    }


    @Provides
    @Singleton
    fun provideSearchRepository(
        searchHistoryDao: SearchHistoryDao
    ): SearchRepository {

        return SearchRepository(
            searchHistoryDao
        )
    }

    @Provides
    @Singleton
    fun provideAvatarRepository():
            AvatarRepository {

        return AvatarRepository()
    }

    @Provides
    @Singleton
    fun provideCloudFavoriteRepository():
            CloudFavoriteRepository {

        return CloudFavoriteRepository()
    }


    @Provides
    @Singleton
    fun provideUserRepository():
            UserRepository {

        return UserRepository()
    }

    @Provides
    @Singleton
    fun provideNotificationRepository():
            NotificationRepository {

        return NotificationRepository()
    }
}