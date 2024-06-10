plugins {
    kotlin("jvm") version "2.0.0"
    id("io.kotest") version "0.4.11"
    id("com.autonomousapps.dependency-analysis") version "1.32.0"
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

val kotestVersion = "5.9.1"

dependencies {
    implementation("jp.kukv:kULID:2.0.0.1")
    testRuntimeOnly("io.kotest:kotest-runner-junit5-jvm:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core-jvm:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-shared:5.9.0")
    testImplementation("io.kotest:kotest-common:5.9.1")
    testImplementation("io.kotest:kotest-framework-api:5.9.0")
}
