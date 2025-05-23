plugins {
    alias(libs.plugins.android.application)
    id("androidx.navigation.safeargs")
    id("org.jetbrains.kotlin.android")
}

android {

    configurations.all {
        exclude(group = "com.android.support", module = "support-compat")
    }

    namespace = "com.proyecto.facilgimapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.proyecto.facilgimapp"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
        buildConfig = true
    }

    buildTypes {
        debug {
            buildConfigField("String", "BASE_URL", "\"https://10.110.4.43:8443/\"")
        }
        release {
            isMinifyEnabled = false
            //Cambiar cuando llegue a release:
            buildConfigField ("String", "BASE_URL", "\"https://api.tu-dominio.com/\"")

        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation("com.github.prolificinteractive:material-calendarview:2.0.0")
    implementation("com.google.android.material:material:1.7.0")
    implementation (libs.glide)
    annotationProcessor (libs.compiler)
    implementation(libs.logging.interceptor)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.picasso)
    implementation(libs.kotlinx.coroutines.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}