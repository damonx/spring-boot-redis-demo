plugins {
    id("org.springframework.boot") version "3.3.4"
    id("io.spring.dependency-management") version "1.1.6"
    java
}

group = "nz.co.damonx"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("org.springframework.boot:spring-boot-starter-web")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:testcontainers:1.21.3")
    testImplementation("com.redis:testcontainers-redis:2.2.4")
    testImplementation("org.testcontainers:junit-jupiter:1.21.3")
    testImplementation("org.assertj:assertj-core:3.27.6")
    testImplementation("org.junit.jupiter:junit-jupiter:6.0.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webflux")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.10.0")
}

tasks.test {
    useJUnitPlatform()
    reports {
        junitXml.required.set(true)
        html.required.set(true)
    }
    testLogging {
        events("started", "passed", "skipped", "failed")

        showStandardStreams = true
        showCauses = true
        showStackTraces = true

        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
    jvmArgs("-XX:+EnableDynamicAgentLoading")
}

tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar> {
    enabled = true
}