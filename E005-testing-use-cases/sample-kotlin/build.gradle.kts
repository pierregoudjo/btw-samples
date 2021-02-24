import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.30"
    application
}

group = "xyz.goudjo.btw-samples"
version = "1.0-SNAPSHOT"

repositories {
    jcenter()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable-jvm:0.3.3")
    testImplementation("io.cucumber:cucumber-java8:6.8.1")
    testImplementation("io.cucumber:cucumber-junit:6.8.1")
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClassName = "MainKt"
}