package com.example.smart_cook.repository

import com.example.smart_cook.data.local.SearchHistoryDao
import com.example.smart_cook.model.SearchHistory

class SearchRepository(
    private val dao: SearchHistoryDao
) {

    suspend fun insertSearch(
        searchHistory: SearchHistory
    ) {

        dao.insertSearch(searchHistory)
    }

    suspend fun getHistory():
            List<SearchHistory> {

        return dao.getAllSearchHistory()
    }

    suspend fun clearHistory() {

        dao.clearHistory()
    }
}