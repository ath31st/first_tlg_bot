plugins {
    id("org.jetbrains.kotlin.jvm") version "1.9.0"
    id("application")
    id("com.github.johnrengelman.shadow") version "7.1.1"
}

group = "org.example.botfarm"
version = "1.0-SNAPSHOT"
description = "firsttlgbot"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.telegram:telegrambots:6.7.0")
    implementation("org.telegram:telegrambotsextensions:6.0.1")
    implementation("com.fasterxml.jackson.core:jackson-core:2.13.2")
    implementation("org.jsoup:jsoup:1.16.1")
    implementation("org.slf4j:slf4j-api:2.0.7")
    implementation("org.slf4j:slf4j-simple:2.0.7")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testImplementation(kotlin("test"))
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("MainKt")
}

tasks.jar {
    manifest.attributes["Main-Class"] = "org.example.botfarm.App"
}
