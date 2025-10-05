# Spring Redis Example

A Spring Boot application demonstrating caching with Redis, including Testcontainers-based integration tests.

## ✅ 1. Prerequisites

Make sure one of the following is installed and running on your machine:
	•	Docker Desktop (Windows / macOS)
👉 https://www.docker.com/products/docker-desktop/
	•	Docker Engine (Linux)
👉 https://docs.docker.com/engine/install/

## ✅ 2. How Redis is started

You do not need to run Redis manually.
The project uses the following Testcontainers setup (example):

```java
@Container
static RedisContainer redis = new RedisContainer("redis:7.0.11-alpine");

@DynamicPropertySource
static void redisProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.data.redis.host", redis::getHost);
    registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
}
```

When integration tests or the Spring Boot app starts, Testcontainers:
	1.	Pulls the Redis Docker image (if not already available locally).
	2.	Runs it in a container.
	3.	Injects host and port into Spring config.



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
