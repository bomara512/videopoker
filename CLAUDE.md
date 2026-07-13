# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

Video poker game with two modules:
- `videopoker-api` — Spring Boot 4.1 (Java 25) REST backend, game state stored in Redis
- `videopoker-ui` — Angular 22 frontend (Node 24 via .nvmrc; built with npm, not Gradle)

## Commands

### Backend (from repo root)

The build declares a Java 25 toolchain — any JDK 17+ can launch Gradle and it will locate/download JDK 25 itself.

```bash
./gradlew                      # default tasks: clean, test, integrationTest
./gradlew test                 # unit tests only (excludes **/integration/**)
./gradlew integrationTest      # integration tests only (src/test/java/net/bitbucketlist/integration/)
./gradlew :videopoker-api:test --tests "GameServiceTest"                    # single test class
./gradlew :videopoker-api:test --tests "GameServiceTest.methodName"        # single test method
```

Tests need Docker: `GameControllerIT` starts a redis:alpine Testcontainer via `@ServiceConnection` (no port/property config). Pure unit tests (service, mapper, deck, scoring) run without it. Integration tests must live under the `net.bitbucketlist.integration` package to be picked up by the `integrationTest` task.

### Running the app locally

Requires a real Redis on the default port 6379:

```bash
cd videopoker-api && docker compose up -d
./gradlew :videopoker-api:bootRun            # API on http://localhost:8080
```

### Frontend (from videopoker-ui/)

Requires Node 24: `nvm use` (reads `.nvmrc`).

```bash
npm start        # ng serve, http://localhost:4200
npm test         # Vitest unit tests; single file: ng test --include='**/game-service.spec.ts'
npm run e2e      # Playwright; starts ng serve itself, needs backend + Redis running
npm run build    # ng build
```

The UI hardcodes the API base URL `http://localhost:8080` in `src/app/game-service.ts`; the controller has an open `@CrossOrigin` to allow this. Game and payout-schedule state are held in `GameService` signals (`game`, `payoutSchedule`, `holds`, `errorMessage`).

## Architecture

### Backend flow

`GameController` → `GameService` → `GameRepository` (Spring Data Redis). `GameEntity` is a `@RedisHash` with a 7-day TTL holding the full game state: the deck (remaining cards), current hand, bet, credits, and `GameState`. `GameMapper` converts entity → `GameDto` for all responses.

### Game state machine

`GameState` has two states enforced by `GameService`: `READY_TO_DEAL` (can change bet, deal) ↔ `READY_TO_DRAW` (can only draw). Violations throw `InvalidGameStateException`, which the controller's `@ExceptionHandler` maps to a 400 with a `{message}` body. Other rules that live in `GameService`: bet range 1–5, credits checked before deal, deck auto-replaced with a fresh shuffled 52-card deck when it runs low (≤10 cards at deal, ≤5 after draw). Draw `holds` are 0-based hand indexes; non-held positions are replaced.

### Scoring (`scoring` package)

`PokerHand` wraps a 5-card hand with `isX()` predicates that are deliberately mutually exclusive (e.g. `isFlush()` returns false for straight/royal flushes) so `calculateBestHand()` can pick the single matching `PokerHandEnum`. `PayoutService` holds the static pay table: per-hand payout lists indexed by `bet - 1` (bets 1–5); `JACKS_OR_BETTER` is the minimum paying hand, `HIGH_CARD` pays 0 and is excluded from the published payout schedule endpoint.

### Domain primitives (`deck` package)

`Card` (Suit + Rank), `Deck` (deals from the top, tracks remaining cards — its size is serialized as `deckSize` in the DTO).

### Conventions

- Lombok used throughout: `@RequiredArgsConstructor` + `@FieldDefaults(level = PRIVATE, makeFinal = true)` for Spring beans, `@Value` for immutable types, `@Data` for the entity. DTOs are Java records.
- Test builders live in `videopoker-api/src/test/java/.../builder/` (`GameEntityBuilder`, `GameDtoBuilder`); `TestPokerHands` provides canned hands for scoring tests.