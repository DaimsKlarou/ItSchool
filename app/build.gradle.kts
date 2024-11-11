plugins {
    alias(libs.plugins.kotlin.android)
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.itschool"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.itschool"
        minSdk = 24
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.annotation)

    // Firebase BOM for version management of Firebase dependencies
    implementation(platform("com.google.firebase:firebase-bom:33.4.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-database-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-messaging")

    implementation("com.google.android.gms:play-services-auth:20.4.0")
    implementation("io.getstream:stream-webrtc-android:1.1.1")

    implementation("com.hbb20:ccp:2.5.0")
    implementation("com.google.android.material:material:1.9.0")
    implementation("com.firebaseui:firebase-ui-firestore:8.0.2")
    implementation("com.github.bumptech.glide:glide:4.15.1")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.github.dhaval2404:imagepicker:2.1")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation(libs.androidx.lifecycle.process)

    // AndroidX testing dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation("com.google.android.gms:play-services-location:21.0.1")

//    //dependances externes pour ameliorer le rendu de mon code
//    implementation("com.squareup.picasso:picasso:2.71828")
//    implementation("androidx.cardview:cardview:1.0.0")
//    implementation("com.rengwuxian.materialedittext:library:2.1.4")
//    implementation("com.squareup.retrofit2:retrofit:2.3.0")
//    implementation("com.squareup.retrofit2:converter-gson:2.3.0")
}
