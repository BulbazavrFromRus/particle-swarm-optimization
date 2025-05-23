plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.diplomawork2"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.diplomawork2"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        viewBinding = true
    }

    packaging {
        resources {
            pickFirsts.addAll(listOf("META-INF/LICENSE.md", "META-INF/LICENSE-notice.md"))
        }
    }
}

dependencies {
    // Основные библиотеки
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation("com.airbnb.android:lottie:5.2.0")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation(libs.androidx.monitor)

    // Юнит-тесты
    testImplementation(libs.junit)

    // Инструментальные тесты
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)


    //Espresso
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation ("androidx.test.ext:junit:1.1.5")
    androidTestImplementation ("androidx.test:rules:1.5.0")
    androidTestImplementation ("androidx.test.espresso:espresso-intents:3.5.1")


}
