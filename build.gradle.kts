import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version KotlinVersion.CURRENT.toString()
    
    id("org.jetbrains.compose") version "1.8.2"
    id("org.jetbrains.kotlin.plugin.compose")
}

group = "com.hermanest"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
    implementation(compose.materialIconsExtended)
    
    implementation("org.openpnp:opencv:4.9.0-0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")
    
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation(kotlin("test"))
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "kotlin-opencv"
            packageVersion = "1.0.0"
        }
    }
}
