package com.example.smart_cook.ui.fragments

import com.google.firebase.firestore.FieldValue
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.smart_cook.repository.MealRepository
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smart_cook.R
import com.example.smart_cook.data.local.AppDatabase
import com.example.smart_cook.data.remote.RetrofitClient
import com.example.smart_cook.databinding.FragmentHomeBinding
import com.example.smart_cook.model.Category
import com.example.smart_cook.model.FavoriteRecipe
import com.example.smart_cook.model.Meal
import com.example.smart_cook.model.RecentlyViewed
import com.example.smart_cook.model.Recommendation
import com.example.smart_cook.repository.FavoriteRepository
import com.example.smart_cook.repository.RecentlyViewedRepository
import com.example.smart_cook.repository.RecommendationRepository
import com.example.smart_cook.ui.adapters.CategoryAdapter
import com.example.smart_cook.ui.adapters.RecentlyViewedAdapter
import com.example.smart_cook.ui.adapters.RecipeAdapter
import com.example.smart_cook.ui.adapters.RecommendationAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.repeatOnLifecycle

import com.example.smart_cook.ui.home.HomeViewModel
import com.example.smart_cook.ui.home.HomeViewModelFactory

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var mealRepository: MealRepository
    private lateinit var homeViewModel: HomeViewModel

    private var allMeals =
        mutableListOf<Meal>()
    private lateinit var recipeAdapter: RecipeAdapter
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var recommendationAdapter: RecommendationAdapter
    private lateinit var recentlyViewedAdapter: RecentlyViewedAdapter

    private lateinit var repository: FavoriteRepository
    private lateinit var viewedRepository: RecentlyViewedRepository
    private lateinit var recommendationRepository: RecommendationRepository

    private val categoryMapping = mapOf(

        "Món mặn 🍲" to listOf(
            "Beef",
            "Chicken",
            "Seafood",
            "Pork",
            "Lamb",
            "Goat"
        ),

        "Món chay 🥗" to listOf(
            "Vegetarian",
            "Vegan"
        ),

        "Canh 🍜" to listOf(
            "Soup"
        ),

        "Tráng miệng 🍰" to listOf(
            "Dessert"
        ),

        "Đồ uống 🥤" to listOf(
            "Drinks"
        ),

        "Ăn nhanh 🍔" to listOf(
            "Breakfast"
        )

    )

    // FIREBASE

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =
            FragmentHomeBinding.inflate(
                inflater,
                container,
                false
            )

        // =========================
        // FIREBASE
        // =========================

        firestore =
            FirebaseFirestore.getInstance()

        auth =
            FirebaseAuth.getInstance()

        mealRepository =
            MealRepository()

        // =========================
        // USERNAME
        // =========================

        loadUsername()

        // =========================
        // FAVORITE DATABASE
        // =========================

        val favoriteDao =
            AppDatabase
                .getDatabase(requireContext())
                .favoriteDao()

        repository =
            FavoriteRepository(favoriteDao)

        // =========================
        // RECENTLY VIEWED DATABASE
        // =========================

        val viewedDao =
            AppDatabase
                .getDatabase(requireContext())
                .recentlyViewedDao()

        viewedRepository =
            RecentlyViewedRepository(viewedDao)

        // =========================
        // SEARCH DATABASE
        // =========================

        val searchDao =
            AppDatabase
                .getDatabase(requireContext())
                .searchHistoryDao()

        recommendationRepository =
            RecommendationRepository(searchDao)

            val homeViewModelFactory =
                HomeViewModelFactory(
                    mealRepository = mealRepository,
                    recommendationRepository = recommendationRepository
                )

            homeViewModel =
                ViewModelProvider(
                    this,
                    homeViewModelFactory
                )[HomeViewModel::class.java]

        // =========================
        // SETUP
        // =========================
        setupCategories()

        loadRecentlyViewed()

        setupRecipeAdapter()

        observeHomeState()

        homeViewModel.loadHome()

        binding.layoutSearch.setOnClickListener {

            requireActivity()
                .findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(
                    R.id.bottomNavigationView
                )
                .selectedItemId = R.id.searchFragment

        }
        return binding.root
    }

    // =========================
    // LOAD USERNAME
    // =========================

    private fun loadUsername() {

        val uid =
            auth.currentUser?.uid ?: return

        firestore
            .collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener {

                val name =
                    it.getString("name")
                        ?: "Smart Cook"

                binding.txtUsername.text =
                    name
            }
    }

    // =========================
    // CATEGORY
    // =========================

    private fun setupCategories(){


        val categories = listOf(


            Category(1,"Món mặn 🍲"),

            Category(2,"Món chay 🥗"),

            Category(3,"Canh 🍜"),

            Category(4,"Tráng miệng 🍰"),

            Category(5,"Đồ uống 🥤"),

            Category(6,"Ăn nhanh 🍔")


        )



        categoryAdapter =


            CategoryAdapter(categories){ category ->



                val filtered =

                    when(category.name){



                        "Món mặn 🍲" ->

                            allMeals.filter {

                                it.category in listOf(

                                    "Chicken",
                                    "Beef",
                                    "Seafood",
                                    "Pork",
                                    "Lamb",
                                    "Món mặn"

                                )

                            }



                        "Món chay 🥗" ->

                            allMeals.filter {

                                it.category in listOf(

                                    "Vegetarian",
                                    "Vegan",
                                    "Món chay"

                                )

                            }



                        "Canh 🍜" ->

                            allMeals.filter {

                                it.category in listOf(

                                    "Soup",
                                    "Canh"

                                )

                            }



                        "Tráng miệng 🍰" ->

                            allMeals.filter {

                                it.category in listOf(

                                    "Dessert",
                                    "Tráng miệng"

                                )

                            }



                        "Đồ uống 🥤" ->

                            allMeals.filter {

                                it.category in listOf(

                                    "Drinks",
                                    "Đồ uống"

                                )

                            }



                        "Ăn nhanh 🍔" ->

                            allMeals.filter {

                                it.category in listOf(

                                    "Breakfast",
                                    "Ăn nhanh"

                                )

                            }



                        else ->

                            allMeals


                    }



                recipeAdapter.updateData(
                    filtered
                )


            }



        binding.rvCategories.layoutManager =

            LinearLayoutManager(

                requireContext(),

                LinearLayoutManager.HORIZONTAL,

                false

            )



        binding.rvCategories.adapter =
            categoryAdapter

    }

    // =========================
    // AI RECOMMENDATION
    // =========================

    private fun loadAIRecommendations(
        meals: List<Meal>
    ) {

        lifecycleScope.launch {

            try {

                val recommendedMeals =
                    recommendationRepository
                        .generateRecommendations(
                            meals
                        )

                android.util.Log.d(
                    "AI_UI",
                    "recommendedMeals size = ${recommendedMeals.size}"
                )

                recommendedMeals.forEach { meal ->

                    android.util.Log.d(
                        "AI_UI",
                        "Show: ${meal.title}"
                    )
                }

                val recommendationList =
                recommendedMeals.map { meal ->

                    android.util.Log.d(
                        "AI_UI",
                        "mapping ${meal.title}"
                    )

                    Recommendation(
                        title = meal.title ?: "Unknown Recipe",
                        image = meal.image
                    )
                }

                android.util.Log.d(
                    "AI_UI",
                    "adapter creating"
                )

                recommendationAdapter =
                    RecommendationAdapter(
                        recommendationList
                    )

                binding.rvRecommendations.layoutManager =
                    LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )

                binding.rvRecommendations.adapter =
                    recommendationAdapter

            } catch (e: Exception) {

                android.util.Log.e(
                    "AI_UI",
                    "FULL ERROR",
                    e
                )

                e.printStackTrace()
            }
        }
    }

    private fun observeHomeState() {

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                homeViewModel.uiState.collect { state ->

                    // =========================
                    // MEALS
                    // =========================

                    allMeals.clear()
                    allMeals.addAll(state.meals)

                    if (::recipeAdapter.isInitialized) {

                        recipeAdapter.updateData(
                            state.meals
                        )
                    }

                    // =========================
                    // RECOMMENDATIONS
                    // =========================

                    val recommendationList =
                        state.recommendedMeals.map { meal ->

                            Recommendation(
                                title =
                                    meal.title
                                        ?: "Unknown Recipe",

                                image =
                                    meal.image
                            )
                        }

                    recommendationAdapter =
                        RecommendationAdapter(
                            recommendationList
                        )

                    binding.rvRecommendations.layoutManager =
                        LinearLayoutManager(
                            requireContext(),
                            LinearLayoutManager.HORIZONTAL,
                            false
                        )

                    binding.rvRecommendations.adapter =
                        recommendationAdapter

                    // =========================
                    // ERROR
                    // =========================

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
    // RECENTLY VIEWED
    // =========================

    private fun loadRecentlyViewed() {

        lifecycleScope.launch {

            try {

                val viewedList =
                    viewedRepository.getAllViewed()

                recentlyViewedAdapter =

                    RecentlyViewedAdapter(

                        viewedList

                    ) { recipe ->

                        val bundle =
                            Bundle()

                        bundle.putString(
                            "mealId",
                            recipe.id
                        )

                        findNavController()
                            .navigate(
                                R.id.recipeDetailFragment,
                                bundle
                            )

                    }

                binding.rvRecentlyViewed.layoutManager =

                    LinearLayoutManager(

                        requireContext(),

                        LinearLayoutManager.HORIZONTAL,

                        false

                    )

                binding.rvRecentlyViewed.adapter =
                    recentlyViewedAdapter

            } catch (e: Exception) {

                e.printStackTrace()
            }
        }
    }

    // =========================
    // LOAD MEALS API
    // =========================

    private fun getMeals() {

        lifecycleScope.launch {

            try {

                val response =
                    RetrofitClient.api.getMeals()

                val apiMeals: List<Meal> =

                    if (
                        response.isSuccessful &&
                        response.body() != null
                    ) {

                        response.body()
                            ?.meals
                            ?.map { apiMeal ->

                                Meal(

                                    id = apiMeal.id,

                                    title =
                                        apiMeal.title
                                            ?: "Unknown Recipe",

                                    image =
                                        apiMeal.image,

                                    category =
                                        apiMeal.category
                                            ?: "",

                                    area =
                                        apiMeal.area
                                            ?: "",

                                    instructions =
                                        apiMeal.instructions
                                            ?: "",

                                    youtube =
                                        apiMeal.youtube ?: "",

                                    source = "api"

                                )

                            }
                            ?: emptyList()

                    } else {

                        emptyList()
                    }

                mealRepository.getFirebaseMeals { firebaseMeals ->

                    allMeals.clear()

                    allMeals.addAll(apiMeals)

                    allMeals.addAll(firebaseMeals)

                    allMeals.sortByDescending {
                        it.createdAt
                    }

                    val meals =
                        allMeals.toList()

                    loadAIRecommendations(
                        meals
                    )

                    if (meals.isEmpty()) {

                        Toast.makeText(
                            requireContext(),
                            "No meals found",
                            Toast.LENGTH_SHORT
                        ).show()

                        return@getFirebaseMeals
                    }

                    if (::recipeAdapter.isInitialized) {

                        if (::recipeAdapter.isInitialized) {

                            recipeAdapter.updateData(
                                meals
                            )

                        }

                    }
                }

            } catch (e: Exception) {

                Toast.makeText(
                    requireContext(),
                    "Error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()

                e.printStackTrace()
            }
        }
    }

    private fun setupRecipeAdapter() {

        lifecycleScope.launch {

            val favoriteIds =
                repository
                    .getAllFavoriteIds()
                    .toMutableSet()

            recipeAdapter =

                RecipeAdapter(

                    recipeList = allMeals,

                    favoriteIds = favoriteIds,

                    onItemClick = { recipe ->

                        lifecycleScope.launch {

                            try {

                                val viewedRecipe =

                                    RecentlyViewed(

                                        id = recipe.id,

                                        title =
                                            recipe.title
                                                ?: "Unknown Recipe",

                                        image =
                                            recipe.image

                                    )

                                viewedRepository
                                    .insertViewed(
                                        viewedRecipe
                                    )

                                loadRecentlyViewed()

                            } catch (e: Exception) {

                                e.printStackTrace()
                            }
                        }

                        val bundle =
                            Bundle()

                        bundle.putString(
                            "mealId",
                            recipe.id
                        )

                        findNavController()
                            .navigate(
                                R.id.recipeDetailFragment,
                                bundle
                            )
                    },

                    onFavoriteClick = { recipe ->

                        lifecycleScope.launch {

                            try {

                                val isExist =
                                    repository.isFavorite(
                                        recipe.id
                                    )

                                if (!isExist) {

                                    val favoriteRecipe =

                                        FavoriteRecipe(

                                            id =
                                                recipe.id,

                                            title =
                                                recipe.title,

                                            image =
                                                recipe.image

                                        )

                                    repository
                                        .insertFavorite(
                                            favoriteRecipe
                                        )

                                    recipeAdapter
                                        .addFavorite(
                                            recipe.id
                                        )
                                    recipeAdapter.notifyDataSetChanged()

                                    val uid =
                                        auth.currentUser?.uid
                                            ?: ""

                                    val favoriteMap =

                                        hashMapOf(

                                            "mealId" to recipe.id,

                                            "title" to recipe.title,

                                            "image" to recipe.image,

                                            "source" to recipe.source
                                        )

                                    firestore
                                        .collection(
                                            "favorites"
                                        )
                                        .document(uid)
                                        .collection(
                                            "meals"
                                        )
                                        .document(
                                            recipe.id
                                        )
                                        .set(
                                            favoriteMap
                                        )

                                    firestore.collection("favorites")
                                        .document(uid)
                                        .collection("meals")
                                        .document(recipe.id)
                                        .set(favoriteMap)

                                    if (recipe.source == "firebase") {

                                        firestore.collection("recipes")
                                            .document(recipe.id)
                                            .update(
                                                "favoriteCount",
                                                FieldValue.increment(1)
                                            )
                                    }

                                    Toast.makeText(

                                        requireContext(),

                                        "${recipe.title} added ❤️",

                                        Toast.LENGTH_SHORT

                                    ).show()

                                } else {

                                    repository.deleteFavorite(

                                        FavoriteRecipe(

                                            id =
                                                recipe.id,

                                            title =
                                                recipe.title,

                                            image =
                                                recipe.image

                                        )

                                    )

                                    recipeAdapter
                                        .removeFavorite(
                                            recipe.id
                                        )
                                    recipeAdapter.notifyDataSetChanged()

                                    val uid =
                                        auth.currentUser?.uid
                                            ?: ""

                                    firestore
                                        .collection(
                                            "favorites"
                                        )
                                        .document(uid)
                                        .collection(
                                            "meals"
                                        )
                                        .document(
                                            recipe.id
                                        )
                                        .delete()

                                    if (recipe.source == "firebase") {

                                        firestore.collection("recipes")
                                            .document(recipe.id)
                                            .update(
                                                "favoriteCount",
                                                FieldValue.increment(-1)
                                            )
                                    }

                                    Toast.makeText(

                                        requireContext(),

                                        "Removed from favorite",

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

            binding.rvRecipes.layoutManager =
                LinearLayoutManager(
                    requireContext()
                )

            binding.rvRecipes.adapter =
                recipeAdapter
        }
    }

    override fun onResume() {

        super.onResume()

        loadRecentlyViewed()

        lifecycleScope.launch {

            try {

                val favoriteIds =
                    repository.getAllFavoriteIds()

                if (::recipeAdapter.isInitialized) {

                    recipeAdapter.setFavoriteIds(
                        favoriteIds.toSet()
                    )
                }

            } catch (e: Exception) {

                e.printStackTrace()
            }
        }
    }
    override fun onDestroyView() {

        super.onDestroyView()

        _binding = null
    }
}