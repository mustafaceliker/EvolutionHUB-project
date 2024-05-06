plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")

}

android {
    namespace = "com.example.evolutionhub"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.evolutionhub"
        minSdk = 34
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        kotlinCompilerExtensionVersion = "1.4.3"
    }

    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.compose.ui:ui:1.1.0")  // Version added
    implementation("androidx.compose.ui:ui-graphics:1.1.0")  // Version added
    implementation("androidx.compose.ui:ui-tooling-preview:1.1.0")  // Version added
    implementation("androidx.compose.material3:material3:1.1.0")  // Version added
    implementation("com.google.firebase:firebase-auth:22.3.0")
    implementation("com.google.firebase:firebase-database:20.3.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.4")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.4")
    implementation("com.google.firebase:firebase-firestore:24.10.0")
    implementation("com.google.firebase:firebase-messaging:23.4.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.1.0")  // Version added
    debugImplementation("androidx.compose.ui:ui-tooling:1.1.0")  // Version added
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.1.0")  // Version added
    implementation("com.google.firebase:firebase-bom:32.7.0")
    implementation ("com.google.firebase:firebase-messaging:23.0.0")
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")
}
