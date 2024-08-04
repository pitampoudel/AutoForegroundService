plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components.findByName("release"))
            artifact("$buildDir/outputs/aar/${project.name}-release.aar") {
                extension = "aar"
            }
        }
    }
}
tasks.named("publishMavenPublicationToMavenLocal") {
    mustRunAfter(tasks.named("bundleReleaseAar"))
}

android {
    namespace = "pitam.autoforegroundservice"

    compileSdk = 34

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

    //Timber
    api("com.jakewharton.timber:timber:5.0.1")

    //LifecycleService
    api("androidx.lifecycle:lifecycle-service:2.8.4")
}