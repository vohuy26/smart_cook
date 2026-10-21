package com.example.smart_cook.ui.add_recipe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast

import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController

import com.example.smart_cook.R
import com.example.smart_cook.databinding.FragmentAddUserRecipeBinding

import dagger.hilt.android.AndroidEntryPoint

import kotlinx.coroutines.launch


@AndroidEntryPoint
class AddUserRecipeFragment : Fragment() {

    private var _binding:
            FragmentAddUserRecipeBinding? = null

    private val binding
        get() = _binding!!

    private val viewModel:
            AddUserRecipeViewModel by viewModels()


    // =========================
    // IMAGE PICKER
    // =========================

    private val imagePicker =
        registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->

            uri ?: return@registerForActivityResult

            binding.imgRecipePreview
                .setImageURI(uri)

            viewModel.onImageSelected(
                uri.toString()
            )
        }


    // =========================
    // CREATE VIEW
    // =========================

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =
            FragmentAddUserRecipeBinding.inflate(
                inflater,
                container,
                false
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

        setupCategory()

        setupInputs()

        setupActions()

        observeState()

        observeMessage()
    }


    // =========================
    // CATEGORY
    // =========================

    private fun setupCategory() {

        val adapter =
            ArrayAdapter.createFromResource(
                requireContext(),
                R.array.recipe_categories_admin,
                android.R.layout.simple_spinner_item
            )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        binding.spCategory.adapter =
            adapter

        binding.spCategory
            .setSelection(0)


        binding.spCategory
            .onItemSelectedListener =
            object :
                android.widget.AdapterView
                .OnItemSelectedListener {

                override fun onItemSelected(
                    parent:
                    android.widget.AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                    val category =
                        parent
                            ?.getItemAtPosition(
                                position
                            )
                            ?.toString()
                            .orEmpty()

                    viewModel
                        .onCategoryChanged(
                            category
                        )
                }


                override fun onNothingSelected(
                    parent:
                    android.widget.AdapterView<*>?
                ) {
                }
            }
    }


    // =========================
    // INPUTS
    // =========================

    private fun setupInputs() {

        binding.edtTitle
            .addTextChangedListener {

                viewModel.onTitleChanged(
                    it.toString()
                )
            }


        binding.edtArea
            .addTextChangedListener {

                viewModel.onAreaChanged(
                    it.toString()
                )
            }


        binding.edtDescription
            .addTextChangedListener {

                viewModel.onDescriptionChanged(
                    it.toString()
                )
            }


        binding.edtIngredients
            .addTextChangedListener {

                viewModel.onIngredientsChanged(
                    it.toString()
                )
            }


        binding.edtSteps
            .addTextChangedListener {

                viewModel.onStepsChanged(
                    it.toString()
                )
            }


        binding.edtCookingTime
            .addTextChangedListener {

                viewModel.onCookingTimeChanged(
                    it.toString()
                )
            }


        binding.edtServings
            .addTextChangedListener {

                viewModel.onServingsChanged(
                    it.toString()
                )
            }


        binding.edtYoutube
            .addTextChangedListener {

                viewModel.onYoutubeChanged(
                    it.toString()
                )
            }
    }


    // =========================
    // ACTIONS
    // =========================

    private fun setupActions() {

        binding.btnChooseImage
            .setOnClickListener {

                imagePicker.launch(
                    "image/*"
                )
            }


        binding.btnSubmitRecipe
            .setOnClickListener {

                viewModel.submitRecipe()
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

                        viewModel.uiState
                            .collect { state ->


                                binding.progressSubmit.visibility =
                                    if (
                                        state.isSubmitting
                                    ) {
                                        View.VISIBLE
                                    } else {
                                        View.GONE
                                    }


                                binding.btnSubmitRecipe.isEnabled =
                                    !state.isSubmitting


                                binding.btnChooseImage.isEnabled =
                                    !state.isSubmitting


                                if (
                                    !state.imageUri
                                        .isNullOrBlank()
                                ) {

                                    binding.imgRecipePreview
                                        .setImageURI(
                                            state.imageUri
                                                .toUri()
                                        )
                                }


                                state.error
                                    ?.let {

                                        Toast.makeText(
                                            requireContext(),
                                            it,
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        viewModel.consumeError()
                                    }


                                if (
                                    state.isSubmitted
                                ) {

                                    viewModel
                                        .consumeSubmitted()

                                    findNavController()
                                        .navigateUp()
                                }
                            }
                    }
            }
    }


    // =========================
    // MESSAGE
    // =========================

    private fun observeMessage() {

        viewLifecycleOwner
            .lifecycleScope
            .launch {

                viewLifecycleOwner
                    .repeatOnLifecycle(
                        Lifecycle.State.STARTED
                    ) {

                        viewModel.message
                            .collect {

                                Toast.makeText(
                                    requireContext(),
                                    it,
                                    Toast.LENGTH_LONG
                                ).show()
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