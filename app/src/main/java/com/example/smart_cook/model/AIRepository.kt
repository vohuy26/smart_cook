package com.example.smart_cook.repository

import com.example.smart_cook.model.AIRecommendation

class AIRepository {

    fun generateRecommendations():
            List<AIRecommendation> {

        return listOf(

            AIRecommendation(
                "Healthy Salad 🥗",
                "https://www.themealdb.com/images/media/meals/1548772327.jpg",
                "Because you like healthy food"
            ),

            AIRecommendation(
                "Cheese Burger 🍔",
                "https://www.themealdb.com/images/media/meals/sytuqu1511553755.jpg",
                "Based on your recent searches"
            ),

            AIRecommendation(
                "Italian Pasta 🍝",
                "https://www.themealdb.com/images/media/meals/wvpsxx1468256321.jpg",
                "Trending for you"
            )
        )
    }
}