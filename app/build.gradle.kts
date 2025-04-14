import com.android.build.api.dsl.VariantDimension
import com.example.shared.encrypt
import java.io.FileInputStream
import java.util.Properties


plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.kapt)
    alias(libs.plugins.safeArgs)
    id("kotlin-parcelize")
}

android {
    signingConfigs {
        create("release") {
            val keystoreProperties = Properties().apply {
                load(FileInputStream(rootProject.file("keystore.properties")))
            }
            storeFile = file(keystoreProperties["storeFile"] as String)
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
            storePassword = keystoreProperties["storePassword"] as String
        }
    }
    namespace = "com.example.hotmovies"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.hotmovies"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val tmdbProperties = Properties().apply {
            load(FileInputStream(rootProject.file("tmdb.properties")))
        }
        buildConfigField("TMDB_API_KEY", (tmdbProperties["apiKey"] as String).encrypt(12))
        buildConfigField("TMDB_BEARER", (tmdbProperties["bearer"] as String).encrypt(45))
        buildConfigField("TMDB_ACCOUNT_ID", (tmdbProperties["accountId"] as String).encrypt(32))
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("debug")
        }
        release {
            isMinifyEnabled = true
            isDebuggable = false
            isJniDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
                "retrofit2.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
        buildConfig = true
    }
}

tasks.withType<Test> {
    jvmArgs = listOf("-XX:+EnableDynamicAgentLoading")
    systemProperty("isTest", true)
}

inline fun <reified ValueT> VariantDimension.buildConfigField(name: String, value: ValueT) {
    val resolvedValue = when (value) {
        is String -> "\"$value\""
        is Int -> "$value"
        is Boolean -> "$value"
        else -> value.toString()
    }
    buildConfigField(ValueT::class.java.simpleName, name, resolvedValue)
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.datastore)
    implementation(libs.androidx.paging)
    implementation(libs.lottie)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.retrofit.logging.inteceptor)
    implementation(libs.glide)
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.androidx.fragment.ktx)
    kapt(libs.glide.processor)

    testImplementation(libs.mockk)
    testImplementation(libs.coroutine.tests)
    testImplementation(libs.junit)
}