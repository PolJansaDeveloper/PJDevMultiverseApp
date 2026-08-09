plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.detekt)
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
            isMinifyEnabled = false
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

    // Android application entry point with Compose support.
    implementation(libs.androidx.activity.compose)

    // Unit testing.
    testImplementation(libs.junit)

    // Dependency injection.
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}

detekt {
    config.setFrom(rootProject.files("config/detekt/detekt.yml"))
    buildUponDefaultConfig = true
}
