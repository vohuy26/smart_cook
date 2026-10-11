package com.example.smart_cook.utils

import com.example.smart_cook.model.Meal

object RecommendationEngine {

    fun getRecommendedMeals(
        allMeals: List<Meal>,
        keywords: List<String>
    ): List<Meal> {

        if (keywords.isEmpty()) {
            return allMeals.shuffled().take(10)
        }

        return allMeals.filter { meal ->

            val title = meal.title ?: ""
            val category = meal.category ?: ""
            val area = meal.area ?: ""

            keywords.any { keyword ->

                title.contains(
                    keyword,
                    ignoreCase = true
                )

                        ||

                        category.contains(
                            keyword,
                            ignoreCase = true
                        )

                        ||

                        area.contains(
                            keyword,
                            ignoreCase = true
                        )
            }

        }.distinctBy {
            it.id
        }.take(10)
    }
}