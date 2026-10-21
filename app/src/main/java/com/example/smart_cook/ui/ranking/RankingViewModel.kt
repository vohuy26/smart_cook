package com.example.smart_cook.ui.ranking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.smart_cook.repository.RankingRepository

import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import android.util.Log

import kotlinx.coroutines.launch

import javax.inject.Inject


@HiltViewModel
class RankingViewModel @Inject constructor(

    private val rankingRepository:
    RankingRepository

) : ViewModel() {


    private val _uiState =
        MutableStateFlow(
            RankingUiState()
        )


    val uiState:
            StateFlow<RankingUiState> =
        _uiState.asStateFlow()


    init {

        loadRankings()
    }


    // =========================
    // LOAD RANKING
    // =========================

    fun loadRankings() {

        viewModelScope.launch {

            _uiState.value =
                _uiState.value.copy(
                    isLoading = true,
                    error = null
                )


            try {

                val rankings =
                    when (
                        _uiState.value.selectedTab
                    ) {

                        RankingTab.TOP_RATED -> {

                            rankingRepository
                                .getTopRated()
                        }


                        RankingTab.MOST_RATED -> {

                            rankingRepository
                                .getMostRated()
                        }
                    }


                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        rankings = rankings,
                        error = null
                    )


            } catch (e: Exception) {

                Log.e(
                    "RANKING_DEBUG",
                    "Ranking error",
                    e
                )

                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        rankings = emptyList(),
                        error =
                            e.message
                                ?: "Không thể tải bảng xếp hạng"
                    )
            }
        }
    }


    // =========================
    // CHANGE TAB
    // =========================

    fun selectTab(
        tab: RankingTab
    ) {

        if (
            tab ==
            _uiState.value.selectedTab
        ) {

            return
        }


        _uiState.value =
            _uiState.value.copy(
                selectedTab = tab
            )


        loadRankings()
    }


    // =========================
    // REFRESH
    // =========================

    fun refresh() {

        loadRankings()
    }
}