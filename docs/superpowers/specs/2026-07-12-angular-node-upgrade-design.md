# videopoker-ui: Angular 22 + Node 24 Upgrade Design

**Date:** 2026-07-12
**Status:** Approved

## Goal

Replace the Angular 11 / Node 12 UI toolchain with current, maintained versions:
Angular 22 (latest stable, v22.0.x) on Node 24 LTS. Behavior of the game UI is
preserved exactly; the code is rewritten in current Angular idioms.

## Background

The UI was scaffolded with Angular CLI 11 in 2021 and runs only on Node 12. Its
toolchain is dead or deprecated end to end: tslint/codelyzer (dead), Protractor
(dead), Karma (deprecated). A 2023 Dependabot bump of `@angular-devkit/build-angular`
to v16 broke the build entirely and was pinned back to the Angular 11 line
(commit fcb36b6) as a stopgap. This upgrade is the real fix.

## Approach: fresh scaffold + port

An incremental `ng update` chain (11→22, eleven majors) was rejected: each hop
needs a different Node version to run the CLI and drags mandatory tooling
migrations along the way — all for ~250 lines of app code and ~300 lines of
specs. Instead:

1. Generate a fresh Angular 22 workspace (`ng new`: no routing, CSS, Vitest).
2. Replace the contents of `videopoker-ui/` with it in place (same path, git
   history preserved as modifications).
3. Port the app code and tests into modern idioms.

## Workspace

- **Node:** 24 LTS, pinned via `videopoker-ui/.nvmrc`.
- **Angular:** 22.0.x, standalone bootstrap (`bootstrapApplication` +
  `provideHttpClient()`), no NgModule, no routing.
- **Styling:** Bootstrap bumped to 5.3.x (npm dependency, as today). Existing
  global `styles.css` and component CSS carried over.
- **Assets carried over unchanged:** 52 card PNGs and favicon under `src/assets/`.
- **Deleted:** `e2e/` (Protractor), `tslint.json`, `karma.conf.js`,
  `src/test.ts`, `src/polyfills.ts`, old tsconfigs/`angular.json` (replaced by
  generated ones), `protractor`/`tslint`/`codelyzer`/`karma*`/`jasmine*`
  dependencies.

## Code port (behavior-preserving)

Same three units, one file each, modernized:

| Unit | Today | After |
|------|-------|-------|
| `game.ts` | classes with `!` fields | interfaces (`Game`, `Card`, `Payout`) |
| `game.service.ts` | RxJS `Subject` + `gameUpdate$` | `game` and `payoutSchedule` signals updated after each HTTP call |
| `app.component.ts` | NgModule component, subscribe in `ngOnInit` | standalone, `inject()`, `computed()` for `credits`/`bet`/`readyToDeal`/`isGameInitialized` |
| `hand/hand.component.ts` | NgModule component, `@Output()` EventEmitter | standalone, `output()` function, local `holds` cleared on game-signal change (matches today's reset-on-update) |

Templates convert `*ngIf`/`*ngFor` to `@if`/`@for`.

Preserved behavior, explicitly:

- Bet cycle: "Bet 1" increments 1→5 then wraps to 1; "Bet Max" sets 5.
- Holds are 0-based indexes emitted from HandComponent to AppComponent, passed
  to `draw`; holds reset on every game update.
- Card images resolved as `assets/cards/{RANK}_{SUIT}.png`; held cards get the
  `held-playing-card` CSS class.
- Payout table rendered from `/game/payout-schedule`; hand rank shown only when
  `READY_TO_DEAL`.
- API base URL stays hardcoded `http://localhost:8080` (backend CORS is open).
  Moving to a dev-server proxy or environment config is **out of scope**.

## Testing

- **Unit:** the three spec files (`app.component.spec.ts`,
  `game.service.spec.ts`, `hand.component.spec.ts`) port to Vitest on Angular's
  unit-test builder. Same TestBed structure; Jasmine spies become `vi` mocks;
  HTTP tested via `provideHttpClientTesting()`. All existing assertions carry
  over.
- **E2E:** the Protractor suite (new game, deal, draw, bet flows + payout
  schedule) is rewritten with `@playwright/test`. Playwright's `webServer`
  starts `ng serve`; the tests require the real backend + Redis running (same
  as today) — documented, not mocked.

## Success criteria

1. `npm run build` succeeds on Node 24.
2. `npm test` (Vitest) passes with all ported assertions.
3. Playwright e2e passes against the live backend (docker Redis + bootRun).
4. Manual smoke: game plays in the browser at localhost:4200.

## Delivery

- All work on a feature branch; nothing pushed without explicit permission.
- Docs updated: `videopoker-ui/README.md` (regenerated + customized), root
  `README.md` (Node 24 prerequisite for the UI), `CLAUDE.md` (new UI commands:
  Vitest, Playwright, `.nvmrc`).

## Future work (out of scope)

- Dev-server proxy / environment-based API URL.
- Angular upgrade cadence going forward (Dependabot config to avoid a repeat of
  the cross-major bump).
- Backend JDK/Gradle/Spring Boot upgrade (same staleness problem, separate project).
