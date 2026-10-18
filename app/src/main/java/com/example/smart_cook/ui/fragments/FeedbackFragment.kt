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

import com.example.smart_cook.databinding.FragmentFeedbackBinding
import com.example.smart_cook.ui.feedback.FeedbackViewModel
import androidx.core.widget.addTextChangedListener

import dagger.hilt.android.AndroidEntryPoint

import kotlinx.coroutines.launch


@AndroidEntryPoint
class FeedbackFragment : Fragment() {

    private var _binding:
            FragmentFeedbackBinding? = null

    private val binding
        get() = _binding!!

    private val viewModel:
            FeedbackViewModel by viewModels()


    // =========================
    // CREATE VIEW
    // =========================

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =
            FragmentFeedbackBinding.inflate(
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
    }


    // =========================
    // LISTENERS
    // =========================

    private fun setupListeners() {

        binding.edtComment
            .addTextChangedListener {

                viewModel
                    .onCommentChanged(
                        it?.toString()
                            .orEmpty()
                    )
            }


        binding.ratingBar
            .setOnRatingBarChangeListener {
                    _,
                    rating,
                    _ ->

                viewModel
                    .onRatingChanged(
                        rating
                    )
            }


        binding.btnSubmit
            .setOnClickListener {

                viewModel
                    .submitFeedback()
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

                                    binding.btnSubmit.isEnabled =
                                        !state.isSubmitting


                                    if (state.isSubmitted) {

                                        binding.edtComment
                                            .setText("")

                                        binding.ratingBar.rating =
                                            0f

                                        viewModel
                                            .consumeSubmitted()
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
    // DESTROY
    // =========================

    override fun onDestroyView() {

        super.onDestroyView()

        _binding = null
    }
}