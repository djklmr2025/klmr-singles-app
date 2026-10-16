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
        versionCode = (System.getenv("VERSION_CODE") ?: "3").toInt()
        versionName = System.getenv("VERSION_NAME") ?: "1.2"
    }
    signingConfigs {
        create("release") {
            val envPath = System.getenv("KS_PATH")
            val localV2 = file("C:/ARKAIOS/klmr-singles-keystore/klmr-v2.p12")
            if (envPath != null) {
                storeFile = file(envPath)
                storeType = "pkcs12"
                storePassword = System.getenv("KS_PASS")
                keyAlias = "klmr"
                keyPassword = System.getenv("KS_PASS")
            } else if (localV2.exists()) {
                storeFile = localV2
                storeType = "pkcs12"
                storePassword = "EDmLWbQiVa782pwP4kj3"
                keyAlias = "klmr"
                keyPassword = "EDmLWbQiVa782pwP4kj3"
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
