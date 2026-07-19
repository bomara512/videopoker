# poker-api: Spring Boot 4.1 + Java 25 + Gradle 9.6 Upgrade Design

**Date:** 2026-07-12
**Status:** Approved

## Goal

Replace the backend's dead toolchain — Java 11 target (buildable only on a
JDK ≤13), Gradle 6.3, Spring Boot 2.3.4, abandoned `it.ozimov:embedded-redis` —
with current versions: Spring Boot 4.1.0, Java 25 (current LTS, already
installed via Homebrew), Gradle 9.6.1, Testcontainers. API behavior is
unchanged; the game plays identically.

## Approach: direct jump, fix-forward

Incremental hops (2.3→2.7→3.5→4.1) and a fresh-scaffold port were rejected:
the app surface is ~16 classes (one controller, one service, one Redis
repository, scoring/deck domain code), and the known breakage list is short.
The 16-test Playwright e2e suite driving the real API is the
behavior-preservation net, alongside the ported unit/integration tests.

## Toolchain

- **Gradle:** wrapper 6.3 → 9.6.1. Bootstrap wrinkle: Gradle 6.3 cannot run on
  a modern JVM, so the `./gradlew wrapper --gradle-version 9.6.1` step itself
  runs under the Azul JDK 13 at
  `~/Library/Java/JavaVirtualMachines/azul-13.0.14/Contents/Home`; everything
  after runs on JDK 25.
- **Java:** Homebrew OpenJDK 25 (`/opt/homebrew/Cellar/openjdk/...`, already
  the shell default). `poker-api/build.gradle` declares a Java toolchain
  (`languageVersion = JavaLanguageVersion.of(25)`) so builds stop depending on
  `JAVA_HOME`.
- **Spring Boot:** plugin and BOM 4.1.0 (Spring Framework 7).

## Dependencies

- Starters: web + data-redis, with exact Boot 4.1 artifact names verified
  against the 4.1 dependency catalog at planning time (Boot 4 reorganized
  starter names; do not guess).
- **Dropped:** `redis.clients:jedis:3.3.0` pin — Boot's managed default client
  (Lettuce) takes over; Spring Data Redis repositories are client-agnostic.
- **Dropped:** `com.avast.gradle.docker-compose` plugin (v0.7.1) and its
  `dockerCompose` block — never wired to any task; docs already instruct
  running `docker compose` directly.
- **Dropped:** `it.ozimov:embedded-redis:0.7.2` (abandoned 2018).
- **Added (test):** `spring-boot-testcontainers`, Testcontainers
  JUnit Jupiter support, and a Redis container wired via `@ServiceConnection`.
  Tests that need Redis require Docker running — same assumption the e2e flow
  already makes.
- Lombok stays (current version handles JDK 25).

## Code changes (small by design)

- **Deleted:** `TestRedisConfiguration` (the only `javax.*` imports in the
  codebase die with it) and `src/test/resources/application.properties`
  (`spring.redis.port=6370`, obsolete; the Boot 2→3 rename to
  `spring.data.redis.*` becomes moot because `@ServiceConnection` supplies
  connection details). Main `application.properties` is empty and stays empty.
- **Records:** `CardDto`, `GameDto`, `PayoutsDto` become Java records; JSON
  field names are unchanged, so the UI contract is untouched. `GameMapper` and
  the test builders (`GameDtoBuilder`, `GameEntityBuilder`) adapt to record
  construction/accessors.
- **Unchanged:** `GameController` (including open `@CrossOrigin` and the
  `InvalidGameStateException` → 400 `{message}` handler), `GameService` rules
  (bet 1–5, credits checks, deck auto-replacement thresholds), scoring and
  deck packages, `GameEntity` (`@RedisHash`, 7-day TTL), Lombok patterns.
- Redis-dependent tests (`GameControllerIT`, and any test that exercised
  embedded Redis) switch to the Testcontainers setup; pure unit tests are
  untouched.

## Verification (success criteria)

1. `./gradlew` (default: clean, test, integrationTest) green on JDK 25, with
   Testcontainers providing Redis.
2. The unmodified 16-test Playwright suite passes against the upgraded backend
   (real proof of unchanged JSON contract and game flow).
3. Manual smoke: game plays at localhost:4200 against the new backend.

## Delivery and docs

- Branch `upgrade/spring-boot4-java25`; subagent-driven execution; never push.
- Docs updated: root `README.md` (JDK prerequisite inverts from "Java 11–13"
  to Java 17+/25; compose instructions unchanged), `CLAUDE.md` backend section
  (new JDK guidance replacing the azul-13 workaround, Testcontainers note,
  `composeUp` reference removed, Boot/Java versions in the overview line).
- `.run/` IntelliJ configs reviewed; updated only if they reference removed
  bits.

## Out of scope

- Any API or behavior change, new endpoints, or security hardening.
- Virtual threads, observability/actuator, gRPC, or other Boot 4 features.
- Frontend changes (the UI and its e2e suite must pass unmodified).
- CI/deployment setup.
