package com.example.smart_cook.model

data class Meal(

    var id: String = "",

    var title: String = "",

    var image: String = "",

    var category: String = "",

    var area: String = "",

    var instructions: String = "",

    var youtube: String = "",

    var source: String = "",

    var createdAt: Long = 0L

)