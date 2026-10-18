package com.example.smart_cook.ui.chat

import com.example.smart_cook.model.ChatMessage

data class ChatUiState(

    val messages:
    List<ChatMessage> = emptyList(),

    val isTyping: Boolean = false,

    val error: String? = null
)