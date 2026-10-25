plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "co.edu.uniquindio.servify"

    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "co.edu.uniquindio.servify"

        minSdk = 28
        targetSdk = 37

        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
    }
}

dependencies {

    // -----------------------------------------
    // Compose
    // -----------------------------------------

    implementation(platform(libs.androidx.compose.bom))

    implementation(libs.androidx.activity.compose)

    implementation(libs.androidx.compose.material3)

    implementation(libs.androidx.compose.ui)

    implementation(libs.androidx.compose.ui.graphics)

    implementation(libs.androidx.compose.ui.tooling.preview)

    // -----------------------------------------
    // Android
    // -----------------------------------------

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)

    implementation(libs.androidx.lifecycle.runtime.ktx)

    // -----------------------------------------
    // Dependency Injection (Hilt & Inject)
    // -----------------------------------------

    implementation("javax.inject:javax.inject:1")
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)

    // -----------------------------------------
    // Navigation
    // -----------------------------------------

    implementation(libs.androidx.navigation.compose)

    // -----------------------------------------
    // ViewModel + Compose
    // -----------------------------------------

    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.viewmodel.savedstate)

    // -----------------------------------------
    // Inyección de dependencias (Hilt + KSP)
    // -----------------------------------------

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.lifecycle.viewmodel.compose)

    // -----------------------------------------
    // Testing
    // -----------------------------------------

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)

    androidTestImplementation(
        platform(libs.androidx.compose.bom)
    )

    androidTestImplementation(
        libs.androidx.compose.ui.test.junit4
    )

    androidTestImplementation(
        libs.androidx.espresso.core
    )

    androidTestImplementation(
        libs.androidx.junit
    )

    debugImplementation(
        libs.androidx.compose.ui.test.manifest
    )

    debugImplementation(
        libs.androidx.compose.ui.tooling
    )

    implementation("androidx.compose.material:material-icons-extended")
}