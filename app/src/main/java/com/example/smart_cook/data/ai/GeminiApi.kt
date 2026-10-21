package com.example.smart_cook.data.ai

import android.util.Log
import com.example.smart_cook.BuildConfig

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

import org.json.JSONArray
import org.json.JSONObject

import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit


object GeminiApi {

    private const val MODEL =
        "gemini-3.8-flash"


    private val client =
        OkHttpClient.Builder()
            .connectTimeout(
                20,
                TimeUnit.SECONDS
            )
            .readTimeout(
                60,
                TimeUnit.SECONDS
            )
            .writeTimeout(
                30,
                TimeUnit.SECONDS
            )
            .callTimeout(
                90,
                TimeUnit.SECONDS
            )
            .retryOnConnectionFailure(
                true
            )
            .build()


    fun askGemini(
        question: String
    ): String {

        if (question.isBlank()) {
            return "Vui lòng nhập câu hỏi."
        }

        val apiKey =
            BuildConfig.GEMINI_API_KEY
                .trim()

        if (apiKey.isBlank()) {
            return "Gemini API key chưa được cấu hình."
        }

        // KHÔNG có dấu } ở đây

        return try {

            val url =
                "https://generativelanguage.googleapis.com/v1beta/models/$MODEL:generateContent?key=$apiKey"

            // phần còn lại...


            // =========================
            // PROMPT
            // =========================

            val prompt =
                """
                Bạn là Smart Cook AI, trợ lý nấu ăn thông minh.

                Chỉ trả lời các câu hỏi liên quan đến:
                - nấu ăn
                - công thức món ăn
                - nguyên liệu
                - thực phẩm
                - dinh dưỡng
                - mẹo nhà bếp
                - bảo quản thực phẩm
                - chế biến món ăn

                Yêu cầu:
                - Nếu người dùng hỏi bằng tiếng Việt, hãy trả lời bằng tiếng Việt.
                - Trình bày rõ ràng và dễ hiểu.
                - Với công thức nấu ăn, hãy liệt kê nguyên liệu trước.
                - Sau đó hướng dẫn từng bước.
                - Không trả lời quá dài nếu không cần thiết.
                - Nếu câu hỏi không liên quan đến nấu ăn hoặc thực phẩm,
                  hãy nói rằng bạn chỉ hỗ trợ lĩnh vực nấu ăn.

                Câu hỏi:
                $question
                """.trimIndent()


            // =========================
            // JSON
            // =========================

            val part =
                JSONObject()
                    .put(
                        "text",
                        prompt
                    )


            val parts =
                JSONArray()
                    .put(
                        part
                    )


            val content =
                JSONObject()
                    .put(
                        "parts",
                        parts
                    )


            val contents =
                JSONArray()
                    .put(
                        content
                    )


            val generationConfig =
                JSONObject()
                    .put(
                        "temperature",
                        0.7
                    )
                    .put(
                        "maxOutputTokens",
                        1024
                    )


            val json =
                JSONObject()
                    .put(
                        "contents",
                        contents
                    )
                    .put(
                        "generationConfig",
                        generationConfig
                    )


            // =========================
            // REQUEST
            // =========================

            val body =
                json
                    .toString()
                    .toRequestBody(
                        "application/json; charset=utf-8"
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

            client
                .newCall(
                    request
                )
                .execute()
                .use { response ->

                    val responseBody =
                        response.body
                            ?.string()
                            .orEmpty()


                    Log.d(
                        "GEMINI_DEBUG",
                        "HTTP code = ${response.code}"
                    )


                    if (!response.isSuccessful) {

                        Log.e(
                            "GEMINI_DEBUG",
                            "HTTP ${response.code}: $responseBody"
                        )


                        return@use when (
                            response.code
                        ) {

                            400 ->
                                "Yêu cầu gửi đến AI không hợp lệ."

                            401,
                            403 ->
                                "API key không hợp lệ hoặc không có quyền truy cập Gemini."

                            404 ->
                                "Không tìm thấy model AI."

                            429 ->
                                "AI đang quá tải hoặc bạn đã vượt giới hạn yêu cầu."

                            500,
                            502,
                            503,
                            504 ->
                                "Máy chủ AI đang bận. Vui lòng thử lại."

                            else ->
                                "AI request failed (${response.code})."
                        }
                    }


                    if (responseBody.isBlank()) {

                        return@use "AI trả về phản hồi rỗng."
                    }

                    val jsonResponse =
                        JSONObject(
                            responseBody
                        )


                    val candidates =
                        jsonResponse
                            .optJSONArray(
                                "candidates"
                            )


                    if (
                        candidates == null ||
                        candidates.length() == 0
                    ) {

                        Log.e(
                            "GEMINI_DEBUG",
                            "No candidates: $responseBody"
                        )

                        return@use "AI không thể tạo câu trả lời."
                    }


                    val responseParts =
                        candidates
                            .optJSONObject(0)
                            ?.optJSONObject(
                                "content"
                            )
                            ?.optJSONArray(
                                "parts"
                            )


                    if (
                        responseParts == null ||
                        responseParts.length() == 0
                    ) {

                        return@use "AI trả về nội dung rỗng."
                    }

                    val answer =
                        StringBuilder()


                    for (
                    i in 0 until
                            responseParts.length()
                    ) {

                        val text =
                            responseParts
                                .optJSONObject(i)
                                ?.optString(
                                    "text"
                                )
                                .orEmpty()


                        if (text.isNotBlank()) {

                            if (answer.isNotEmpty()) {
                                answer.append("\n")
                            }

                            answer.append(
                                text
                            )
                        }
                    }


                    answer
                        .toString()
                        .trim()
                        .ifBlank {
                            "AI trả về nội dung rỗng."
                        }
                }


        } catch (
            e: SocketTimeoutException
        ) {

            Log.e(
                "GEMINI_DEBUG",
                "Gemini timeout",
                e
            )

            "AI phản hồi quá lâu. Vui lòng thử lại."


        } catch (
            e: UnknownHostException
        ) {

            Log.e(
                "GEMINI_DEBUG",
                "Internet connection error",
                e
            )

            "Không thể kết nối Internet."


        } catch (
            e: Exception
        ) {

            Log.e(
                "GEMINI_DEBUG",
                "Gemini request failed",
                e
            )

            e.message
                ?: "Không thể kết nối với AI."
        }
    }
}