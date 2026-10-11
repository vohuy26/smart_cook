package com.example.smart_cook.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.smart_cook.R

import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.navigation.fragment.findNavController

import com.example.smart_cook.data.local.AppDatabase
import com.example.smart_cook.databinding.FragmentFavoriteBinding
import com.example.smart_cook.model.FavoriteRecipe
import com.example.smart_cook.repository.FavoriteRepository
import com.example.smart_cook.ui.adapters.FavoriteAdapter

import kotlinx.coroutines.launch

class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    private lateinit var favoriteAdapter: FavoriteAdapter
    private lateinit var repository: FavoriteRepository

    // =========================
    // FAVORITE LIST
    // =========================

    private val favoriteList =
        mutableListOf<FavoriteRecipe>()

    private val allFavoriteList =
        mutableListOf<FavoriteRecipe>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =
            FragmentFavoriteBinding.inflate(
                inflater,
                container,
                false
            )

        val dao =
            AppDatabase
                .getDatabase(requireContext())
                .favoriteDao()

        repository =
            FavoriteRepository(dao)

        setupRecyclerView()

        setupSwipeDelete()

        loadFavorites()

        setupSearch()

        return binding.root
    }

    // =========================
    // SEARCH
    // =========================

    private fun setupSearch() {

        binding.edtSearch.addTextChangedListener(

            object : TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {

                    searchFavorite(
                        s.toString()
                    )
                }

                override fun afterTextChanged(
                    s: Editable?
                ) {
                }
            }
        )
    }

    private fun searchFavorite(
        keyword: String
    ) {

        val filteredList =

            allFavoriteList.filter {

                it.title.contains(
                    keyword,
                    ignoreCase = true
                )

            }

        favoriteAdapter.updateList(
            filteredList
        )

        if (filteredList.isEmpty()) {

            binding.layoutEmpty.visibility =
                View.VISIBLE

            binding.rvFavorites.visibility =
                View.GONE

        } else {

            binding.layoutEmpty.visibility =
                View.GONE

            binding.rvFavorites.visibility =
                View.VISIBLE
        }
    }

    // =========================
    // RECYCLER VIEW
    // =========================

    private fun setupRecyclerView() {

        favoriteAdapter =

            FavoriteAdapter(

                favoriteList,

                onDelete = { recipe ->

                    deleteFavorite(
                        recipe
                    )

                },

                onItemClick = { recipe ->

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

            )

        binding.rvFavorites.layoutManager =

            LinearLayoutManager(
                requireContext()
            )

        binding.rvFavorites.adapter =
            favoriteAdapter

    }
    // =========================
    // LOAD FAVORITES
    // =========================

    private fun loadFavorites() {

        lifecycleScope.launch {

            try {

                val favorites =
                    repository.getAllFavorites()

                allFavoriteList.clear()

                allFavoriteList.addAll(
                    favorites
                )

                favoriteList.clear()

                favoriteList.addAll(
                    favorites
                )

                favoriteAdapter.updateList(
                    favorites
                )

                checkEmptyState()

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
    // EMPTY STATE
    // =========================

    private fun checkEmptyState() {

        if (favoriteList.isEmpty()) {

            binding.layoutEmpty.visibility =
                View.VISIBLE

            binding.rvFavorites.visibility =
                View.GONE

        } else {

            binding.layoutEmpty.visibility =
                View.GONE

            binding.rvFavorites.visibility =
                View.VISIBLE
        }
    }

    // =========================
    // DELETE FAVORITE
    // =========================

    private fun deleteFavorite(
        recipe: FavoriteRecipe
    ) {

        lifecycleScope.launch {

            try {

                repository.deleteFavorite(
                    recipe
                )

                favoriteList.remove(
                    recipe
                )

                allFavoriteList.remove(
                    recipe
                )

                favoriteAdapter.updateList(
                    favoriteList
                )

                checkEmptyState()

                Toast.makeText(

                    requireContext(),

                    "Removed from favorite ❤️",

                    Toast.LENGTH_SHORT

                ).show()

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
    // SWIPE DELETE
    // =========================

    private fun setupSwipeDelete() {

        val swipeHandler =

            object : ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT
            ) {

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {

                    return false
                }

                override fun onSwiped(
                    viewHolder: RecyclerView.ViewHolder,
                    direction: Int
                ) {

                    val position =
                        viewHolder.adapterPosition

                    val recipe =
                        favoriteList[position]

                    deleteFavorite(
                        recipe
                    )
                }
            }

        ItemTouchHelper(
            swipeHandler
        ).attachToRecyclerView(
            binding.rvFavorites
        )
    }

    override fun onResume() {

        super.onResume()

        loadFavorites()

    }

    override fun onDestroyView() {

        super.onDestroyView()

        _binding = null

    }
}