package com.example.smart_cook.repository

import com.example.smart_cook.data.local.RecentlyViewedDao
import com.example.smart_cook.model.RecentlyViewed

class RecentlyViewedRepository(
    private val dao: RecentlyViewedDao
) {

    suspend fun insertViewed(
        recipe: RecentlyViewed
    ) {
        dao.insertViewed(recipe)
    }

    suspend fun getAllViewed():
            List<RecentlyViewed> {

        return dao.getAllViewed()
    }

    suspend fun clearAll() {

        dao.clearAll()
    }
}