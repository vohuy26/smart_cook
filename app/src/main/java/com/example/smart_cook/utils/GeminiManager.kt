package com.example.smart_cook.utils

import com.google.ai.client.generativeai.GenerativeModel

object GeminiManager {

    private const val API_KEY =
        "DÁN_API_KEY_CỦA_BẠN"

    private val generativeModel =
        GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = API_KEY
        )

    suspend fun askAI(
        question: String
    ): String {

        return try {

            val prompt = """
                Bạn là AI đầu bếp thông minh trong app Smart Cook.
                
                Hãy trả lời bằng tiếng Việt.
                
                Người dùng hỏi:
                
                $question
            """.trimIndent()

            val response =
                generativeModel.generateContent(
                    prompt
                )

            response.text
                ?: "Không có phản hồi"

        } catch (e: Exception) {

            e.message
                ?: "Lỗi AI"
        }
    }
}