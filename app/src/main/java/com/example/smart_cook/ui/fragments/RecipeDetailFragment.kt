package com.example.smart_cook.ui.fragments

import android.content.Intent
import android.net.Uri
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

import com.bumptech.glide.Glide

import com.example.smart_cook.R
import com.example.smart_cook.databinding.FragmentRecipeDetailBinding
import com.example.smart_cook.model.Meal
import com.example.smart_cook.ui.recipe_detail.RecipeDetailViewModel

import dagger.hilt.android.AndroidEntryPoint

import kotlinx.coroutines.launch


@AndroidEntryPoint
class RecipeDetailFragment : Fragment() {

    private var _binding:
            FragmentRecipeDetailBinding? = null

    private val binding
        get() = _binding!!

    private val viewModel:
            RecipeDetailViewModel by viewModels()


    // =========================
    // CREATE VIEW
    // =========================

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =
            FragmentRecipeDetailBinding.inflate(
                inflater,
                container,
                false
            )

        setupListeners()

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

        val mealId =
            arguments
                ?.getString(
                    "mealId"
                )
                .orEmpty()

        if (mealId.isNotBlank()) {

            viewModel
                .loadMealDetail(
                    mealId
                )

        } else {

            Toast.makeText(
                requireContext(),
                "Invalid recipe ID",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    // =========================
    // LISTENERS
    // =========================

    private fun setupListeners() {

        binding.btnBack
            .setOnClickListener {

                requireActivity()
                    .onBackPressedDispatcher
                    .onBackPressed()
            }


        binding.btnFavorite
            .setOnClickListener {

                viewModel
                    .toggleFavorite()
            }


        binding.btnYoutube
            .setOnClickListener {

                openYoutube()
            }
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

                                    binding
                                        .btnFavorite
                                        .isEnabled =
                                        !state.isLoading

                                    binding
                                        .btnYoutube
                                        .isEnabled =
                                        !state.isLoading


                                    state.meal
                                        ?.let { meal ->

                                            showMeal(
                                                meal
                                            )
                                        }


                                    updateFavoriteIcon(
                                        state.isFavorite
                                    )


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
    // SHOW RECIPE
    // =========================

    private fun showMeal(
        meal: Meal
    ) {

        Glide.with(
            binding.imgRecipe
        )
            .load(
                meal.image
            )
            .centerCrop()
            .into(
                binding.imgRecipe
            )


        binding.txtTitle.text =
            meal.title

        binding.txtCategory.text =
            meal.category

        binding.txtArea.text =
            meal.area

        binding.txtInstructions.text =
            meal.instructions


        // Tạm thời giữ nội dung cũ.
        // Ingredient sẽ refactor ở phase sau.
        binding.txtIngredients.text =
            "Nguyên liệu đang cập nhật"
    }


    // =========================
    // FAVORITE UI
    // =========================

    private fun updateFavoriteIcon(
        isFavorite: Boolean
    ) {

        if (isFavorite) {

            binding.btnFavorite
                .setImageResource(
                    android.R.drawable.btn_star_big_on
                )

        } else {

            binding.btnFavorite
                .setImageResource(
                    R.drawable.ic_favorite
                )
        }
    }


    // =========================
    // YOUTUBE
    // =========================

    private fun openYoutube() {

        val youtubeLink =
            viewModel
                .uiState
                .value
                .meal
                ?.youtube
                .orEmpty()

        if (youtubeLink.isNotBlank()) {

            try {

                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(
                            youtubeLink
                        )
                    )
                )

            } catch (e: Exception) {

                Toast.makeText(
                    requireContext(),
                    "Unable to open Youtube",
                    Toast.LENGTH_SHORT
                ).show()
            }

        } else {

            Toast.makeText(
                requireContext(),
                "No Youtube Video",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    // =========================
    // ON RESUME
    // =========================

    override fun onResume() {

        super.onResume()

        viewModel
            .refreshFavorite()
    }


    // =========================
    // DESTROY
    // =========================

    override fun onDestroyView() {

        super.onDestroyView()

        _binding = null
    }
}