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
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.smart_cook.databinding.FragmentNotificationBinding
import com.example.smart_cook.ui.adapters.NotificationAdapter
import com.example.smart_cook.ui.notification.NotificationViewModel

import dagger.hilt.android.AndroidEntryPoint

import kotlinx.coroutines.launch


@AndroidEntryPoint
class NotificationFragment : Fragment() {

    private var _binding:
            FragmentNotificationBinding? = null

    private val binding
        get() = _binding!!

    private val viewModel:
            NotificationViewModel by viewModels()


    // =========================
    // CREATE VIEW
    // =========================

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =
            FragmentNotificationBinding.inflate(
                inflater,
                container,
                false
            )

        setupRecyclerView()

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

        binding.rvNotifications.layoutManager =
            LinearLayoutManager(
                requireContext()
            )

        binding.rvNotifications.adapter =
            NotificationAdapter(
                emptyList()
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

                        viewModel
                            .uiState
                            .collect { state ->

                                binding.rvNotifications.adapter =
                                    NotificationAdapter(
                                        state.notifications
                                    )


                                binding.rvNotifications.visibility =

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
            }
    }


    // =========================
    // RESUME
    // =========================

    override fun onResume() {

        super.onResume()

        viewModel
            .loadNotifications()
    }


    // =========================
    // DESTROY
    // =========================

    override fun onDestroyView() {

        super.onDestroyView()

        _binding = null
    }
}