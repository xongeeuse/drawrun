plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.kapt)
//    alias(libs.plugins.compose.compiler)
    id("kotlin-parcelize")
}

android {
    namespace = "com.example.drawrun"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.drawrun"
        minSdk = 26
        targetSdk = 34
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
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        viewBinding = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"  // 컴파일러 버전 명시
    }

}

kapt {
    arguments {
        arg("dagger.hilt.android.internal.disableAndroidSuperclassValidation", "true")
    }
}


dependencies {
    implementation(kotlin("stdlib"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // 위치 서비스
    implementation(libs.gms.location)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Lifecycle components
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.runtime.ktx)

    // Navigation component
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)

    // Room database
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.runtime.android)
    implementation(libs.androidx.runtime.android)
    implementation(libs.androidx.lifecycle.livedata.core)
    implementation(libs.androidx.ui.graphics.android)
    implementation(libs.play.services.maps)
    kapt(libs.room.compiler)

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)

    // OkHttp
    implementation(libs.okhttp)

    // Gson
    implementation(libs.gson)

    // Mapbox Navigation Core (모든 기능 포함)
    implementation(libs.mapbox.navigationcore.android)




    // Mapbox Maps SDK (필수)
    implementation(libs.mapbox.maps)
//    implementation(libs.mapbox.search)
//    implementation(libs.mapbox.search.android) {
//        exclude(group = "com.mapbox.navigationcore")
//    }

    // 선택적: UI 컴포넌트가 필요한 경우
    implementation(libs.mapbox.navigationcore.ui.maps) {
        exclude(group = "com.mapbox.navigationcore", module = "android")
    }
    implementation(libs.mapbox.navigationcore.ui.components) {
        exclude(group = "com.mapbox.navigationcore", module = "android")
    }

    // Wear OS 관련 의존성
    implementation(libs.play.services.wearable)
    implementation(libs.wear)
    implementation(libs.wear.remote.interactions)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // bom 의존성 추가
    implementation(platform(libs.androidx.compose.bom))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material:material")
    implementation("androidx.compose.ui:ui-tooling-preview")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")

    implementation("com.github.bumptech.glide:glide:4.14.2")
    kapt ("com.github.bumptech.glide:compiler:4.14.2")
    implementation("com.google.android.gms:play-services-wearable:18.0.0")
    implementation("androidx.wear:wear:1.2.0")

    // slide
    implementation("androidx.viewpager2:viewpager2:1.0.0")
}