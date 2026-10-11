package com.example.smart_cook.data.ai

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import com.example.smart_cook.BuildConfig

object GeminiApi {

    private val API_KEY
        get() = BuildConfig.GEMINI_API_KEY

    private val BASE_URL
        get() =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=${BuildConfig.GEMINI_API_KEY}"

    private val client =
        OkHttpClient()

    fun askGemini(
        question: String
    ): String {

        return try {

            val json =
                JSONObject()

            val contents =
                JSONArray()

            val contentObject =
                JSONObject()

            val parts =
                JSONArray()

            val part =
                JSONObject()

            part.put(
                "text",
                "You are Smart Cook AI. Answer shortly about cooking and food only. User question: $question"
            )

            parts.put(part)

            contentObject.put(
                "parts",
                parts
            )

            contents.put(contentObject)

            json.put(
                "contents",
                contents
            )

            val body =
                json.toString()
                    .toRequestBody(
                        "application/json"
                            .toMediaType()
                    )

            val request =
                Request.Builder()
                    .url(BASE_URL)
                    .post(body)
                    .build()

            val response =
                client.newCall(request)
                    .execute()

            val responseBody =
                response.body?.string()

            val result =
                JSONObject(responseBody!!)

            result
                .getJSONArray("candidates")
                .getJSONObject(0)
                .getJSONObject("content")
                .getJSONArray("parts")
                .getJSONObject(0)
                .getString("text")

        } catch (e: Exception) {

            e.message ?: "Error"
        }
    }
}