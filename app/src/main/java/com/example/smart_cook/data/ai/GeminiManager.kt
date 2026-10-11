package com.example.smart_cook.ai

import com.google.ai.client.generativeai.GenerativeModel

object GeminiManager {

    private const val API_KEY =
        "YOUR_GEMINI_API_KEY"

    private val generativeModel =
        GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = API_KEY
        )

    suspend fun askAI(
        question: String
    ): String {

        return try {

            val response =
                generativeModel.generateContent(
                    question
                )

            response.text ?: "No response"

        } catch (e: Exception) {

            e.message ?: "AI Error"
        }
    }
}