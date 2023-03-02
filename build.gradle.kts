plugins {
    id("convention.detekt")
}

buildscript {

    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath(libs.androidGradle)
        classpath(libs.kotlinGradle)
        classpath(libs.hiltGradle)
        classpath(libs.googleServicesGradle)
        classpath(libs.kspGradle)
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}
