plugins {
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.serialization") version "2.1.0"
}

group = "dev.mmauro"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")

    val cliktVersion = "5.0.3"
    implementation("com.github.ajalt.clikt:clikt:$cliktVersion")
    implementation("com.github.ajalt.clikt:clikt-markdown:$cliktVersion")

    val mordantVersion = "3.0.2"
    implementation("com.github.ajalt.mordant:mordant:$mordantVersion")
    implementation("com.github.ajalt.mordant:mordant-markdown:$mordantVersion")

    val kotestVersion = "5.9.1"
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("io.kotest:kotest-framework-datatest:$kotestVersion")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(8)
}

tasks.withType<Jar> {
    manifest {
        attributes(
            mapOf(
                "Main-Class" to "dev.mmauro.malverio.MainKt",
            )
        )
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    val contents = configurations.runtimeClasspath.get()
        .map { if (it.isDirectory) it else zipTree(it) } + sourceSets.main.get().output
    from(contents)
}