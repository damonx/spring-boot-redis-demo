# ===== Stage 1: Build the Spring Boot application with AOT =====
FROM eclipse-temurin:21-jdk-jammy AS builder

WORKDIR /app

# Copy Gradle files first (layer caching)
COPY build.gradle settings.gradle gradlew ./
COPY gradle gradle
RUN ./gradlew dependencies --no-daemon || true

# Copy source
COPY src src

# Build application with AOT enabled
RUN ./gradlew bootJar -PspringAot=true --no-daemon

# ===== Stage 2: Runtime image with CDS =====
FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

# Generate CDS archive
RUN java -Xshare:dump -XX:SharedArchiveFile=/app/app-cds.jsa -jar app.jar || true

# Use CDS when starting
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-Xshare:on", "-XX:SharedArchiveFile=/app/app-cds.jsa", "-jar", "/app/app.jar"]

EXPOSE 8080