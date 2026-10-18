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

import com.example.smart_cook.databinding.FragmentChatBinding
import com.example.smart_cook.model.ChatMessage
import com.example.smart_cook.ui.adapters.ChatAdapter
import com.example.smart_cook.ui.chat.ChatViewModel

import dagger.hilt.android.AndroidEntryPoint

import kotlinx.coroutines.launch


@AndroidEntryPoint
class ChatFragment : Fragment() {

    private var _binding:
            FragmentChatBinding? = null

    private val binding
        get() = _binding!!

    private val viewModel:
            ChatViewModel by viewModels()

    private lateinit var chatAdapter:
            ChatAdapter


    // =========================
    // CREATE VIEW
    // =========================

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =
            FragmentChatBinding.inflate(
                inflater,
                container,
                false
            )

        setupRecyclerView()

        setupSendMessage()

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

        chatAdapter =
            ChatAdapter(
                mutableListOf<ChatMessage>()
            )

        binding.rvChat.layoutManager =
            LinearLayoutManager(
                requireContext()
            )

        binding.rvChat.adapter =
            chatAdapter
    }


    // =========================
    // SEND MESSAGE
    // =========================

    private fun setupSendMessage() {

        binding.btnSend
            .setOnClickListener {

                val message =
                    binding.edtMessage
                        .text
                        .toString()
                        .trim()

                if (message.isBlank()) {
                    return@setOnClickListener
                }

                if (
                    viewModel
                        .uiState
                        .value
                        .isTyping
                ) {
                    return@setOnClickListener
                }


                viewModel
                    .sendMessage(
                        message
                    )


                binding.edtMessage
                    .text
                    .clear()
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

                        viewModel
                            .uiState
                            .collect { state ->


                                // =========================
                                // DISPLAY MESSAGES
                                // =========================

                                val displayMessages =
                                    state.messages
                                        .toMutableList()


                                // Typing indicator tạm thời
                                // hiển thị như một bot message.
                                if (state.isTyping) {

                                    displayMessages.add(

                                        ChatMessage(
                                            message =
                                                "🤖 Đang trả lời...",
                                            isUser =
                                                false,
                                            time =
                                                ""
                                        )
                                    )
                                }


                                // Adapter hiện tại chưa dùng
                                // ListAdapter nên cập nhật bằng
                                // instance mới để đảm bảo compile.
                                chatAdapter =
                                    ChatAdapter(
                                        displayMessages
                                    )

                                binding.rvChat.adapter =
                                    chatAdapter


                                // =========================
                                // SEND BUTTON
                                // =========================

                                binding.btnSend.isEnabled =
                                    !state.isTyping

                                binding.edtMessage.isEnabled =
                                    !state.isTyping


                                // =========================
                                // AUTO SCROLL
                                // =========================

                                if (
                                    displayMessages.isNotEmpty()
                                ) {

                                    binding.rvChat
                                        .scrollToPosition(
                                            displayMessages.size - 1
                                        )
                                }


                                // =========================
                                // ERROR
                                // =========================

                                state.error
                                    ?.let { message ->

                                        Toast.makeText(
                                            requireContext(),
                                            message,
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        viewModel
                                            .consumeError()
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