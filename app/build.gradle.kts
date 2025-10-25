plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.compose) // Kotlin 2.0+ Compose compiler
    alias(libs.plugins.hilt)
    kotlin("plugin.serialization") version "2.0.20"
}

android {
    namespace = "com.paam.songbook"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.paam.songbook"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    // 👇 Removed composeOptions, handled by plugin
}

dependencies {
    // Compose BOM
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    implementation(libs.navigation.compose)
    implementation(libs.androidx.activity.compose)
    implementation("androidx.compose.ui:ui-tooling-preview:1.7.0")
    implementation("androidx.compose.material:material-icons-extended:1.7.0")

    // Lifecycle + Coroutines
    implementation(libs.lifecycle.runtime)
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.coroutines.android)

    // Networking
    implementation(libs.retrofit)
    implementation(libs.retrofit.moshi)
    implementation(libs.okhttp)

    // Database
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    implementation(libs.androidx.palette.ktx)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.animation)
    kapt(libs.room.compiler)

    // Media3
    implementation(libs.media3.exoplayer)
    implementation(libs.media3.session)
    implementation(libs.media3.ui)

    // Legacy media support
    implementation("androidx.media:media:1.6.0")

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // Coil
    implementation("io.coil-kt:coil-compose:2.6.0")

    // AndroidX Core
    implementation(libs.androidx.core.ktx)

    // Coroutines Guava interop
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-guava:1.7.3")

    // JSON serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

    // Extra UI libs
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.valentinilk.shimmer:compose-shimmer:1.0.3")
    implementation("com.jakewharton.threetenabp:threetenabp:1.4.6")

    // Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
