package com.example.smart_cook.ui.my_recipes

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

import androidx.recyclerview.widget.LinearLayoutManager

import com.example.smart_cook.databinding.FragmentMyRecipesBinding

import dagger.hilt.android.AndroidEntryPoint

import kotlinx.coroutines.launch


@AndroidEntryPoint
class MyRecipesFragment : Fragment() {


    private var _binding:
            FragmentMyRecipesBinding? =
        null

    private val binding
        get() = _binding!!


    private val viewModel:
            MyRecipesViewModel by viewModels()


    private lateinit var adapter:
            MyRecipesAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =
            FragmentMyRecipesBinding.inflate(
                inflater,
                container,
                false
            )


        setupRecycler()


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


    private fun setupRecycler() {

        adapter =
            MyRecipesAdapter()


        binding.rvMyRecipes
            .layoutManager =
            LinearLayoutManager(
                requireContext()
            )


        binding.rvMyRecipes.adapter =
            adapter
    }


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


                                binding
                                    .progressLoading
                                    .visibility =

                                    if (
                                        state.isLoading
                                    ) {
                                        View.VISIBLE
                                    } else {
                                        View.GONE
                                    }


                                binding
                                    .txtEmpty
                                    .visibility =

                                    if (
                                        state.isEmpty &&
                                        !state.isLoading
                                    ) {
                                        View.VISIBLE
                                    } else {
                                        View.GONE
                                    }


                                binding
                                    .rvMyRecipes
                                    .visibility =

                                    if (
                                        state.recipes
                                            .isNotEmpty()
                                    ) {
                                        View.VISIBLE
                                    } else {
                                        View.GONE
                                    }


                                adapter.submitList(
                                    state.recipes
                                )


                                state.error
                                    ?.let { error ->

                                        Toast.makeText(
                                            requireContext(),
                                            error,
                                            Toast.LENGTH_SHORT
                                        ).show()


                                        viewModel
                                            .consumeError()
                                    }
                            }
                    }
            }
    }


    override fun onResume() {

        super.onResume()

        viewModel
            .loadMyRecipes()
    }


    override fun onDestroyView() {

        super.onDestroyView()

        _binding = null
    }
}