package com.example.smart_cook.repository

import com.example.smart_cook.data.local.SearchHistoryDao
import com.example.smart_cook.model.SearchHistory

class SearchRepository(
    private val dao: SearchHistoryDao
) {

    suspend fun insertSearch(
        searchHistory: SearchHistory
    ) {

        dao.insertSearch(
            searchHistory
        )
    }


    suspend fun saveSearch(
        keyword: String
    ) {

        val cleanKeyword =
            keyword.trim()

        if (cleanKeyword.isBlank()) {
            return
        }

        // Nếu từ khóa đã tồn tại,
        // xóa bản cũ để bản mới lên đầu.
        dao.deleteByKeyword(
            cleanKeyword
        )

        dao.insertSearch(
            SearchHistory(
                keyword = cleanKeyword
            )
        )

        // Chỉ giữ 10 lịch sử gần nhất.
        dao.keepLatestTen()
    }


    suspend fun getHistory():
            List<SearchHistory> {

        return dao.getAllSearchHistory()
    }


    suspend fun getRecentKeywords():
            List<String> {

        return dao.getRecentKeywords()
    }


    suspend fun clearHistory() {

        dao.clearHistory()
    }
}