# PokerUi

Angular 22 frontend for Poker. Requires Node 24 (`.nvmrc` — run `nvm use`).

## Development server

`npm start` serves the app at http://localhost:4200 and proxies API calls
(`/game/**`) to the backend (see the root README for running it). The backend
port defaults to 8080; to use another port, set `SERVER_PORT` for both
processes — it's the same variable Spring Boot reads:

```bash
SERVER_PORT=8081 ./gradlew :poker-api:bootRun
SERVER_PORT=8081 npm start
```

## Unit tests

`npm test` runs the Vitest suite. Single file: `ng test --include='**/game-service.spec.ts'`.

## End-to-end tests

`npm run e2e` runs the Playwright suite. It starts the dev server itself, but
the backend + Redis must already be running.

## Build

`npm run build` outputs to `dist/poker-ui/`.
