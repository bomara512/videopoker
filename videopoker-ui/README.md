# VideopokerUi

Angular 22 frontend for VideoPoker. Requires Node 24 (`.nvmrc` — run `nvm use`).

## Development server

`npm start` serves the app at http://localhost:4200. It expects the backend API
at http://localhost:8080 (see the root README for running it).

## Unit tests

`npm test` runs the Vitest suite. Single file: `ng test --include='**/game-service.spec.ts'`.

## End-to-end tests

`npm run e2e` runs the Playwright suite. It starts the dev server itself, but
the backend + Redis must already be running.

## Build

`npm run build` outputs to `dist/`.
