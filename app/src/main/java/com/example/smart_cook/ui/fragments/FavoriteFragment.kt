package com.example.smart_cook.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.smart_cook.R
import com.example.smart_cook.databinding.FragmentFavoriteBinding
import com.example.smart_cook.ui.adapters.FavoriteAdapter
import com.example.smart_cook.ui.favorite.FavoriteViewModel

import dagger.hilt.android.AndroidEntryPoint

import kotlinx.coroutines.launch


@AndroidEntryPoint
class FavoriteFragment : Fragment() {

    private var _binding:
            FragmentFavoriteBinding? = null

    private val binding
        get() = _binding!!

    private val viewModel:
            FavoriteViewModel by viewModels()

    private lateinit var favoriteAdapter:
            FavoriteAdapter


    // =========================
    // CREATE VIEW
    // =========================

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

        setupRecyclerView()

        setupSearch()

        setupSwipeDelete()

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

        observeState()
    }


    // =========================
    // RECYCLER VIEW
    // =========================

    private fun setupRecyclerView() {

        favoriteAdapter =
            FavoriteAdapter(

                mutableListOf(),

                onDelete = { recipe ->

                    viewModel
                        .deleteFavorite(
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
    // SEARCH
    // =========================

    private fun setupSearch() {

        binding.edtSearch
            .addTextChangedListener(

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

                        viewModel
                            .searchFavorite(
                                s?.toString()
                                    .orEmpty()
                            )
                    }


                    override fun afterTextChanged(
                        s: Editable?
                    ) {
                    }
                }
            )
    }


    // =========================
    // OBSERVE STATE
    // =========================

    private fun observeState() {

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

                            viewModel
                                .uiState
                                .collect { state ->

                                    favoriteAdapter
                                        .updateList(
                                            state.favorites
                                        )


                                    binding
                                        .layoutEmpty
                                        .visibility =

                                        if (state.isEmpty) {

                                            View.VISIBLE

                                        } else {

                                            View.GONE
                                        }


                                    binding
                                        .rvFavorites
                                        .visibility =

                                        if (state.isEmpty) {

                                            View.GONE

                                        } else {

                                            View.VISIBLE
                                        }


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

                            viewModel
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
    // SWIPE DELETE
    // =========================

    private fun setupSwipeDelete() {

        val swipeHandler =

            object :
                ItemTouchHelper.SimpleCallback(
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
                        viewHolder.bindingAdapterPosition

                    val currentFavorites =
                        viewModel
                            .uiState
                            .value
                            .favorites

                    if (
                        position != RecyclerView.NO_POSITION &&
                        position < currentFavorites.size
                    ) {

                        val recipe =
                            currentFavorites[position]

                        viewModel
                            .deleteFavorite(
                                recipe
                            )
                    }
                }
            }


        ItemTouchHelper(
            swipeHandler
        )
            .attachToRecyclerView(
                binding.rvFavorites
            )
    }


    // =========================
    // RESUME
    // =========================

    override fun onResume() {

        super.onResume()

        viewModel
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