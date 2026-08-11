plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.detekt)
}

android {
    namespace = "com.pjdev.presentation"

    compileSdk {
        version = release(37)
    }

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }



    buildTypes {

        debug {
            enableUnitTestCoverage = true
        }

        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    lint {
        abortOnError = true
        warningsAsErrors = false
    }
}

dependencies {
    // Internal project modules.
    implementation(project(":domain"))

    // Compose.
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    // Lifecycle and ViewModel.
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Coroutines.
    implementation(libs.kotlinx.coroutines.core)

    // Paging.
    implementation(libs.androidx.paging.common)
    implementation(libs.androidx.paging.compose)

    // Dependency injection.
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Unit testing.
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)

    // Compose tooling available only in debug builds.
    debugImplementation(libs.androidx.compose.ui.tooling)

    // Image loading.
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
}

detekt {
    config.setFrom(rootProject.files("config/detekt/detekt.yml"))
    buildUponDefaultConfig = true
}