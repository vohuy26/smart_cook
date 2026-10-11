package com.example.smart_cook.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_history")
data class SearchHistory(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val keyword: String
)