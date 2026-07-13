# Backend Stack Upgrade Implementation Plan (Spring Boot 4.1 / Java 25 / Gradle 9.6)

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Direct-jump `videopoker-api` from Java 11 / Gradle 6.3 / Spring Boot 2.3.4 to Java 25 / Gradle 9.6.1 / Spring Boot 4.1.0 with Testcontainers replacing the abandoned embedded-redis, preserving API behavior exactly.

**Architecture:** One toolchain/build rewrite, then fix-forward: main code compiles almost unchanged (the only `javax.*` usage lives in test config being deleted), tests move to Testcontainers, DTOs become records last (gated by the green suite). The unmodified 16-test Playwright e2e suite is the final behavior proof.

**Tech Stack:** Spring Boot 4.1.0 (Framework 7), Java 25 via Gradle toolchain, Gradle 9.6.1, Lombok (Boot-managed), Testcontainers + `@ServiceConnection`, Lettuce (Boot default Redis client).

**Spec:** `docs/superpowers/specs/2026-07-12-backend-stack-upgrade-design.md`

## Global Constraints

- All work on branch `upgrade/spring-boot4-java25` off master. Never push. Commit messages end with `Co-Authored-By: Claude Fable 5 <noreply@anthropic.com>`.
- API behavior unchanged: same endpoints, same JSON field names, bet range 1–5, credits rules, deck auto-replacement thresholds, `InvalidGameStateException` → 400 `{message}`, open `@CrossOrigin`.
- The frontend and its Playwright suite must NOT be modified; `npm run e2e` (16 tests) must pass against the upgraded backend in Task 4.
- JDKs on this machine: Homebrew OpenJDK 25 (`java` on PATH, `/opt/homebrew/opt/openjdk`), Azul 13 at `~/Library/Java/JavaVirtualMachines/azul-13.0.14/Contents/Home` (needed ONLY for the one wrapper-bootstrap command). The Gradle toolchain (`languageVersion = 25`) must make `JAVA_HOME` irrelevant afterwards.
- Docker must be running for Redis-dependent tests (Testcontainers) and for the compose Redis used by bootRun/e2e.
- Boot 4 artifact/package names were pre-verified: web starter is `spring-boot-starter-webmvc`; per-starter test starters exist (e.g. `spring-boot-starter-webmvc-test`); module-specific classes moved to `org.springframework.boot.<module>` packages. If an artifact or import in this plan fails to resolve, find the relocated name in the Boot 4.1 BOM/jars (do not silently drop the dependency or annotation) and record the correction in your report.

---

### Task 1: Gradle 9.6.1 wrapper + Spring Boot 4.1 build files

**Files:**
- Modify: `gradle/wrapper/gradle-wrapper.properties`, `gradlew`, `gradlew.bat`, `gradle/wrapper/gradle-wrapper.jar` (via the wrapper task — never hand-edit)
- Modify: `videopoker-api/build.gradle` (full rewrite below)
- Unchanged: root `build.gradle` (`defaultTasks 'clean', 'test', 'integrationTest'`), `settings.gradle`

**Interfaces:**
- Consumes: nothing.
- Produces: a Gradle 9.6.1 build where `./gradlew :videopoker-api:compileJava` and `./gradlew :videopoker-api:bootJar -x test` succeed on JDK 25 with Boot 4.1. Test compilation is expected to still FAIL (embedded-redis imports) — Task 2 fixes that; do not try to fix tests in this task.

- [ ] **Step 1: Create the branch**

```bash
cd /Users/bomara/workspace/dev/videopoker-ai-refresh/videopoker
git checkout -b upgrade/spring-boot4-java25
```

- [ ] **Step 2: Upgrade the wrapper (bootstrap under JDK 13, then regenerate under the new Gradle)**

```bash
JAVA_HOME=~/Library/Java/JavaVirtualMachines/azul-13.0.14/Contents/Home ./gradlew wrapper --gradle-version 9.6.1
./gradlew wrapper --gradle-version 9.6.1   # second run under the new version regenerates scripts/jar; default JDK 25 is fine now
./gradlew --version
```

Expected: `Gradle 9.6.1`, JVM shows 25.x. (First command needs the old JDK because Gradle 6.3 cannot start on JDK 25.)

- [ ] **Step 3: Rewrite `videopoker-api/build.gradle`**

```groovy
import org.springframework.boot.gradle.plugin.SpringBootPlugin

plugins {
    id 'org.springframework.boot' version '4.1.0'
    id 'java'
}

group = 'net.bitbucketlist'
version = '0.0.1-SNAPSHOT'

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

configurations {
    compileOnly {
        extendsFrom annotationProcessor
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation platform(SpringBootPlugin.BOM_COORDINATES)
    annotationProcessor platform(SpringBootPlugin.BOM_COORDINATES)

    annotationProcessor 'org.projectlombok:lombok'
    compileOnly 'org.projectlombok:lombok'

    implementation 'org.springframework.boot:spring-boot-starter-webmvc'
    implementation 'org.springframework.boot:spring-boot-starter-data-redis'

    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testImplementation 'org.springframework.boot:spring-boot-starter-webmvc-test'
    testImplementation 'org.springframework.boot:spring-boot-testcontainers'
    testImplementation 'org.testcontainers:junit-jupiter'
}

tasks.named('test') {
    useJUnitPlatform()
    exclude '**/integration/**'
    jvmArgs '-XX:+EnableDynamicAgentLoading'   // silence Mockito dynamic-agent warning on JDK 21+
}

tasks.register('integrationTest', Test) {
    useJUnitPlatform()
    // Gradle 8+ removed the old convention that pointed bare Test tasks at the test source set — set explicitly:
    testClassesDirs = sourceSets.test.output.classesDirs
    classpath = sourceSets.test.runtimeClasspath
    include '**/integration/**'
    jvmArgs '-XX:+EnableDynamicAgentLoading'
}
```

What's gone versus the old file, per spec: `io.spring.dependency-management` plugin (replaced by the platform BOM), `com.avast.gradle.docker-compose` plugin and its `dockerCompose` block, `redis.clients:jedis:3.3.0` (Lettuce via Boot default), `it.ozimov:embedded-redis:0.7.2`, `sourceCompatibility = '11'` (replaced by the toolchain).

- [ ] **Step 4: Verify main code compiles and the boot jar builds**

```bash
./gradlew :videopoker-api:compileJava
./gradlew :videopoker-api:bootJar -x test
ls videopoker-api/build/libs/    # Expected: videopoker-api-0.0.1-SNAPSHOT.jar
```

Expected: both succeed with zero source changes to `src/main` (no `javax.*` imports exist there). If `compileJava` reports missing Spring classes, an artifact name is off — resolve per Global Constraints. `./gradlew :videopoker-api:compileTestJava` is EXPECTED to fail (`redis.embedded` unresolved) — that is Task 2's work, not a defect in this task.

- [ ] **Step 5: Commit**

```bash
git add gradle gradlew gradlew.bat videopoker-api/build.gradle
git commit -m "Upgrade build to Gradle 9.6.1 / Spring Boot 4.1 / Java 25 toolchain

Drops jedis pin (Lettuce default), avast docker-compose plugin (unused),
and the dependency-management plugin (platform BOM instead). Test
sources still reference embedded-redis; migrated next commit.

Co-Authored-By: Claude Fable 5 <noreply@anthropic.com>"
```

---

### Task 2: Testcontainers test infrastructure

**Files:**
- Delete: `videopoker-api/src/test/java/net/bitbucketlist/videopoker/TestRedisConfiguration.java`
- Delete: `videopoker-api/src/test/resources/application.properties`
- Modify: `videopoker-api/src/test/java/net/bitbucketlist/integration/GameControllerIT.java` (class header only; test methods unchanged)

**Interfaces:**
- Consumes: Task 1's build (Testcontainers deps on the test classpath).
- Produces: full `./gradlew` (clean, test, integrationTest) green on JDK 25 — the baseline Task 3's refactor is gated on.

- [ ] **Step 1: Delete the dead embedded-redis pieces**

```bash
git rm videopoker-api/src/test/java/net/bitbucketlist/videopoker/TestRedisConfiguration.java
git rm videopoker-api/src/test/resources/application.properties
```

(This removes the codebase's only `javax.*` imports and the obsolete `spring.redis.port=6370` property; `@ServiceConnection` supplies connection details at runtime, so no `spring.data.redis.*` property is needed anywhere.)

- [ ] **Step 2: Rewire `GameControllerIT` to Testcontainers**

Replace the class annotations/imports so the header reads:

```java
package net.bitbucketlist.integration;

// ... keep all existing imports EXCEPT the two removed below; add the five marked new
import net.bitbucketlist.videopoker.VideoPokerApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection; // new
import org.testcontainers.containers.GenericContainer;                              // new
import org.testcontainers.junit.jupiter.Container;                                  // new
import org.testcontainers.junit.jupiter.Testcontainers;                             // new

@Testcontainers
@SpringBootTest(classes = VideoPokerApplication.class)
@AutoConfigureMockMvc
class GameControllerIT {

    @Container
    @ServiceConnection(name = "redis")
    static GenericContainer<?> redis = new GenericContainer<>("redis:alpine");
```

Removed: the `TestRedisConfiguration` import and its entry in `@SpringBootTest(classes = ...)`.
`redis:alpine` matches `videopoker-api/docker-compose.yml`, so tests exercise the same image production-dev runs.
Note on `@AutoConfigureMockMvc`: Boot 4 relocated module-specific test classes out of `org.springframework.boot.test.autoconfigure.web.servlet`; if the old import no longer resolves, use the relocated one from the webmvc test module (`org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc`) — whichever the compiler accepts is the correct 4.1 location; record it in your report. Same rule for any other import the compiler rejects (e.g. `MockMvc` itself stays in `org.springframework.test.web.servlet`).

- [ ] **Step 3: Run the full suite (Docker must be running)**

```bash
docker info >/dev/null && ./gradlew
```

Expected: `clean`, `test` (unit: GameServiceTest, GameMapperTest, deck + scoring tests), and `integrationTest` (GameControllerIT, container pulled/started automatically) all PASS on JDK 25 with pristine output — no Mockito agent warning (jvmArgs from Task 1), no deprecation noise. First run may take a minute pulling `redis:alpine`.

- [ ] **Step 4: Commit**

```bash
git add -A videopoker-api/src/test
git commit -m "Replace abandoned embedded-redis with Testcontainers

GameControllerIT starts redis:alpine via @ServiceConnection; the
javax-era TestRedisConfiguration and its port property are gone.

Co-Authored-By: Claude Fable 5 <noreply@anthropic.com>"
```

---

### Task 3: DTOs to records

**Files:**
- Modify: `videopoker-api/src/main/java/net/bitbucketlist/videopoker/dto/CardDto.java`
- Modify: `videopoker-api/src/main/java/net/bitbucketlist/videopoker/dto/GameDto.java`
- Modify: `videopoker-api/src/main/java/net/bitbucketlist/videopoker/dto/PayoutsDto.java`
- Modify: `videopoker-api/src/test/java/net/bitbucketlist/integration/GameControllerIT.java` (accessor renames only)
- Unchanged: `GameMapper`, `GameDtoBuilder`, `GameEntityBuilder` (all construct DTOs positionally — records keep the canonical constructor), `GameMapperTest` (relies on `equals`, which records provide).

**Interfaces:**
- Consumes: Task 2's green suite (the safety net for this refactor).
- Produces: `record CardDto(Suit suit, Rank rank)`, `record GameDto(UUID id, int deckSize, int bet, int credits, List<CardDto> hand, PokerHandEnum handRank, GameState gameState)`, `record PayoutsDto(PokerHandEnum hand, List<Integer> payouts)`. JSON field names are the record component names — identical to today's Lombok-generated names, so the wire contract is unchanged.

- [ ] **Step 1: Rewrite the three DTOs as records**

`CardDto.java`:

```java
package net.bitbucketlist.videopoker.dto;

import net.bitbucketlist.videopoker.deck.Rank;
import net.bitbucketlist.videopoker.deck.Suit;

public record CardDto(Suit suit, Rank rank) {
}
```

`GameDto.java`:

```java
package net.bitbucketlist.videopoker.dto;

import net.bitbucketlist.videopoker.GameState;
import net.bitbucketlist.videopoker.scoring.PokerHandEnum;

import java.util.List;
import java.util.UUID;

public record GameDto(
    UUID id,
    int deckSize,
    int bet,
    int credits,
    List<CardDto> hand,
    PokerHandEnum handRank,
    GameState gameState
) {
}
```

`PayoutsDto.java`:

```java
package net.bitbucketlist.videopoker.dto;

import net.bitbucketlist.videopoker.scoring.PokerHandEnum;

import java.util.List;

public record PayoutsDto(PokerHandEnum hand, List<Integer> payouts) {
}
```

- [ ] **Step 2: Update the accessor call sites**

Only `GameControllerIT.java` calls DTO getters (verified by grep; `GameServiceTest`'s `getX()` calls are on `GameEntity`, which keeps Lombok). In `GameControllerIT.java` replace, on `GameDto`/`CardDto` values only:
- `.getId()` → `.id()` (lines ~75, 84, 92, 94, 96, 111, 113, 115, 126, 128, 130, 145, 147, 167, 168, 170, 172)
- `.getBet()` → `.bet()` (line ~82)
- `.getHand()` → `.hand()` (lines ~145, 157–161)

Jackson deserialization in the IT (`objectMapper.readValue(..., GameDto.class)`) works with records out of the box on Boot 4's Jackson.

- [ ] **Step 3: Full suite green**

```bash
./gradlew
```

Expected: all unit + integration tests PASS. `GameMapperTest`'s `isEqualTo` assertions prove record equality matches the old Lombok `@Data` semantics.

- [ ] **Step 4: Commit**

```bash
git add videopoker-api/src
git commit -m "Convert DTOs to records

Positional construction sites (GameMapper, builders) are unchanged;
only GameControllerIT's accessor calls rename. Wire format identical.

Co-Authored-By: Claude Fable 5 <noreply@anthropic.com>"
```

---

### Task 4: E2E verification and docs

**Files:**
- Modify: `README.md` (root)
- Modify: `CLAUDE.md`
- Review (modify only if they reference removed bits): `.run/*.run.xml`

**Interfaces:**
- Consumes: everything above green.
- Produces: docs matching the new stack; e2e proof of unchanged behavior.

- [ ] **Step 1: Restart the backend on the new stack**

An OLD backend process (pre-upgrade jar under JDK 13) may still be running on port 8080 — kill it first, keep the compose Redis:

```bash
lsof -ti :8080 | xargs kill 2>/dev/null; sleep 2
docker compose -f videopoker-api/docker-compose.yml up -d
./gradlew :videopoker-api:bootRun   # background; ready on "Started VideoPokerApplication"
```

- [ ] **Step 2: Run the untouched Playwright suite**

```bash
cd videopoker-ui
export PATH="$HOME/.nvm/versions/node/$(ls "$HOME/.nvm/versions/node" | grep '^v24' | sort -V | tail -1)/bin:$PATH"
npm run e2e
```

Expected: 16/16 PASS — the full game flow (credits math, payout table JSON, deal/draw/hold, error-free console) against Boot 4.1. If any test fails, STOP: that is a behavior regression from Tasks 1–3; report BLOCKED with the failure, do not adjust the e2e suite.

- [ ] **Step 3: Update root `README.md`**

Replace the prerequisites line:

```markdown
1. Install docker, curl, jq, and a Java 11–13 JDK (the Gradle 6.3 build fails on newer JVMs).
```

with:

```markdown
1. Install docker, curl, jq, and a Java 17+ JDK (Java 25 recommended; the build's Gradle toolchain targets 25).
```

- [ ] **Step 4: Update `CLAUDE.md`**

- Overview line: `videopoker-api — Spring Boot 4.1 (Java 25) REST backend, game state stored in Redis`.
- Backend commands section: DELETE the paragraph about needing a Java 11–13 JDK and the `JAVA_HOME` export example; replace with one line: `The build declares a Java 25 toolchain — any JDK 17+ can launch Gradle and it will locate/download JDK 25 itself.`
- Replace the sentence saying tests don't need Docker (`TestRedisConfiguration` starts an embedded Redis on port 6370...) with: `Tests need Docker: GameControllerIT starts a redis:alpine Testcontainer via @ServiceConnection (no port/property config). Pure unit tests (service, mapper, deck, scoring) run without it.`
- In "Running the app locally": remove the `# or: ./gradlew composeUp` alternative (plugin dropped).
- Leave the game state machine / scoring / conventions sections alone, except the Lombok conventions line gains: `DTOs are Java records.`

- [ ] **Step 5: Check the IntelliJ run configs**

```bash
grep -l "gradle" .run/*.run.xml | xargs grep -o 'name="[^"]*"' | sort -u
```

These are plain Gradle-task run configs (`test`, `integrationTest`); they only break if they pin a JDK or reference removed tasks (`composeUp`). Update only what actually references removed bits; otherwise leave untouched and note that in the report.

- [ ] **Step 6: Final full verification**

```bash
./gradlew                       # clean test integrationTest — PASS
# e2e already verified in Step 2 against the running backend
```

Stop bootRun afterwards; leave Redis per session norms.

- [ ] **Step 7: Commit**

```bash
git add README.md CLAUDE.md .run
git commit -m "Update docs for Spring Boot 4.1 / Java 25 backend

Co-Authored-By: Claude Fable 5 <noreply@anthropic.com>"
```

---

## Completion

After Task 4, use superpowers:finishing-a-development-branch — full verification, then merge/PR options. **Never push without explicit permission.**
