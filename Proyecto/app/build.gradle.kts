import org.jetbrains.dokka.gradle.DokkaTask
import java.net.URL

plugins {
    alias(libs.plugins.android.application)
    id("androidx.navigation.safeargs")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.dokka") version "1.9.0"
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
            //poner localhost para movil fisico 10.0.2.2
            buildConfigField("String", "BASE_URL", "\"https://10.0.2.2:8443/\"")
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
    implementation("com.google.android.material:material:1.12.0")
    implementation (libs.glide)
    implementation(libs.firebase.inappmessaging)
    implementation(libs.cardview)
    annotationProcessor (libs.compiler)
    implementation(libs.logging.interceptor)
    implementation(libs.appcompat)
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


tasks.named<DokkaTask>("dokkaJavadoc") {
    // Dokka ya sabe que "dokkaJavadoc" genera output en formato javadoc,
    // así que NO hace falta outputFormat.set("javadoc").

    // Le indicamos sólo el directorio donde volcará los .html:
    outputDirectory.set(buildDir.resolve("docs/javadoc"))

    dokkaSourceSets {
        named("main") {
            // Nombre del módulo/documentación (opcional):
            moduleName.set("API de FacilGimApp")

            // Raíces de código (tu código está en src/main/java y en src/main/kotlin):
            sourceRoots.from(file("src/main/java"))
            sourceRoots.from(file("src/main/kotlin"))

            // Si quieres enlazar a la doc oficial de Android:
            externalDocumentationLink {
                url.set(URL("https://developer.android.com/reference/"))
                // packageListUrl se rellena automáticamente en Dokka 1.9.x,
                // así que normalmente NO hace falta especificarlo a mano.
            }

            // Por ejemplo, sólo documentar clases públicas:
            documentedVisibilities.set(
                setOf(org.jetbrains.dokka.DokkaConfiguration.Visibility.PUBLIC)
            )
        }
    }
}