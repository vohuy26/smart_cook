package com.example.smart_cook.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "recently_viewed"
)
data class RecentlyViewed(

    @PrimaryKey
    val id: String,

    val title: String,

    val image: String
)