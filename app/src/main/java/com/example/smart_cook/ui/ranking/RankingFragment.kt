package com.example.smart_cook.ui.ranking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.navigation.fragment.findNavController
import com.example.smart_cook.R
import android.content.res.ColorStateList
import android.graphics.Color
import android.widget.Toast

import com.example.smart_cook.databinding.FragmentRankingBinding

import dagger.hilt.android.AndroidEntryPoint

import kotlinx.coroutines.launch


@AndroidEntryPoint
class RankingFragment : Fragment() {


    private var _binding:
            FragmentRankingBinding? =
        null


    private val binding
        get() =
            _binding!!


    private val viewModel:
            RankingViewModel by viewModels()


    private lateinit var rankingAdapter:
            RankingAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =
            FragmentRankingBinding.inflate(
                inflater,
                container,
                false
            )


        setupRecycler()

        setupListeners()


        return binding.root
    }


    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {

        super.onViewCreated(
            view,
            savedInstanceState
        )

        observeState()
    }


    // =========================
    // RECYCLER
    // =========================

    private fun setupRecycler() {

        rankingAdapter =
            RankingAdapter { ranking ->

                val bundle =
                    Bundle()

                bundle.putString(
                    "mealId",
                    ranking.recipeId
                )

                findNavController()
                    .navigate(
                        R.id.recipeDetailFragment,
                        bundle
                    )
            }


        binding.rvRanking.layoutManager =
            LinearLayoutManager(
                requireContext()
            )


        binding.rvRanking.adapter =
            rankingAdapter
    }


    // =========================
    // LISTENERS
    // =========================

    private fun setupListeners() {

        binding.btnTopRated
            .setOnClickListener {

                viewModel.selectTab(
                    RankingTab.TOP_RATED
                )
            }


        binding.btnMostRated
            .setOnClickListener {

                viewModel.selectTab(
                    RankingTab.MOST_RATED
                )
            }
    }


    // =========================
    // STATE
    // =========================

    private fun observeState() {

        viewLifecycleOwner
            .lifecycleScope
            .launch {

                viewLifecycleOwner
                    .repeatOnLifecycle(
                        Lifecycle.State.STARTED
                    ) {

                        viewModel
                            .uiState
                            .collect { state ->


                                binding.progressRanking.visibility =
                                    if (
                                        state.isLoading
                                    ) {

                                        View.VISIBLE

                                    } else {

                                        View.GONE
                                    }


                                rankingAdapter.submitList(
                                    state.rankings
                                )


                                binding.txtEmptyRanking.visibility =
                                    if (
                                        !state.isLoading &&
                                        state.rankings.isEmpty()
                                    ) {

                                        View.VISIBLE

                                    } else {

                                        View.GONE
                                    }


                                binding.rvRanking.visibility =
                                    if (
                                        state.rankings.isEmpty()
                                    ) {

                                        View.GONE

                                    } else {

                                        View.VISIBLE
                                    }


                                updateTabUi(
                                    state.selectedTab
                                )

                                state.error?.let { error ->

                                    Toast.makeText(
                                        requireContext(),
                                        error,
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                    }
            }
    }


    // =========================
    // TAB UI
    // =========================

    private fun updateTabUi(
        selectedTab: RankingTab
    ) {

        val selectedColor =
            Color.parseColor("#6C63FF")

        val unselectedColor =
            Color.TRANSPARENT

        val selectedTextColor =
            Color.WHITE

        val unselectedTextColor =
            Color.parseColor("#555555")

        val borderColor =
            Color.parseColor("#DDDDDD")


        // TOP RATED
        if (
            selectedTab ==
            RankingTab.TOP_RATED
        ) {

            binding.btnTopRated.backgroundTintList =
                ColorStateList.valueOf(
                    selectedColor
                )

            binding.btnTopRated.setTextColor(
                selectedTextColor
            )

            binding.btnTopRated.strokeWidth = 0

        } else {

            binding.btnTopRated.backgroundTintList =
                ColorStateList.valueOf(
                    unselectedColor
                )

            binding.btnTopRated.setTextColor(
                unselectedTextColor
            )

            binding.btnTopRated.strokeWidth = 1

            binding.btnTopRated.strokeColor =
                ColorStateList.valueOf(
                    borderColor
                )
        }


        // MOST RATED
        if (
            selectedTab ==
            RankingTab.MOST_RATED
        ) {

            binding.btnMostRated.backgroundTintList =
                ColorStateList.valueOf(
                    selectedColor
                )

            binding.btnMostRated.setTextColor(
                selectedTextColor
            )

            binding.btnMostRated.strokeWidth = 0

        } else {

            binding.btnMostRated.backgroundTintList =
                ColorStateList.valueOf(
                    unselectedColor
                )

            binding.btnMostRated.setTextColor(
                unselectedTextColor
            )

            binding.btnMostRated.strokeWidth = 1

            binding.btnMostRated.strokeColor =
                ColorStateList.valueOf(
                    borderColor
                )
        }


        // LUÔN CHO PHÉP CLICK
        binding.btnTopRated.isEnabled = true
        binding.btnMostRated.isEnabled = true
    }


    override fun onResume() {

        super.onResume()

        viewModel.refresh()
    }


    override fun onDestroyView() {

        super.onDestroyView()

        _binding = null
    }
}