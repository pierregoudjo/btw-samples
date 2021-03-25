plugins {
    kotlin("jvm")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable-jvm:0.3.3")
    implementation(platform("dev.forkhandles:forkhandles-bom:1.8.5.0"))
    implementation("dev.forkhandles:tuples4k")

    testImplementation ("io.kotest:kotest-runner-junit5:4.4.3")
    testImplementation ("io.kotest:kotest-assertions-core:4.4.3")
    testImplementation ("io.kotest:kotest-property:4.4.3")
}

val compileKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by tasks
compileKotlin.kotlinOptions {
    freeCompilerArgs = listOf("-Xinline-classes")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<Test> {
    useJUnitPlatform()
}
