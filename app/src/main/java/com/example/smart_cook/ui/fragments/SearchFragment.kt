package com.example.smart_cook.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.smart_cook.R
import com.example.smart_cook.databinding.FragmentSearchBinding
import com.example.smart_cook.ui.adapters.RecentSearchAdapter
import com.example.smart_cook.ui.adapters.RecipeAdapter
import com.example.smart_cook.ui.search.SearchViewModel

import dagger.hilt.android.AndroidEntryPoint

import kotlinx.coroutines.launch


@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding
        get() = _binding!!

    private val searchViewModel:
            SearchViewModel by viewModels()

    private lateinit var recipeAdapter:
            RecipeAdapter

    private lateinit var recentSearchAdapter:
            RecentSearchAdapter

    // =========================
    // CREATE VIEW
    // =========================

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =
            FragmentSearchBinding.inflate(
                inflater,
                container,
                false
            )


        // =========================
        // SEARCH RESULT
        // =========================

        binding.rvSearchRecipes.layoutManager =
            LinearLayoutManager(
                requireContext()
            )

        setupRecipeAdapter()


        // =========================
        // RECENT SEARCH
        // =========================

        binding.rvRecentSearch.layoutManager =
            LinearLayoutManager(
                requireContext()
            )

        recentSearchAdapter =
            RecentSearchAdapter(
                mutableListOf<String>()
            ) { keyword ->

                binding.searchView
                    .setQuery(
                        keyword,
                        true
                    )
            }

        binding.rvRecentSearch.adapter =
            recentSearchAdapter

        // =========================
        // SEARCH VIEW
        // =========================

        binding.searchView
            .setOnQueryTextListener(

                object :
                    SearchView.OnQueryTextListener {

                    override fun onQueryTextSubmit(
                        query: String?
                    ): Boolean {

                        if (!query.isNullOrBlank()) {

                            val keyword =
                                query.trim()

                            searchViewModel
                                .saveRecentSearch(
                                    keyword
                                )

                            searchViewModel
                                .searchMeals(
                                    keyword
                                )
                        }

                        return true
                    }


                    override fun onQueryTextChange(
                        newText: String?
                    ): Boolean {

                        searchViewModel
                            .onQueryChanged(
                                newText.orEmpty()
                            )

                        return true
                    }
                }
            )


        return binding.root
    }


    // =========================
    // VIEW CREATED
    // =========================

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {

        super.onViewCreated(
            view,
            savedInstanceState
        )

        observeSearchState()

        searchViewModel
            .loadRecentSearches()

        searchViewModel
            .loadFavorites()

        searchViewModel
            .loadDefaultRecipes()
    }


    // =========================
    // RECIPE ADAPTER
    // =========================

    private fun setupRecipeAdapter() {

        recipeAdapter =
            RecipeAdapter(

                recipeList =
                    mutableListOf(),

                favoriteIds =
                    mutableSetOf(),

                onItemClick = { meal ->

                    val bundle =
                        Bundle()

                    bundle.putString(
                        "mealId",
                        meal.id
                    )

                    findNavController()
                        .navigate(
                            R.id.recipeDetailFragment,
                            bundle
                        )
                },

                onFavoriteClick = { meal ->

                    searchViewModel
                        .toggleFavorite(
                            meal
                        )
                }
            )

        binding.rvSearchRecipes.adapter =
            recipeAdapter
    }


    // =========================
    // OBSERVE STATE
    // =========================

    private fun observeSearchState() {

        viewLifecycleOwner
            .lifecycleScope
            .launch {

                viewLifecycleOwner
                    .repeatOnLifecycle(
                        Lifecycle.State.STARTED
                    ) {

                        // =========================
                        // UI STATE
                        // =========================

                        launch {

                            searchViewModel
                                .uiState
                                .collect { state ->

                                    // LOADING

                                    binding
                                        .progressBar
                                        .visibility =

                                        if (state.isLoading) {

                                            View.VISIBLE

                                        } else {

                                            View.GONE
                                        }


                                    // EMPTY STATE

                                    binding
                                        .layoutEmpty
                                        .visibility =

                                        if (state.isEmpty) {

                                            View.VISIBLE

                                        } else {

                                            View.GONE
                                        }


                                    // RECIPE LIST

                                    binding
                                        .rvSearchRecipes
                                        .visibility =

                                        if (state.isEmpty) {

                                            View.GONE

                                        } else {

                                            View.VISIBLE
                                        }


                                    // UPDATE MEALS

                                    recipeAdapter
                                        .updateData(
                                            state.meals
                                        )


                                    // UPDATE FAVORITES

                                    recipeAdapter
                                        .setFavoriteIds(
                                            state.favoriteIds
                                        )

                                    recentSearchAdapter
                                        .updateList(
                                            state.recentSearches
                                        )


                                    // ERROR

                                    state.error
                                        ?.let { message ->

                                            Toast.makeText(
                                                requireContext(),
                                                message,
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                }
                        }


                        // =========================
                        // MESSAGE
                        // =========================

                        launch {

                            searchViewModel
                                .message
                                .collect { message ->

                                    Toast.makeText(
                                        requireContext(),
                                        message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }
                    }
            }
    }

    // =========================
    // ON RESUME
    // =========================

    override fun onResume() {

        super.onResume()

        searchViewModel
            .loadRecentSearches()

        searchViewModel
            .loadFavorites()
    }

    // =========================
    // DESTROY
    // =========================

    override fun onDestroyView() {

        super.onDestroyView()

        _binding = null
    }
}