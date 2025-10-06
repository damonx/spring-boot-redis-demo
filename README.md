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
    src
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── example
    │   │           └── demo
    │   │               ├── configuration
    │   │               ├── controller
    │   │               ├── exception
    │   │               ├── model
    │   │               ├── properties
    │   │               ├── scheduler
    │   │               ├── service
    │   │               ├── tracker
    │   │               └── validators
    │   └── resources
    └── test
        ├── java
        │   └── com
        │       └── example
        │           └── demo
        │               ├── scheduler
        │               ├── service
        │               └── validators
        └── resources

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
Starting a Gradle Daemon (subsequent builds will be faster)
Java HotSpot(TM) 64-Bit Server VM warning: Sharing is only supported for boot loader classes because bootstrap classpath has been appended

> Task :test

RefreshAheadSchedulerTest > Test refresh hot users should call refreshUser for each hot user. STARTED

RefreshAheadSchedulerTest > Test refresh hot users should call refreshUser for each hot user. STANDARD_OUT
    21:24:50.177 [Test worker] INFO com.example.demo.scheduler.RefreshAheadScheduler -- Current refresh hotUsers frequency is 5 mins.
    21:24:50.180 [Test worker] INFO com.example.demo.scheduler.RefreshAheadScheduler -- Refreshing 3 hot users: [30, 20, 10]

RefreshAheadSchedulerTest > Test refresh hot users should call refreshUser for each hot user. PASSED

RefreshAheadSchedulerTest > Test refresh hot user does nothing when there are no hot users. STARTED

RefreshAheadSchedulerTest > Test refresh hot user does nothing when there are no hot users. STANDARD_OUT
    21:24:50.191 [Test worker] INFO com.example.demo.scheduler.RefreshAheadScheduler -- Current refresh hotUsers frequency is 5 mins.
    21:24:50.191 [Test worker] INFO com.example.demo.scheduler.RefreshAheadScheduler -- No hot users found to refresh at this time.

RefreshAheadSchedulerTest > Test refresh hot user does nothing when there are no hot users. PASSED

User Service Redis Test with Redis running in TestContainer. STANDARD_OUT
    21:24:50.219 [Test worker] INFO org.testcontainers.images.PullPolicy -- Image pull policy will be performed by: DefaultPullPolicy()
    21:24:50.220 [Test worker] INFO org.testcontainers.utility.ImageNameSubstitutor -- Image name substitution will be performed by: DefaultImageNameSubstitutor (composite of 'ConfigurationFileImageNameSubstitutor' and 'PrefixingImageNameSubstitutor')
    21:24:50.224 [Test worker] INFO org.testcontainers.DockerClientFactory -- Testcontainers version: 1.21.3
    21:24:50.329 [Test worker] INFO org.testcontainers.dockerclient.DockerClientProviderStrategy -- Loaded org.testcontainers.dockerclient.UnixSocketClientProviderStrategy from ~/.testcontainers.properties, will try it first
    21:24:50.467 [Test worker] INFO org.testcontainers.dockerclient.DockerClientProviderStrategy -- Found Docker environment with local Unix socket (unix:///var/run/docker.sock)
    21:24:50.467 [Test worker] INFO org.testcontainers.DockerClientFactory -- Docker host IP address is localhost
    21:24:50.477 [Test worker] INFO org.testcontainers.DockerClientFactory -- Connected to docker: 
      Server Version: 28.4.0
      API Version: 1.51
      Operating System: Docker Desktop
      Total Memory: 7837 MB
      Labels: 
        com.docker.desktop.address=unix:///Users/t827056/Library/Containers/com.docker.docker/Data/docker-cli.sock
    21:24:50.494 [Test worker] INFO tc.testcontainers/ryuk:0.12.0 -- Creating container for image: testcontainers/ryuk:0.12.0
    21:24:50.813 [Test worker] INFO tc.testcontainers/ryuk:0.12.0 -- Container testcontainers/ryuk:0.12.0 is starting: 55942d4b8e6f5a318446714a661042fd25f5400fe4defd7cbb563c03c49ab967
    21:24:51.096 [Test worker] INFO tc.testcontainers/ryuk:0.12.0 -- Container testcontainers/ryuk:0.12.0 started in PT0.602121S
    21:24:51.101 [Test worker] INFO org.testcontainers.utility.RyukResourceReaper -- Ryuk started - will monitor and terminate Testcontainers containers on JVM exit
    21:24:51.101 [Test worker] INFO org.testcontainers.DockerClientFactory -- Checking the system...
    21:24:51.101 [Test worker] INFO org.testcontainers.DockerClientFactory -- ✔︎ Docker server version should be at least 1.6.0
    21:24:51.101 [Test worker] INFO tc.redis:7.0.11-alpine -- Creating container for image: redis:7.0.11-alpine
    21:24:51.133 [Test worker] INFO tc.redis:7.0.11-alpine -- Container redis:7.0.11-alpine is starting: d97176ac7be5afe47a570cea74d9b6469ea80c47f9aa11b9eb531692f6f1f1a3
    21:24:51.283 [Test worker] INFO tc.redis:7.0.11-alpine -- Container redis:7.0.11-alpine started in PT0.181678S
    21:24:51.322 [Test worker] INFO org.springframework.test.context.support.AnnotationConfigContextLoaderUtils -- Could not detect default configuration classes for test class [com.example.demo.service.UserServiceTest]: UserServiceTest does not declare any static, non-private, non-final, nested classes annotated with @Configuration.
    21:24:51.368 [Test worker] INFO org.springframework.boot.test.context.SpringBootTestContextBootstrapper -- Found @SpringBootConfiguration com.example.demo.SpringRedisDemoApplication for test class com.example.demo.service.UserServiceTest

      .   ____          _            __ _ _
     /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
    ( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
     \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
      '  |____| .__|_| |_|_| |_\__, | / / / /
     =========|_|==============|___/=/_/_/_/

     :: Spring Boot ::                (v3.3.4)

    2025-10-06T21:24:51.552+13:00  INFO 60781 --- [spring-redis-example] [    Test worker] c.example.demo.service.UserServiceTest   : Starting UserServiceTest using Java 21.0.3 with PID 60781 (started by t827056 in /Users/t827056/personal-workspace/spring-boot-redis-demo)
    2025-10-06T21:24:51.552+13:00 DEBUG 60781 --- [spring-redis-example] [    Test worker] c.example.demo.service.UserServiceTest   : Running with Spring Boot v3.3.4, Spring v6.1.13
    2025-10-06T21:24:51.553+13:00  INFO 60781 --- [spring-redis-example] [    Test worker] c.example.demo.service.UserServiceTest   : No active profile set, falling back to 1 default profile: "default"
    2025-10-06T21:24:51.766+13:00  INFO 60781 --- [spring-redis-example] [    Test worker] .s.d.r.c.RepositoryConfigurationDelegate : Multiple Spring Data modules found, entering strict repository configuration mode
    2025-10-06T21:24:51.766+13:00  INFO 60781 --- [spring-redis-example] [    Test worker] .s.d.r.c.RepositoryConfigurationDelegate : Bootstrapping Spring Data Redis repositories in DEFAULT mode.
    2025-10-06T21:24:51.780+13:00  INFO 60781 --- [spring-redis-example] [    Test worker] .s.d.r.c.RepositoryConfigurationDelegate : Finished Spring Data repository scanning in 7 ms. Found 0 Redis repository interfaces.
    2025-10-06T21:24:52.320+13:00  INFO 60781 --- [spring-redis-example] [    Test worker] c.example.demo.service.UserServiceTest   : Started UserServiceTest in 0.89 seconds (process running for 3.313)

User Service Redis Test with Redis running in TestContainer. > Test addUser stores user and caches it STARTED

User Service Redis Test with Redis running in TestContainer. > Test addUser stores user and caches it STANDARD_OUT
    2025-10-06T21:24:52.474+13:00  INFO 60781 --- [spring-redis-example] [    Test worker] c.example.demo.service.UserServiceImpl   : Adding new user to database and cache: 3

User Service Redis Test with Redis running in TestContainer. > Test addUser stores user and caches it PASSED

User Service Redis Test with Redis running in TestContainer. > Test getAllUsers returns immutable map. STARTED

User Service Redis Test with Redis running in TestContainer. > Test getAllUsers returns immutable map. STANDARD_OUT
    2025-10-06T21:24:52.549+13:00  INFO 60781 --- [spring-redis-example] [    Test worker] c.example.demo.service.UserServiceImpl   : Removing all users from database and cache
    2025-10-06T21:24:52.550+13:00  INFO 60781 --- [spring-redis-example] [    Test worker] c.example.demo.service.UserServiceImpl   : Adding new user to database and cache: 1
    2025-10-06T21:24:52.551+13:00  INFO 60781 --- [spring-redis-example] [    Test worker] c.example.demo.service.UserServiceImpl   : Adding new user to database and cache: 2
    2025-10-06T21:24:52.552+13:00 DEBUG 60781 --- [spring-redis-example] [    Test worker] c.example.demo.service.UserServiceImpl   : Fetching all users from database

User Service Redis Test with Redis running in TestContainer. > Test getAllUsers returns immutable map. PASSED

User Service Redis Test with Redis running in TestContainer. > Test updateUser updates cache STARTED

User Service Redis Test with Redis running in TestContainer. > Test updateUser updates cache STANDARD_OUT
    2025-10-06T21:24:52.563+13:00  INFO 60781 --- [spring-redis-example] [    Test worker] c.example.demo.service.UserServiceImpl   : Adding new user to database and cache: 5
    2025-10-06T21:24:52.564+13:00  INFO 60781 --- [spring-redis-example] [    Test worker] c.example.demo.service.UserServiceImpl   : Updating user in database and cache: 5

User Service Redis Test with Redis running in TestContainer. > Test updateUser updates cache PASSED

User Service Redis Test with Redis running in TestContainer. > Test fetching from cache got the same user having same value. STARTED

User Service Redis Test with Redis running in TestContainer. > Test fetching from cache got the same user having same value. STANDARD_OUT
    2025-10-06T21:24:52.570+13:00 DEBUG 60781 --- [spring-redis-example] [    Test worker] c.example.demo.service.UserServiceImpl   : Fetching user from DB with ID: 1

User Service Redis Test with Redis running in TestContainer. > Test fetching from cache got the same user having same value. PASSED

User Service Redis Test with Redis running in TestContainer. > Test measuring the getUserByIdBypassingCache is slower than get. STARTED

User Service Redis Test with Redis running in TestContainer. > Test measuring the getUserByIdBypassingCache is slower than get. STANDARD_OUT
    2025-10-06T21:24:53.093+13:00 DEBUG 60781 --- [spring-redis-example] [    Test worker] c.example.demo.service.UserServiceImpl   : Fetching user from DB with ID: 1
    2025-10-06T21:24:53.606+13:00 DEBUG 60781 --- [spring-redis-example] [    Test worker] c.example.demo.service.UserServiceImpl   : Fetching user by ID bypassing cache: 1

User Service Redis Test with Redis running in TestContainer. > Test measuring the getUserByIdBypassingCache is slower than get. PASSED

User Service Redis Test with Redis running in TestContainer. > Test measuring the first normal fetch user from database and second fetch user from cache. STARTED

User Service Redis Test with Redis running in TestContainer. > Test measuring the first normal fetch user from database and second fetch user from cache. STANDARD_OUT
    2025-10-06T21:24:54.114+13:00 DEBUG 60781 --- [spring-redis-example] [    Test worker] c.example.demo.service.UserServiceImpl   : Fetching user from DB with ID: 1

User Service Redis Test with Redis running in TestContainer. > Test measuring the first normal fetch user from database and second fetch user from cache. PASSED

PositiveDurationValidatorUnitTest > Valid feature flag service cache ttl should return true. STARTED

PositiveDurationValidatorUnitTest > Valid feature flag service cache ttl should return true. PASSED

PositiveDurationValidatorUnitTest > Test invalid cache duration configurations, especially zero and negative duration. > 1 -> ZERO duration. STARTED

PositiveDurationValidatorUnitTest > Test invalid cache duration configurations, especially zero and negative duration. > 1 -> ZERO duration. PASSED

PositiveDurationValidatorUnitTest > Test invalid cache duration configurations, especially zero and negative duration. > 2 -> Prefix negative duration. STARTED

PositiveDurationValidatorUnitTest > Test invalid cache duration configurations, especially zero and negative duration. > 2 -> Prefix negative duration. PASSED

PositiveDurationValidatorUnitTest > Test invalid cache duration configurations, especially zero and negative duration. > 3 -> Negative number duration. STARTED

PositiveDurationValidatorUnitTest > Test invalid cache duration configurations, especially zero and negative duration. > 3 -> Negative number duration. PASSED

BUILD SUCCESSFUL in 16s
8 actionable tasks: 5 executed, 3 up-to-date
```
