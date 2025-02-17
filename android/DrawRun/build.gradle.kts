// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    kotlin("jvm") version "1.9.10" apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    // Hilt 의존성 주입 플러그인
    alias(libs.plugins.hilt) apply false
    // Kotlin Annotation Processing Tool 플러그인
    alias(libs.plugins.kotlin.kapt) apply false
}

// 빌드 스크립트 설정
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.google.gms:google-services:4.4.0")
    }
}

// 프로젝트 정리를 위한 사용자 정의 태스크 등록
tasks.register<Delete>("clean") {
    delete(layout.buildDirectory)  // 루트 프로젝트의 build 디렉토리 삭제
}