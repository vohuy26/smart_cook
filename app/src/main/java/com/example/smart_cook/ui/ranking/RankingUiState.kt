package com.example.smart_cook.ui.ranking

import com.example.smart_cook.model.RecipeRanking

data class RankingUiState(

    val isLoading: Boolean = false,

    val selectedTab: RankingTab =
        RankingTab.TOP_RATED,

    val rankings: List<RecipeRanking> =
        emptyList(),

    val error: String? = null
)