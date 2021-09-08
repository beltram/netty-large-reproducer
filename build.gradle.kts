buildscript {
    repositories {
        mavenCentral()
    }
}

plugins {
    kotlin("jvm") version "1.5.30"
}

group = "com.spring.reproducer"
version = "0.0.1"

repositories { mavenCentral() }

val kotlinVersion = "1.5.30"
val reactorVersion = "2020.0.10"
val junitVersion = "5.7.2"
val assertjVersion = "3.20.2"

dependencies {
    api(platform("org.jetbrains.kotlin:kotlin-bom:$kotlinVersion"))
    api(platform("io.projectreactor:reactor-bom:$reactorVersion"))
    api(platform("org.junit:junit-bom:$junitVersion"))
    testImplementation("io.projectreactor.netty:reactor-netty-core:1.0.10")
    testImplementation("io.projectreactor:reactor-core:3.4.9")
    testImplementation("io.projectreactor:reactor-test:3.4.9")
    testImplementation("io.projectreactor.kotlin:reactor-kotlin-extensions:1.1.4")
    testImplementation("org.assertj:assertj-core:$assertjVersion")
    testImplementation("io.projectreactor:reactor-test:3.4.9")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.2")
    testImplementation("org.slf4j:slf4j-api:1.7.32")
    testImplementation("ch.qos.logback:logback-core:1.2.5")
    testImplementation("ch.qos.logback:logback-classic:1.2.5")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.2")
}

tasks.withType<Test> { useJUnitPlatform() }