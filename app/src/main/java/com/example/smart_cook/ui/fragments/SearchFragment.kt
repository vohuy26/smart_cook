package com.example.smart_cook.ui.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.smart_cook.R
import com.example.smart_cook.data.local.AppDatabase
import com.example.smart_cook.data.remote.RetrofitClient
import com.example.smart_cook.databinding.FragmentSearchBinding

import com.example.smart_cook.model.FavoriteRecipe
import com.example.smart_cook.model.Meal

import com.example.smart_cook.repository.FavoriteRepository
import com.example.smart_cook.repository.MealRepository

import com.example.smart_cook.ui.adapters.RecipeAdapter

import kotlinx.coroutines.launch
import android.content.Context
import com.example.smart_cook.ui.adapters.RecentSearchAdapter

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var recipeAdapter: RecipeAdapter
    private lateinit var favoriteRepository: FavoriteRepository
    private lateinit var mealRepository: MealRepository
    private var isSearching = false

    // Recent Search
    private lateinit var recentSearchAdapter: RecentSearchAdapter
    private val recentSearchList = mutableListOf<String>()

    private val PREF_NAME = "recent_search"
    private val KEY_HISTORY = "history"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSearchBinding.inflate(
            inflater,
            container,
            false
        )

        val database =
            AppDatabase.getDatabase(requireContext())

        favoriteRepository =
            FavoriteRepository(
                database.favoriteDao()
            )

        mealRepository =
            MealRepository()

        // =========================
        // SEARCH RESULT
        // =========================

        binding.rvSearchRecipes.layoutManager =
            LinearLayoutManager(requireContext())

        // =========================
        // RECENT SEARCH
        // =========================

        binding.rvRecentSearch.layoutManager =
            LinearLayoutManager(requireContext())

        recentSearchAdapter =
            RecentSearchAdapter(
                recentSearchList
            ) { keyword ->

                binding.searchView.setQuery(
                    keyword,
                    true
                )

            }

        binding.rvRecentSearch.adapter =
            recentSearchAdapter

        loadRecentSearch()

        // =========================
        // SEARCH VIEW
        // =========================

        binding.searchView.setOnQueryTextListener(

            object : SearchView.OnQueryTextListener {

                override fun onQueryTextSubmit(
                    query: String?
                ): Boolean {

                    if (!query.isNullOrEmpty()) {

                        saveRecentSearch(query)

                        searchMeals(query)

                    }

                    return true
                }

                override fun onQueryTextChange(
                    newText: String?
                ): Boolean {

                    if (newText.isNullOrBlank()) {

                        binding.layoutEmpty.visibility = View.GONE
                        binding.rvSearchRecipes.visibility = View.VISIBLE

                        loadDefaultRecipes()

                    } else {

                        searchMeals(newText.trim())

                    }

                    return true
                }
            }
        )

        loadRecentSearch()

        loadDefaultRecipes()

        return binding.root
    }
// =========================
// SEARCH API + FIREBASE
// =========================

    private fun searchMeals(
        keyword: String
    ) {

        lifecycleScope.launch {

            try {

                binding.progressBar.visibility =
                    View.VISIBLE

                // ======================
                // THEMEALDB API
                // ======================

                val response =
                    RetrofitClient.api
                        .searchMeals(keyword)

                val apiMeals: List<Meal> =

                    if (response.isSuccessful) {

                        response.body()
                            ?.meals
                            ?.map {

                                Meal(

                                    id = it.id,

                                    title = it.title ?: "",

                                    image = it.image,

                                    category = it.category ?: "",

                                    area = it.area ?: "",

                                    instructions =
                                        it.instructions ?: "",

                                    youtube =
                                        it.youtube ?: "",

                                    source = "api"

                                )

                            }
                            ?: emptyList()

                    } else {

                        emptyList()

                    }

                // ======================
                // FIREBASE
                // ======================

                mealRepository.getFirebaseMeals {

                        firebaseMeals ->

                    val firebaseResult =

                        firebaseMeals.filter {

                            it.title.contains(

                                keyword,

                                ignoreCase = true

                            )

                        }

                    val result =

                        apiMeals + firebaseResult

                    binding.progressBar.visibility =
                        View.GONE

                    if (result.isEmpty()) {

                        binding.layoutEmpty.visibility =
                            View.VISIBLE

                        binding.rvSearchRecipes.visibility =
                            View.GONE

                        return@getFirebaseMeals

                    }

                    binding.layoutEmpty.visibility =
                        View.GONE

                    binding.rvSearchRecipes.visibility =
                        View.VISIBLE

                    lifecycleScope.launch {

                        val favoriteIds =

                            favoriteRepository
                                .getAllFavoriteIds()
                                .toMutableSet()

                        recipeAdapter =

                            RecipeAdapter(

                                recipeList = result,

                                favoriteIds = favoriteIds,

                                onItemClick = { meal ->

                                    val bundle = Bundle()

                                    bundle.putString(
                                        "mealId",
                                        meal.id
                                    )

                                    findNavController().navigate(
                                        R.id.recipeDetailFragment,
                                        bundle
                                    )

                                },

                                onFavoriteClick = { meal ->

                                    lifecycleScope.launch {

                                        try {

                                            val isFavorite =

                                                favoriteRepository
                                                    .isFavorite(
                                                        meal.id
                                                    )

                                            if (!isFavorite) {

                                                val favorite =

                                                    FavoriteRecipe(

                                                        id = meal.id,

                                                        title = meal.title,

                                                        image = meal.image

                                                    )

                                                favoriteRepository
                                                    .insertFavorite(
                                                        favorite
                                                    )

                                                recipeAdapter.addFavorite(
                                                    meal.id
                                                )

                                                Toast.makeText(

                                                    requireContext(),

                                                    "Added Favorite ❤️",

                                                    Toast.LENGTH_SHORT

                                                ).show()

                                            } else {

                                                favoriteRepository
                                                    .deleteFavorite(

                                                        FavoriteRecipe(

                                                            id = meal.id,

                                                            title = meal.title,

                                                            image = meal.image

                                                        )

                                                    )

                                                recipeAdapter.removeFavorite(
                                                    meal.id
                                                )

                                                Toast.makeText(

                                                    requireContext(),

                                                    "Removed Favorite",

                                                    Toast.LENGTH_SHORT

                                                ).show()

                                            }

                                        } catch (e: Exception) {

                                            Toast.makeText(

                                                requireContext(),

                                                e.message,

                                                Toast.LENGTH_SHORT

                                            ).show()

                                        }

                                    }

                                }

                            )

                        binding.rvSearchRecipes.adapter =
                            recipeAdapter

                    }

                }

            } catch (e: Exception) {

                binding.progressBar.visibility =
                    View.GONE

                Toast.makeText(

                    requireContext(),

                    "Error: ${e.message}",

                    Toast.LENGTH_SHORT

                ).show()

                e.printStackTrace()

            }

        }

    }

    private fun loadDefaultRecipes() {

        lifecycleScope.launch {

            try {

                binding.progressBar.visibility =
                    View.VISIBLE

                val randomMeals =
                    mutableListOf<Meal>()

                repeat(10) {

                    val response =
                        RetrofitClient.api.getRandomMeal()

                    if (response.isSuccessful) {

                        response.body()
                            ?.meals
                            ?.firstOrNull()
                            ?.let {

                                randomMeals.add(

                                    Meal(

                                        id = it.id,

                                        title = it.title ?: "",

                                        image = it.image,

                                        category = it.category ?: "",

                                        area = it.area ?: "",

                                        instructions =
                                            it.instructions ?: "",

                                        youtube =
                                            it.youtube ?: "",

                                        source = "api"

                                    )

                                )

                            }

                    }

                }

                binding.progressBar.visibility =
                    View.GONE

                val favoriteIds =
                    favoriteRepository
                        .getAllFavoriteIds()
                        .toMutableSet()

                recipeAdapter =

                    RecipeAdapter(

                        recipeList = randomMeals,

                        favoriteIds = favoriteIds,

                        onItemClick = { meal ->

                            val bundle = Bundle()

                            bundle.putString(
                                "mealId",
                                meal.id
                            )

                            findNavController().navigate(
                                R.id.recipeDetailFragment,
                                bundle
                            )

                        },

                        onFavoriteClick = { meal ->

                            lifecycleScope.launch {

                                val isFavorite =
                                    favoriteRepository
                                        .isFavorite(meal.id)

                                if (!isFavorite) {

                                    favoriteRepository.insertFavorite(

                                        FavoriteRecipe(

                                            id = meal.id,

                                            title = meal.title,

                                            image = meal.image

                                        )

                                    )

                                    recipeAdapter.addFavorite(
                                        meal.id
                                    )

                                } else {

                                    favoriteRepository.deleteFavorite(

                                        FavoriteRecipe(

                                            id = meal.id,

                                            title = meal.title,

                                            image = meal.image

                                        )

                                    )

                                    recipeAdapter.removeFavorite(
                                        meal.id
                                    )

                                }

                            }

                        }

                    )

                binding.rvSearchRecipes.adapter =
                    recipeAdapter

            } catch (e: Exception) {

                e.printStackTrace()

            }

        }

    }
// =========================
// RECENT SEARCH
// =========================

    private fun saveRecentSearch(
        keyword: String
    ) {

        val prefs =
            requireContext().getSharedPreferences(
                PREF_NAME,
                Context.MODE_PRIVATE
            )

        val history =
            loadHistory().toMutableList()

        history.remove(keyword)

        history.add(
            0,
            keyword
        )

        if (history.size > 10) {

            history.removeAt(
                history.lastIndex
            )

        }

        prefs.edit()
            .putString(
                KEY_HISTORY,
                history.joinToString("|")
            )
            .apply()

        loadRecentSearch()

        loadDefaultRecipes()

    }

    private fun loadRecentSearch() {

        recentSearchList.clear()

        recentSearchList.addAll(
            loadHistory()
        )

        recentSearchAdapter.updateList(
            recentSearchList.toList()
        )

    }

    private fun loadHistory(): List<String> {

        val prefs =
            requireContext().getSharedPreferences(
                PREF_NAME,
                Context.MODE_PRIVATE
            )

        val history =
            prefs.getString(
                KEY_HISTORY,
                ""
            ) ?: ""

        if (history.isBlank()) {

            return emptyList()

        }

        return history
            .split("|")
            .filter {
                it.isNotBlank()
            }

    }
// =========================
// FAVORITE
// =========================

    private fun saveFavorite(
        meal: Meal
    ) {

        lifecycleScope.launch {

            try {

                val isFavorite =
                    favoriteRepository.isFavorite(
                        meal.id
                    )

                if (!isFavorite) {

                    favoriteRepository.insertFavorite(

                        FavoriteRecipe(

                            id = meal.id,

                            title = meal.title,

                            image = meal.image

                        )

                    )

                    Toast.makeText(

                        requireContext(),

                        "Added Favorite ❤️",

                        Toast.LENGTH_SHORT

                    ).show()

                } else {

                    Toast.makeText(

                        requireContext(),

                        "Already Favorite",

                        Toast.LENGTH_SHORT

                    ).show()

                }

            } catch (e: Exception) {

                Toast.makeText(

                    requireContext(),

                    e.message,

                    Toast.LENGTH_SHORT

                ).show()

            }

        }

    }

// =========================
// ON RESUME
// =========================

    override fun onResume() {

        super.onResume()

        loadRecentSearch()

        if (::recipeAdapter.isInitialized) {

            lifecycleScope.launch {

                try {

                    val favoriteIds =

                        favoriteRepository
                            .getAllFavoriteIds()

                    recipeAdapter.setFavoriteIds(

                        favoriteIds.toSet()

                    )

                } catch (e: Exception) {

                    e.printStackTrace()

                }

            }

        }

    }

// =========================
// DESTROY
// =========================

    override fun onDestroyView() {

        super.onDestroyView()

        _binding = null

    }

}