package com.example.smart_cook.data.ai

import com.example.smart_cook.BuildConfig

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

import org.json.JSONArray
import org.json.JSONObject


object GeminiApi {

    private const val MODEL =
        "gemini-3.8-flash"

    private val client =
        OkHttpClient()


    fun askGemini(
        question: String
    ): String {

        if (question.isBlank()) {

            return "Please enter a question."
        }


        val apiKey =
            BuildConfig.GEMINI_API_KEY

        if (apiKey.isBlank()) {

            return "Gemini API key is missing."
        }


        return try {

            // =========================
            // URL
            // =========================

            val url =
                "https://generativelanguage.googleapis.com/v1beta/models/$MODEL:generateContent?key=$apiKey"


            // =========================
            // REQUEST JSON
            // =========================

            val json =
                JSONObject()

            val contents =
                JSONArray()

            val content =
                JSONObject()

            val parts =
                JSONArray()

            val part =
                JSONObject()


            part.put(
                "text",
                """
                You are Smart Cook AI, a cooking assistant.

                Only answer questions related to:
                - cooking
                - recipes
                - ingredients
                - food
                - nutrition
                - kitchen tips

                Keep the answer clear and concise.

                User question:
                $question
                """.trimIndent()
            )


            parts.put(
                part
            )

            content.put(
                "parts",
                parts
            )

            contents.put(
                content
            )

            json.put(
                "contents",
                contents
            )


            // =========================
            // REQUEST BODY
            // =========================

            val body =
                json.toString()
                    .toRequestBody(
                        "application/json"
                            .toMediaType()
                    )


            val request =
                Request.Builder()
                    .url(
                        url
                    )
                    .post(
                        body
                    )
                    .build()


            // =========================
            // RESPONSE
            // =========================

            val aiResult: String =

                client
                    .newCall(
                        request
                    )
                    .execute()
                    .use { response ->

                        val responseBody =
                            response.body
                                ?.string()


                        when {

                            !response.isSuccessful -> {

                                "AI request failed (${response.code})"
                            }


                            responseBody.isNullOrBlank() -> {

                                "AI returned an empty response."
                            }


                            else -> {

                                val result =
                                    JSONObject(
                                        responseBody
                                    )

                                val candidates =
                                    result.optJSONArray(
                                        "candidates"
                                    )


                                if (
                                    candidates == null ||
                                    candidates.length() == 0
                                ) {

                                    "AI could not generate a response."

                                } else {

                                    candidates
                                        .optJSONObject(0)
                                        ?.optJSONObject(
                                            "content"
                                        )
                                        ?.optJSONArray(
                                            "parts"
                                        )
                                        ?.optJSONObject(0)
                                        ?.optString(
                                            "text"
                                        )
                                        ?.takeIf {
                                            it.isNotBlank()
                                        }
                                        ?: "AI returned an empty response."
                                }
                            }
                        }
                    }


            aiResult


        } catch (e: Exception) {

            e.message
                ?: "Unable to connect to AI."
        }
    }
}