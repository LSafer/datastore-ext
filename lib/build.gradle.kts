@file:Suppress("UnstableApiUsage")

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("maven-publish")
    id("kotlinx-serialization")
}

android {
    compileSdk = 32

    defaultConfig {
        minSdk = 21
        targetSdk = 32

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.3.0"
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/LICENSE.md"
            excludes += "/META-INF/LICENSE-notice.md"
            excludes += "/META-INF/INDEX.LIST"
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/groovy-release-info.properties"
            excludes += "/META-INF/groovy/org.codehaus.groovy.runtime.ExtensionModule"
        }
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.7.10")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.7.10")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0")

    implementation("androidx.compose.foundation:foundation:1.2.1")

    implementation("androidx.datastore:datastore:1.0.0")
    implementation("androidx.datastore:datastore-core:1.0.0")
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("androidx.datastore:datastore-preferences-core:1.0.0")

    testImplementation(kotlin("test"))
}
