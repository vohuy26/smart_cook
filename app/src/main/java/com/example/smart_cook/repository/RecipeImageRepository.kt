package com.example.smart_cook.repository

import android.net.Uri

import androidx.core.net.toUri

import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback

import kotlinx.coroutines.suspendCancellableCoroutine

import javax.inject.Inject

import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


class RecipeImageRepository @Inject constructor() {


    suspend fun uploadRecipeImage(
        uriString: String
    ): String {

        if (uriString.isBlank()) {

            throw IllegalArgumentException(
                "Recipe image is required."
            )
        }


        val uri: Uri =
            uriString.toUri()


        return suspendCancellableCoroutine { continuation ->

            MediaManager
                .get()
                .upload(uri)

                // Project hiện tại đang dùng preset này.
                .unsigned(
                    "smartcook_avatar"
                )

                .option(
                    "folder",
                    "smartcook_recipes"
                )

                .callback(

                    object : UploadCallback {


                        override fun onStart(
                            requestId: String?
                        ) {
                        }


                        override fun onProgress(
                            requestId: String?,
                            bytes: Long,
                            totalBytes: Long
                        ) {
                        }


                        override fun onSuccess(
                            requestId: String?,
                            resultData:
                            MutableMap<Any?, Any?>?
                        ) {

                            val imageUrl =
                                resultData
                                    ?.get(
                                        "secure_url"
                                    )
                                    ?.toString()


                            if (
                                imageUrl.isNullOrBlank()
                            ) {

                                if (
                                    continuation.isActive
                                ) {

                                    continuation
                                        .resumeWithException(

                                            IllegalStateException(
                                                "Image upload returned no URL."
                                            )
                                        )
                                }

                                return
                            }


                            if (
                                continuation.isActive
                            ) {

                                continuation
                                    .resume(
                                        imageUrl
                                    )
                            }
                        }


                        override fun onError(
                            requestId: String?,
                            error: ErrorInfo?
                        ) {

                            if (
                                continuation.isActive
                            ) {

                                continuation
                                    .resumeWithException(

                                        IllegalStateException(
                                            error
                                                ?.description
                                                ?: "Unable to upload recipe image."
                                        )
                                    )
                            }
                        }


                        override fun onReschedule(
                            requestId: String?,
                            error: ErrorInfo?
                        ) {
                        }
                    }
                )
                .dispatch()
        }
    }
}