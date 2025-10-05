# Spring Redis Demo

A Spring Boot application demonstrating caching with Redis, including Testcontainers-based integration tests.

## ✅ 1. Prerequisites

Make sure one of the following is installed and running on your machine:
	- Docker Desktop (Windows / macOS)
👉 https://www.docker.com/products/docker-desktop/
	- Docker Engine (Linux)
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
```bash
src/
├── main/java/…/configuration   # RedisConfig
├── main/java/…/model          # User.java
├── main/java/…/service        # UserService.java
└── test/java/…/service        # UserServiceTest.java (Testcontainers)
```

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
```

## 🧪 Running Tests (Redis via Testcontainers)
```bash
./gradlew test
```

## 🚀 Running the App

Make sure Redis is running locally (or rely on defaults from Testcontainers).

```bash
./gradlew bootRun
```

## Sample build
```bash
➜ gradleBuild.sh build
Running Gradle task: build...

> Task :test

    User Service Redis Test... STANDARD_OUT
    00:50:25.347 [Test worker] INFO org.testcontainers.images.PullPolicy -- Image pull policy will be performed by: DefaultPullPolicy()
    00:50:25.350 [Test worker] INFO org.testcontainers.utility.ImageNameSubstitutor -- Image name substitution will be performed by: DefaultImageNameSubstitutor (composite of 'ConfigurationFileImageNameSubstitutor' and 'PrefixingImageNameSubstitutor')
    00:50:25.354 [Test worker] INFO org.testcontainers.DockerClientFactory -- Testcontainers version: 1.21.3
    00:50:25.462 [Test worker] INFO org.testcontainers.dockerclient.DockerClientProviderStrategy -- Loaded org.testcontainers.dockerclient.UnixSocketClientProviderStrategy from ~/.testcontainers.properties, will try it first
    00:50:25.571 [Test worker] INFO org.testcontainers.dockerclient.DockerClientProviderStrategy -- Found Docker environment with local Unix socket (unix:///var/run/docker.sock)
    00:50:25.572 [Test worker] INFO org.testcontainers.DockerClientFactory -- Docker host IP address is localhost
    00:50:25.578 [Test worker] INFO org.testcontainers.DockerClientFactory -- Connected to docker: 
      Server Version: 28.4.0
      API Version: 1.51
      Operating System: Docker Desktop
      Total Memory: 7837 MB
      Labels: 
        com.docker.desktop.address=unix:///Users/t827056/Library/Containers/com.docker.docker/Data/docker-cli.sock
    00:50:25.590 [Test worker] INFO tc.testcontainers/ryuk:0.12.0 -- Creating container for image: testcontainers/ryuk:0.12.0
    00:50:27.872 [Test worker] INFO tc.testcontainers/ryuk:0.12.0 -- Container testcontainers/ryuk:0.12.0 is starting: aef7fb3de263260a82269ed7cf498352c9f1a2c8e818c48f179723b4ac7bad29
    00:50:28.068 [Test worker] INFO tc.testcontainers/ryuk:0.12.0 -- Container testcontainers/ryuk:0.12.0 started in PT2.478743S
    00:50:28.072 [Test worker] INFO org.testcontainers.utility.RyukResourceReaper -- Ryuk started - will monitor and terminate Testcontainers containers on JVM exit
    00:50:28.072 [Test worker] INFO org.testcontainers.DockerClientFactory -- Checking the system...
    00:50:28.072 [Test worker] INFO org.testcontainers.DockerClientFactory -- ✔︎ Docker server version should be at least 1.6.0
    00:50:28.073 [Test worker] INFO tc.redis:7.0.11-alpine -- Creating container for image: redis:7.0.11-alpine
    00:50:28.094 [Test worker] INFO tc.redis:7.0.11-alpine -- Container redis:7.0.11-alpine is starting: 20d7278ed40f8bfd6a5f15ab52bd63d3ea1fff0d9e4b5983eac018c6d5498bad
    00:50:28.218 [Test worker] INFO tc.redis:7.0.11-alpine -- Container redis:7.0.11-alpine started in PT0.145857S
    00:50:28.270 [Test worker] INFO org.springframework.test.context.support.AnnotationConfigContextLoaderUtils -- Could not detect default configuration classes for test class [com.example.demo.service.UserServiceTest]: UserServiceTest does not declare any static, non-private, non-final, nested classes annotated with @Configuration.
    00:50:28.324 [Test worker] INFO org.springframework.boot.test.context.SpringBootTestContextBootstrapper -- Found @SpringBootConfiguration com.example.demo.SpringRedisDemoApplication for test class com.example.demo.service.UserServiceTest

      .   ____          _            __ _ _
     /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
    ( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
     \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
      '  |____| .__|_| |_|_| |_\__, | / / / /
     =========|_|==============|___/=/_/_/_/

     :: Spring Boot ::                (v3.3.4)

    2025-10-06T00:50:28.490+13:00  INFO 85472 --- [spring-redis-example] [    Test worker] c.example.demo.service.UserServiceTest   : Starting UserServiceTest using Java 21.0.3 with PID 85472 (started by t827056 in /Users/t827056/personal-workspace/spring-boot-redis-demo)
    2025-10-06T00:50:28.491+13:00 DEBUG 85472 --- [spring-redis-example] [    Test worker] c.example.demo.service.UserServiceTest   : Running with Spring Boot v3.3.4, Spring v6.1.13
    2025-10-06T00:50:28.491+13:00  INFO 85472 --- [spring-redis-example] [    Test worker] c.example.demo.service.UserServiceTest   : No active profile set, falling back to 1 default profile: "default"
    2025-10-06T00:50:28.744+13:00  INFO 85472 --- [spring-redis-example] [    Test worker] .s.d.r.c.RepositoryConfigurationDelegate : Multiple Spring Data modules found, entering strict repository configuration mode
    2025-10-06T00:50:28.745+13:00  INFO 85472 --- [spring-redis-example] [    Test worker] .s.d.r.c.RepositoryConfigurationDelegate : Bootstrapping Spring Data Redis repositories in DEFAULT mode.
    2025-10-06T00:50:28.760+13:00  INFO 85472 --- [spring-redis-example] [    Test worker] .s.d.r.c.RepositoryConfigurationDelegate : Finished Spring Data repository scanning in 6 ms. Found 0 Redis repository interfaces.
    2025-10-06T00:50:29.313+13:00  INFO 85472 --- [spring-redis-example] [    Test worker] c.example.demo.service.UserServiceTest   : Started UserServiceTest in 0.932 seconds (process running for 4.365)

User Service Redis Test... > Test addUser stores user and caches it STARTED

Java HotSpot(TM) 64-Bit Server VM warning: Sharing is only supported for boot loader classes because bootstrap classpath has been appended

> Task :test

User Service Redis Test... > Test addUser stores user and caches it PASSED

User Service Redis Test... > Test getAllUsers returns immutable map. STARTED

User Service Redis Test... > Test getAllUsers returns immutable map. PASSED

User Service Redis Test... > Test updateUser updates cache STARTED

User Service Redis Test... > Test updateUser updates cache PASSED

User Service Redis Test... > Test fetching from cache got the same user having same value. STARTED

User Service Redis Test... > Test fetching from cache got the same user having same value. STANDARD_OUT
    Fetching from DB: 1

User Service Redis Test... > Test fetching from cache got the same user having same value. PASSED

User Service Redis Test... > Test measuring the first normal fetch user from database and second fetch user from cache. STARTED

User Service Redis Test... > Test measuring the first normal fetch user from database and second fetch user from cache. STANDARD_OUT
    Fetching from DB: 1

User Service Redis Test... > Test measuring the first normal fetch user from database and second fetch user from cache. PASSED

User Service Redis Test... > Test removeUser evicts cache STARTED

User Service Redis Test... > Test removeUser evicts cache STANDARD_OUT
    Fetching from DB: 4

User Service Redis Test... > Test removeUser evicts cache PASSED

PositiveDurationValidatorUnitTest > Valid feature flag service cache ttl should return true. STARTED

PositiveDurationValidatorUnitTest > Valid feature flag service cache ttl should return true. PASSED

PositiveDurationValidatorUnitTest > Test invalid cache duration configurations, especially zero and negative duration. > 1 -> ZERO duration. STARTED

PositiveDurationValidatorUnitTest > Test invalid cache duration configurations, especially zero and negative duration. > 1 -> ZERO duration. PASSED

PositiveDurationValidatorUnitTest > Test invalid cache duration configurations, especially zero and negative duration. > 2 -> Prefix negative duration. STARTED

PositiveDurationValidatorUnitTest > Test invalid cache duration configurations, especially zero and negative duration. > 2 -> Prefix negative duration. PASSED

PositiveDurationValidatorUnitTest > Test invalid cache duration configurations, especially zero and negative duration. > 3 -> Negative number duration. STARTED

PositiveDurationValidatorUnitTest > Test invalid cache duration configurations, especially zero and negative duration. > 3 -> Negative number duration. PASSED

BUILD SUCCESSFUL in 12s
8 actionable tasks: 6 executed, 2 up-to-date
```
