plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}
android {
    namespace = "com.klmr.singles"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.klmr.singles"
        minSdk = 21
        targetSdk = 34
        versionCode = (System.getenv("VERSION_CODE") ?: "1").toInt()
        versionName = System.getenv("VERSION_NAME") ?: "1.0"
    }
    signingConfigs {
        create("release") {
            val p = System.getenv("KS_PATH")
            if (p != null) {
                storeFile = file(p)
                storeType = "pkcs12"
                storePassword = System.getenv("KS_PASS")
                keyAlias = "klmr"
                keyPassword = System.getenv("KS_PASS")
            }
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
    lint {
        checkReleaseBuilds = false
        abortOnError = false
    }
}
