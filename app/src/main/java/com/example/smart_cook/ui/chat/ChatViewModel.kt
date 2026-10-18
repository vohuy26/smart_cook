package com.example.smart_cook.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.smart_cook.model.ChatMessage
import com.example.smart_cook.repository.ChatRepository

import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import javax.inject.Inject


@HiltViewModel
class ChatViewModel @Inject constructor(

    private val chatRepository:
    ChatRepository

) : ViewModel() {


    // =========================
    // UI STATE
    // =========================

    private val _uiState =
        MutableStateFlow(
            ChatUiState()
        )

    val uiState:
            StateFlow<ChatUiState> =
        _uiState.asStateFlow()


    // =========================
    // SEND MESSAGE
    // =========================

    fun sendMessage(
        text: String
    ) {

        val message =
            text.trim()


        // Không gửi message rỗng
        if (message.isBlank()) {
            return
        }


        // Không cho gửi tiếp khi AI
        // đang xử lý message trước.
        if (_uiState.value.isTyping) {
            return
        }


        val userMessage =
            ChatMessage(
                message = message,
                isUser = true,
                time = getCurrentTime()
            )


        // =========================
        // ADD USER MESSAGE
        // =========================

        _uiState.value =
            _uiState.value.copy(
                messages =
                    _uiState.value.messages +
                            userMessage,
                isTyping = true,
                error = null
            )


        // =========================
        // ASK AI
        // =========================

        viewModelScope.launch {

            try {

                val aiResponse =
                    chatRepository
                        .askAI(
                            message
                        )


                val botMessage =
                    ChatMessage(
                        message =
                            aiResponse,
                        isUser =
                            false,
                        time =
                            getCurrentTime()
                    )


                // =========================
                // ADD AI MESSAGE
                // =========================

                _uiState.value =
                    _uiState.value.copy(
                        messages =
                            _uiState.value.messages +
                                    botMessage,
                        isTyping = false,
                        error = null
                    )


            } catch (e: Exception) {

                _uiState.value =
                    _uiState.value.copy(
                        isTyping = false,
                        error =
                            e.message
                                ?: "Unable to get AI response"
                    )
            }
        }
    }


    // =========================
    // CONSUME ERROR
    // =========================

    fun consumeError() {

        if (_uiState.value.error == null) {
            return
        }

        _uiState.value =
            _uiState.value.copy(
                error = null
            )
    }


    // =========================
    // CURRENT TIME
    // =========================

    private fun getCurrentTime():
            String {

        val formatter =
            SimpleDateFormat(
                "HH:mm",
                Locale.getDefault()
            )

        return formatter.format(
            Date()
        )
    }
}