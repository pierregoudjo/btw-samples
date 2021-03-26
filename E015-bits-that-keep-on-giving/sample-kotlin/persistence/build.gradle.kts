import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    `java-library`
    kotlin("plugin.serialization") version "1.4.30"
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":ddd-cqrs-event-sourcing"))

    testImplementation ("io.kotest:kotest-runner-junit5:4.4.3")
    testImplementation ("io.kotest:kotest-assertions-core:4.4.3")
    testImplementation ("io.kotest:kotest-property:4.4.3")
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    freeCompilerArgs = listOf("-Xinline-classes")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<Test> {
    useJUnitPlatform()
}
