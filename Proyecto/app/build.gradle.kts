import org.jetbrains.dokka.gradle.DokkaTask
import java.net.URL
import java.util.Properties
import java.io.FileInputStream


plugins {
    alias(libs.plugins.android.application)
    id("androidx.navigation.safeargs")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.dokka") version "1.9.0"
}

android {
//Cargamos las claves desde la raiz del proyecto
    val keystorePropertiesFile = rootProject.file("keystore.properties")
    val keystoreProperties = Properties().apply {
        load(FileInputStream(keystorePropertiesFile))
    }
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

    // Configuramos signingConfigs usando las props cargadas
        signingConfigs {
            create("release") {
                // El valor de STORE_FILE en keystore.properties, por ejemplo: keystore/facilgim-release.jks
                storeFile = file(keystoreProperties["STORE_FILE"] as String)
                storePassword = keystoreProperties["STORE_PASSWORD"] as String
                keyAlias = keystoreProperties["KEY_ALIAS"] as String
                keyPassword = keystoreProperties["KEY_PASSWORD"] as String
            }
        }

        buildTypes {
            getByName("debug") {
                // Para debug seguimos apuntando al local:
                buildConfigField("String", "BASE_URL", "\"https://10.0.2.2:8443/\"")
            }
            getByName("release") {
                isMinifyEnabled = false

                // Asocia la signingConfig que hemos creado arriba con nombre "release"
                signingConfig = signingConfigs.getByName("release")

                // URL para producción:
                buildConfigField("String", "BASE_URL", "\"https://91.99.49.236.sslip.io/\"")
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
        implementation(libs.glide)
        implementation(libs.firebase.inappmessaging)
        implementation(libs.cardview)
        annotationProcessor(libs.compiler)
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

//Generamos el javadoc
    tasks.named<DokkaTask>("dokkaJavadoc") {

        // Le indicamos  el directorio donde volcará los .html:
        outputDirectory.set(buildDir.resolve("docs/javadoc"))

        dokkaSourceSets {
            named("main") {
                // Nombre del módulo/documentación:
                moduleName.set("API de FacilGimApp")

                // Raíces de código:
                sourceRoots.from(file("src/main/java"))
                sourceRoots.from(file("src/main/kotlin"))

                // Si se quiere enlazar a la doc oficial de Android:
                externalDocumentationLink {
                    url.set(URL("https://developer.android.com/reference/"))
                }

                // sólo documentar clases públicas:
                documentedVisibilities.set(
                    setOf(org.jetbrains.dokka.DokkaConfiguration.Visibility.PUBLIC)
                )
            }
        }
    }
