package com.example.smart_cook.repository

import com.example.smart_cook.model.RecipeRanking

import javax.inject.Inject

import android.util.Log

class RankingRepository @Inject constructor(

    private val reviewRepository:
    ReviewRepository

) {


    // =========================
    // ALL RANKINGS
    // =========================

    suspend fun getRankings():
            List<RecipeRanking> {

        Log.d(
            "RANKING_DEBUG",
            "getRankings() START"
        )

        val reviews =
            reviewRepository
                .getAllReviews()

        Log.d(
            "RANKING_DEBUG",
            "Total reviews = ${reviews.size}"
        )

        reviews.forEach { review ->

            Log.d(
                "RANKING_DEBUG",
                "recipeId=${review.recipeId}, " +
                        "recipeName=${review.recipeName}, " +
                        "rating=${review.rating}, " +
                        "comment=${review.comment}"
            )
        }


        val grouped =
            reviews.groupBy {
                it.recipeId
            }



        return grouped
            .mapNotNull {
                    (recipeId, recipeReviews) ->


                if (
                    recipeId.isBlank()
                ) {

                    return@mapNotNull null
                }


                // Chỉ review thực sự có rating.
                val ratedReviews =
                    recipeReviews.filter {
                        it.rating > 0f
                    }


                // Recipe chưa từng được rating
                // thì chưa tham gia Ranking.
                if (
                    ratedReviews.isEmpty()
                ) {

                    return@mapNotNull null
                }


                val averageRating =
                    ratedReviews
                        .map {
                            it.rating
                        }
                        .average()
                        .toFloat()


                val commentCount =
                    recipeReviews.count {
                        it.comment.isNotBlank()
                    }


                val recipeName =
                    recipeReviews
                        .firstOrNull {
                            it.recipeName.isNotBlank()
                        }
                        ?.recipeName
                        ?: "Unknown Recipe"


                RecipeRanking(

                    recipeId =
                        recipeId,

                    recipeName =
                        recipeName,

                    averageRating =
                        averageRating,

                    ratingCount =
                        ratedReviews.size,

                    commentCount =
                        commentCount
                )
            }
    }


    // =========================
    // TOP RATED
    // =========================

    suspend fun getTopRated(
        limit: Int = 10
    ): List<RecipeRanking> {

        return getRankings()
            .sortedWith(

                compareByDescending<
                        RecipeRanking
                        > {
                    it.averageRating
                }

                    .thenByDescending {
                        it.ratingCount
                    }
            )
            .take(
                limit
            )
    }


    // =========================
    // MOST RATED
    // =========================

    suspend fun getMostRated(
        limit: Int = 10
    ): List<RecipeRanking> {

        return getRankings()
            .sortedWith(

                compareByDescending<
                        RecipeRanking
                        > {
                    it.ratingCount
                }

                    .thenByDescending {
                        it.averageRating
                    }
            )
            .take(
                limit
            )
    }
}