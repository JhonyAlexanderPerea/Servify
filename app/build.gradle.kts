import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

// Claves y datos de firma locales (local.properties no se sube al repositorio).
val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) file.inputStream().use { load(it) }
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
            "co.edu.uniquindio.servify.HiltTestRunner"

        // Tokens y claves: se definen en local.properties y se exponen como recursos string.
        resValue("string", "mapbox_access_token", localProperties.getProperty("MAPBOX_ACCESS_TOKEN", ""))
        resValue("string", "openrouter_api_key", localProperties.getProperty("OPENROUTER_API_KEY", ""))

        // Mapbox solo incluye librerías nativas para estas arquitecturas.
        ndk {
            abiFilters += listOf("arm64-v8a", "x86_64")
        }
    }

    signingConfigs {
        create("release") {
            // Con ?.let el build debug no falla si no están definidos en local.properties.
            storeFile = localProperties.getProperty("RELEASE_STORE_FILE")?.let { file(it) }
            storePassword = localProperties.getProperty("RELEASE_STORE_PASSWORD")
            keyAlias = localProperties.getProperty("RELEASE_KEY_ALIAS")
            keyPassword = localProperties.getProperty("RELEASE_KEY_PASSWORD")
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            // TODO(fase-3): activar R8 (enable = true) cuando los DTOs tengan @Keep.
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
        resValues = true
    }
}

dependencies {

    // -----------------------------------------
    // Compose
    // -----------------------------------------

    implementation(platform(libs.androidx.compose.bom))

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.animation)

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
    ksp(libs.hilt.compiler)
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

    androidTestImplementation(
        libs.hilt.android.testing
    )

    kspAndroidTest(
        libs.hilt.compiler
    )

    debugImplementation(
        libs.androidx.compose.ui.test.manifest
    )

    debugImplementation(
        libs.androidx.compose.ui.tooling
    )

    implementation("androidx.compose.material:material-icons-extended")
}