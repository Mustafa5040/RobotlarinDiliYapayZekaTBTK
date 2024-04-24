plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "com.gal.tubitakrobotapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.gal.tubitakrobotapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    packagingOptions{
        exclude ("META-INF/INDEX.LIST")
        exclude ("META-INF/DEPENDENCIES")
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.gridlayout)
    testImplementation(libs.junit)
    implementation ("androidx.datastore:datastore-preferences:1.0.0")
    // optional - RxJava2 support
    //implementation("androidx.datastore:datastore-preferences-rxjava2:1.0.0")
    // optional - RxJava3 support
    //implementation ("androidx.datastore:datastore-preferences-rxjava3:1.0.0")
    implementation ("com.google.cloud:google-cloud-speech:1.29.1")
    implementation ("com.google.auth:google-auth-library-oauth2-http:0.26.0")
    implementation ("io.grpc:grpc-okhttp:1.38.0")
    implementation ("io.grpc:grpc-stub:1.38.1")
    implementation ("com.google.api:gax:1.58.0")
    implementation ("com.android.volley:volley:1.2.1")
    implementation ("com.jakewharton:process-phoenix:2.0.0")
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

}