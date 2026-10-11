package com.example.smart_cook.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smart_cook.databinding.FragmentChatBinding
import com.example.smart_cook.model.ChatMessage
import com.example.smart_cook.ui.adapters.ChatAdapter
import com.example.smart_cook.utils.GeminiManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatFragment : Fragment() {

    private var _binding:
            FragmentChatBinding? = null

    private val binding get() = _binding!!

    private lateinit var chatAdapter:
            ChatAdapter

    private val chatList =
        mutableListOf<ChatMessage>()

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

    private fun setupRecyclerView() {

        chatAdapter =
            ChatAdapter(chatList)

        binding.rvChat.layoutManager =
            LinearLayoutManager(
                requireContext()
            )

        binding.rvChat.adapter =
            chatAdapter
    }

    private fun setupSendMessage() {

        binding.btnSend.setOnClickListener {

            val message =
                binding.edtMessage.text
                    .toString()
                    .trim()

            if (message.isEmpty()) {
                return@setOnClickListener
            }

            val userMessage =
                ChatMessage(
                    message = message,
                    isUser = true,
                    time = getCurrentTime()
                )

            chatList.add(userMessage)

            chatAdapter.notifyItemInserted(
                chatList.size - 1
            )

            binding.rvChat.scrollToPosition(
                chatList.size - 1
            )

            binding.edtMessage.text.clear()

            lifecycleScope.launch {

                val typingMessage =
                    ChatMessage(
                        message = "🤖 Đang trả lời...",
                        isUser = false,
                        time = getCurrentTime()
                    )

                chatList.add(typingMessage)

                chatAdapter.notifyItemInserted(
                    chatList.size - 1
                )

                binding.rvChat.scrollToPosition(
                    chatList.size - 1
                )

                val aiResponse =
                    GeminiManager.askAI(message)

                chatList.removeAt(
                    chatList.size - 1
                )

                chatAdapter.notifyDataSetChanged()

                val botMessage =
                    ChatMessage(
                        message = aiResponse,
                        isUser = false,
                        time = getCurrentTime()
                    )

                chatList.add(botMessage)

                chatAdapter.notifyItemInserted(
                    chatList.size - 1
                )

                binding.rvChat.scrollToPosition(
                    chatList.size - 1
                )
            }
        }
    }

    private fun getCurrentTime(): String {

        val sdf =
            SimpleDateFormat(
                "HH:mm",
                Locale.getDefault()
            )

        return sdf.format(Date())
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}