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

    @Query(
        "DELETE FROM search_history WHERE keyword = :keyword"
    )
    suspend fun deleteByKeyword(
        keyword: String
    )

    @Query(
        """
        DELETE FROM search_history
        WHERE id NOT IN (
            SELECT id
            FROM search_history
            ORDER BY id DESC
            LIMIT 10
        )
        """
    )
    suspend fun keepLatestTen()

    @Query(
        "SELECT * FROM search_history ORDER BY id DESC"
    )
    suspend fun getAllSearchHistory():
            List<SearchHistory>

    @Query(
        "SELECT keyword FROM search_history ORDER BY id DESC LIMIT 10"
    )
    suspend fun getRecentKeywords():
            List<String>

    @Query(
        "DELETE FROM search_history"
    )
    suspend fun clearHistory()
}