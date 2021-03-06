import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    `java-library`
}

group = "xyz.goudjo.btw-samples"
version = "1.0-SNAPSHOT"

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable-jvm:0.3.3")

    testImplementation("org.spekframework.spek2:spek-dsl-jvm:2.0.15")
    testRuntimeOnly("org.spekframework.spek2:spek-runner-junit5:2.0.15")
    testImplementation ("io.kotest:kotest-assertions-core:4.4.3")
    testRuntimeOnly("org.jetbrains.kotlin:kotlin-reflect:1.4.30")
}

tasks {
    test {
        useJUnitPlatform {
            includeEngines("spek2")
        }
    }
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
