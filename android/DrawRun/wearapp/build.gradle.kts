plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.drawrun"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.drawrun"
        minSdk = 33
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

    }

    buildTypes {
        debug {
            isMinifyEnabled = false
        }
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
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    // Play Services 및 Wearable
    implementation(libs.play.services.wearable)

    // Compose 관련 의존성
    implementation(platform("androidx.compose:compose-bom:2024.04.01"))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.foundation:foundation")
    implementation(libs.androidx.wear.tooling.preview)
    implementation("androidx.activity:activity-compose")
    implementation(libs.androidx.core.splashscreen)
    implementation("androidx.navigation:navigation-compose")

    // 추가된 dependencies
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")  // Lifecycle runtime
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")  // ViewModel Compose
    implementation("androidx.navigation:navigation-compose:2.7.0")  // Navigation (선택)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation(libs.androidx.core)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.material3.android)
    implementation(libs.androidx.runtime.livedata)
//    implementation(project(":app"))  // Coroutines Android

    // Android Test 및 Debug
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation("com.google.android.gms:play-services-wearable:18.0.0")  // Data Layer 통신
    implementation("androidx.compose.runtime:runtime")
    // Retrofit 및 JSON 파싱 관련 의존성
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("androidx.wear:wear:1.2.0")
    implementation("androidx.wear.compose:compose-material:1.1.0")
    implementation("androidx.compose.ui:ui:1.4.0")

}
