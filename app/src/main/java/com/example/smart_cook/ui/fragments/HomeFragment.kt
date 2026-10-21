package com.example.smart_cook.ui.fragments
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smart_cook.R
import com.example.smart_cook.databinding.FragmentHomeBinding
import com.example.smart_cook.model.Category
import com.example.smart_cook.model.Meal
import com.example.smart_cook.model.RecentlyViewed
import com.example.smart_cook.model.Recommendation
import com.example.smart_cook.ui.adapters.CategoryAdapter
import com.example.smart_cook.ui.adapters.RecentlyViewedAdapter
import com.example.smart_cook.ui.adapters.RecipeAdapter
import com.example.smart_cook.ui.adapters.RecommendationAdapter
import com.example.smart_cook.ui.home.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModels()

    private var allMeals = mutableListOf<Meal>()
    private lateinit var recipeAdapter: RecipeAdapter
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var recommendationAdapter: RecommendationAdapter
    private lateinit var recentlyViewedAdapter: RecentlyViewedAdapter


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
        // SETUP
        // =========================

        setupCategories()

        setupRecipeAdapter()

        setupNotification()

        binding.layoutSearch.setOnClickListener {

            requireActivity()
                .findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(
                    R.id.bottomNavigationView
                )
                .selectedItemId =
                R.id.searchFragment
        }

        binding.cardRanking
            .setOnClickListener {

                findNavController()
                    .navigate(
                        R.id.rankingFragment
                    )
            }

        return binding.root
    }

    private fun setupNotification() {

        binding.cardNotification
            .setOnClickListener {

                findNavController()
                    .navigate(
                        R.id.notificationFragment
                    )
            }
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(
            view,
            savedInstanceState
        )

        observeHomeState()

        homeViewModel.loadUsername()

        homeViewModel.loadHome()
    }


    private fun renderRecentlyViewed(
        viewedList: List<RecentlyViewed>
    ) {

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
    private fun observeHomeState() {

            viewLifecycleOwner
                .lifecycleScope
                .launch {

                    viewLifecycleOwner
                        .repeatOnLifecycle(
                            Lifecycle.State.STARTED
                        ) {

                            launch {

                                homeViewModel
                                    .uiState
                                    .collect { state ->

                                        binding.txtUsername.text =
                                            state.username

                                        allMeals.clear()

                                        allMeals.addAll(
                                            state.meals
                                        )

                                        if (
                                            ::recipeAdapter
                                                .isInitialized
                                        ) {

                                            recipeAdapter
                                                .updateData(
                                                    state.meals
                                                )

                                            recipeAdapter
                                                .setFavoriteIds(
                                                    state.favoriteIds
                                                )
                                        }
                                        renderRecentlyViewed(
                                            state.recentlyViewed
                                        )

                                        val recommendationList =
                                            state
                                                .recommendedMeals
                                                .map { meal ->

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

                                        binding
                                            .rvRecommendations
                                            .layoutManager =
                                            LinearLayoutManager(
                                                requireContext(),
                                                LinearLayoutManager.HORIZONTAL,
                                                false
                                            )

                                        binding
                                            .rvRecommendations
                                            .adapter =
                                            recommendationAdapter


                                        state.error?.let {

                                            Toast.makeText(
                                                requireContext(),
                                                it,
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                            }


                            launch {

                                homeViewModel
                                    .favoriteMessage
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


    private fun setupRecipeAdapter() {

        recipeAdapter =
            RecipeAdapter(

                recipeList =
                    allMeals,

                favoriteIds =
                    mutableSetOf(),

                onItemClick = { recipe ->

                    homeViewModel
                        .addRecentlyViewed(
                            recipe
                        )

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

                    homeViewModel
                        .toggleFavorite(
                            recipe
                        )
                }
            )

        binding.rvRecipes.layoutManager =
            LinearLayoutManager(
                requireContext()
            )

        binding.rvRecipes.adapter =
            recipeAdapter
    }

    override fun onResume() {

        super.onResume()

        homeViewModel
            .loadRecentlyViewed()

        homeViewModel
            .loadFavorites()
    }
    override fun onDestroyView() {

        super.onDestroyView()

        _binding = null
    }
}