package com.example.smart_cook.repository

import com.example.smart_cook.data.ai.GeminiApi

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class ChatRepository {

    suspend fun askAI(
        question: String
    ): String {

        return withContext(
            Dispatchers.IO
        ) {

            GeminiApi.askGemini(
                question
            )
        }
    }
}