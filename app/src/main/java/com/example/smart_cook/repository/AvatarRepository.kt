package com.example.smart_cook.repository

import androidx.core.net.toUri

import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback

import kotlinx.coroutines.suspendCancellableCoroutine

import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


class AvatarRepository {

    suspend fun uploadAvatar(
        uriString: String
    ): String {

        val uri =
            uriString.toUri()

        return suspendCancellableCoroutine { continuation ->

            MediaManager
                .get()
                .upload(uri)
                .unsigned(
                    "smartcook_avatar"
                )
                .option(
                    "folder",
                    "smartcook_avatar"
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
                                    ) as? String

                            if (
                                !imageUrl.isNullOrBlank()
                            ) {

                                if (
                                    continuation.isActive
                                ) {

                                    continuation.resume(
                                        imageUrl
                                    )
                                }

                            } else {

                                if (
                                    continuation.isActive
                                ) {

                                    continuation
                                        .resumeWithException(
                                            IllegalStateException(
                                                "Cloudinary returned empty avatar URL"
                                            )
                                        )
                                }
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
                                        Exception(
                                            error
                                                ?.description
                                                ?: "Avatar upload failed"
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