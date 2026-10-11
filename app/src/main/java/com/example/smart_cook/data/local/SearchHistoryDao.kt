package com.example.smart_cook.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.smart_cook.model.SearchHistory

@Dao
interface SearchHistoryDao {

    @Insert
    suspend fun insertSearch(
        searchHistory: SearchHistory
    )

    // Get all search history
    @Query(
        "SELECT * FROM search_history ORDER BY id DESC"
    )
    suspend fun getAllSearchHistory():
            List<SearchHistory>

    // Get recent keywords for AI recommendation
    @Query(
        "SELECT keyword FROM search_history ORDER BY id DESC LIMIT 10"
    )
    suspend fun getRecentKeywords():
            List<String>

    // Clear history
    @Query(
        "DELETE FROM search_history"
    )
    suspend fun clearHistory()
}