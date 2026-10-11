package com.example.smart_cook.utils

import android.content.Context
import com.cloudinary.android.MediaManager

object CloudinaryManager {

    private var initialized = false

    fun init(context: Context) {

        if (initialized) return

        val config = HashMap<String, String>()

        config["cloud_name"] = "dogglclp9"

        MediaManager.init(context, config)

        initialized = true
    }
}