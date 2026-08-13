plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.detekt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.pjdev.pjdevmultiverseapp"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.pjdev.pjdevmultiverseapp"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }

    lint {
        abortOnError = true
        warningsAsErrors = false
    }

}

dependencies {

    // Internal project modules.
    implementation(project(":presentation"))
    implementation(project(":data"))
    implementation(project(":domain"))

    // Android application entry point with Compose support.
    implementation(libs.androidx.activity.compose)

    // Unit testing.
    testImplementation(libs.junit)

    // Dependency injection.
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.lifecycle.viewmodel.compose)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Provides a consistent splash screen across supported Android versions.
    implementation(libs.androidx.core.splashscreen)

    implementation(libs.kotlinx.serialization.json)

}

detekt {
    config.setFrom(rootProject.files("config/detekt/detekt.yml"))
    buildUponDefaultConfig = true
}
