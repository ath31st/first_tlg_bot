plugins {
    id("java-library")
    id("maven-publish")

    id("org.jetbrains.kotlin.jvm") version "1.9.0"
    id("application")
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
    implementation("org.telegram:telegrambots:6.0.1")
    implementation("org.telegram:telegrambotsextensions:6.0.1")
    implementation("log4j:log4j:1.2.17")
    implementation("org.slf4j:slf4j-api:1.7.36")
    implementation("org.slf4j:slf4j-log4j12:1.7.36")
    implementation("com.fasterxml.jackson.core:jackson-core:2.13.2")
    implementation("org.jsoup:jsoup:1.15.3")
    testImplementation("junit:junit:4.13.2")

    implementation("org.projectlombok:lombok:1.18.28")
    annotationProcessor("org.projectlombok:lombok:1.18.28")

    testImplementation("org.projectlombok:lombok:1.18.28")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.28")

    testImplementation(kotlin("test"))
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile>().configureEach {
    jvmTargetValidationMode.set(org.jetbrains.kotlin.gradle.dsl.jvm.JvmTargetValidationMode.WARNING)
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