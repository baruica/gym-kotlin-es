plugins {
    kotlin("jvm") version "2.4.0"
    id("io.kotest") version "6.2.2"
    id("com.autonomousapps.dependency-analysis") version "3.17.0"
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

group = "me.baruica"
version = "1.0"
description = "The Gym"

repositories {
    mavenCentral()
}

val kotestVersion = "6.2.2"

dependencies {
    implementation("jp.kukv:kULID:2.0.0.1")
    testRuntimeOnly("io.kotest:kotest-runner-junit5-jvm:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core-jvm:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-shared:6.2.2")
    testImplementation("io.kotest:kotest-common:6.2.2")
    testImplementation("io.kotest:kotest-framework-api:6.2.2")
}
