# Spring Redis Example

A Spring Boot application demonstrating caching with Redis, including Testcontainers-based integration tests.

## ✅ Features

- Spring Boot (Java 21 compatible)
- Redis cache with `LettuceConnectionFactory`
- `@Cacheable`, `@CachePut`, and `@CacheEvict`
- Custom TTL and serialization
- Testcontainers for Redis integration tests
- JUnit 5 + AssertJ
- Gradle build

## 📦 Project Structure
src/
├── main/java/…/configuration   # RedisConfig
├── main/java/…/model          # User.java
├── main/java/…/service        # UserService.java
└── test/java/…/service        # UserServiceTest.java (Testcontainers)

## ⚙️ Dependencies (build.gradle)

Key libraries:
- `spring-boot-starter-data-redis`
- `spring-boot-starter-cache`
- `spring-boot-starter-web`
- `spring-boot-starter-validation`
- `jackson-datatype-jsr310`
- `org.testcontainers:testcontainers`
- `com.redis:testcontainers-redis`
- `spring-boot-starter-test`

## 🔨 Build Project

Run this to build the project, compile tests, and resolve dependencies:

```bash
./gradlew clean build


## 🧪 Running Tests (Redis via Testcontainers)
```bash
./gradlew test

## 🚀 Running the App

Make sure Redis is running locally (or rely on defaults from Testcontainers).

```bash
./gradlew bootRun

