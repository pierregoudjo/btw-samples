import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    application
}

group = "xyz.goudjo.btw-samples"
version = "1.0-SNAPSHOT"

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClassName = "MainKt"
}

val run: JavaExec by tasks

run.standardInput = System.`in`
