package com.example.smart_cook.utils

object FakeAIEngine {

    fun getRecommendation(
        keyword: String
    ): String {

        return when {

            keyword.contains(
                "salad",
                true
            ) -> {

                "Healthy Recommendation 🥗"
            }

            keyword.contains(
                "burger",
                true
            ) -> {

                "Fast Food Recommendation 🍔"
            }

            else -> {

                "Chef Special Recommendation 👨‍🍳"
            }
        }
    }
}