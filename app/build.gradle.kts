import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)

    kotlin("kapt")

    alias(libs.plugins.hilt)

    id("com.google.gms.google-services")
}

val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")

    if (localPropertiesFile.exists()) {
        localPropertiesFile.inputStream().use {
            load(it)
        }
    }
}



val geminiApiKey =
    localProperties.getProperty("GEMINI_API_KEY", "")

android {
    namespace = "com.example.smart_cook"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.smart_cook"
        minSdk = 24
        targetSdk = 35

        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"

            buildConfigField(
                "String",
                "GEMINI_API_KEY",
                "\"$geminiApiKey\""
    )
    }

    buildTypes {
        release {
            isMinifyEnabled = false

            proguardFiles(
                getDefaultProguardFile(
                    "proguard-android-optimize.txt"
                ),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility =
            JavaVersion.VERSION_17

        targetCompatibility =
            JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }


}

kapt {
    correctErrorTypes = true
}

dependencies {
    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // Core
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)

    // RecyclerView
    implementation(
        "androidx.recyclerview:recyclerview:1.3.2"
    )

    // CardView
    implementation(
        "androidx.cardview:cardview:1.0.0"
    )

    // Lifecycle
    implementation(libs.lifecycle.livedata)
    implementation(libs.lifecycle.viewmodel)

    // Navigation
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)

    // OkHttp Logging
    implementation(libs.okhttp)

    // Room Database
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)

    kapt(libs.room.compiler)

    // Glide
    implementation(
        "com.github.bumptech.glide:glide:4.16.0"
    )

    kapt(
        "com.github.bumptech.glide:compiler:4.16.0"
    )

    // Coroutine
    implementation(libs.coroutines)

    // Unit Test
    testImplementation(
        "junit:junit:4.13.2"
    )

    // Android Test
    androidTestImplementation(
        "androidx.test.ext:junit:1.2.1"
    )

    androidTestImplementation(
        "androidx.test.espresso:espresso-core:3.6.1"
    )

    implementation("com.github.bumptech.glide:glide:4.16.0")
    kapt("com.github.bumptech.glide:compiler:4.16.0")

    implementation("com.google.firebase:firebase-auth-ktx:23.1.0")

    //API germini
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")

    //database
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)

    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")

    implementation("com.tbuonomo:dotsindicator:5.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")

    //video
    implementation("androidx.browser:browser:1.8.0")

    //admin
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.cardview:cardview:1.0.0")

    //ảnh
    implementation("com.google.firebase:firebase-storage-ktx")
    implementation("com.cloudinary:cloudinary-android:2.3.1")
    implementation("com.cloudinary:cloudinary-android:3.0.2")

    dependencies {

        implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    }

    implementation("com.github.bumptech.glide:glide:4.16.0")
    kapt ("com.github.bumptech.glide:ksp:4.16.0")

}