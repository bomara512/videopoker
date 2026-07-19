# UI Follow-up Fixes Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Fix the two structural issues the Angular 22 upgrade's final review flagged for follow-up: holds state duplicated across `App`/`Hand`, and backend errors vanishing into the browser console.

**Architecture:** Move holds into `GameService` as the single source of truth (killing the output-plus-parallel-effects sync that caused the original stale-holds bug), then add an `errorMessage` signal fed by HTTP error callbacks and rendered as a dismissible Bootstrap alert in `App`.

**Tech Stack:** Angular 22 (signals, zoneless, standalone), Vitest, Playwright. Node 24.

**Provenance:** Final whole-branch review of `docs/superpowers/plans/2026-07-12-angular22-node24-upgrade.md` (merged at `c624ae7`), Minor finding #6 (dual holds state) and Recommendations (HTTP error surfacing). Error UX decision (user-approved): dismissible Bootstrap alert, auto-cleared on next successful action.

## Global Constraints

- Node 24 must be active for every npm/ng command: `export PATH="$HOME/.nvm/versions/node/$(ls "$HOME/.nvm/versions/node" | grep '^v24' | sort -V | tail -1)/bin:$PATH"`.
- Game behavior visible to the player is unchanged except the new error alert. The 16-test Playwright suite must pass unmodified after each task (backend + Redis required: `docker compose -f poker-api/docker-compose.yml up -d`, then `JAVA_HOME=~/Library/Java/JavaVirtualMachines/azul-13.0.14/Contents/Home ./gradlew :poker-api:bootRun` in background until "Started PokerApplication").
- All work on branch `fix/ui-followups` off master. Never push. Commit messages end with `Co-Authored-By: Claude Fable 5 <noreply@anthropic.com>`.
- E2E CSS hook classes must survive: `game`, `payout`, `hand`, `bestHand`, `dealButton`, `drawButton`, `holdButton`, `betOneButton`, `betMaxButton`. New hook for the alert: `errorMessage`.
- Out of scope (do NOT touch): backend module, dev-server proxy/environment config, `gameUrl()` non-null guard, `handRank` type cosmetics — all triaged leave-as-is; backend stack upgrade is a separate future project.

---

### Task 1: Holds single source of truth in GameService

**Files:**
- Modify: `poker-ui/src/app/game-service.ts`
- Modify: `poker-ui/src/app/hand/hand.ts`
- Modify: `poker-ui/src/app/app.ts`
- Modify: `poker-ui/src/app/app.html` (one line)
- Test: `poker-ui/src/app/game-service.spec.ts`, `poker-ui/src/app/hand/hand.spec.ts`, `poker-ui/src/app/app.spec.ts`

**Interfaces:**
- Consumes: existing `GameService` (`game`/`payoutSchedule` signals; `createGame()`, `deal()`, `draw(holds: number[])`, `bet(amount)`), `Hand` (`holdsChanged` output, `holds` signal), `App` (`holdsChangedHandler`, private `holds` field, reset effect).
- Produces (Task 2 relies on this): `GameService` gains `readonly holds: WritableSignal<number[]>`, `toggleHold(index: number): void`, and a private `updateGame(game: Game): void` used by every mutating HTTP callback; `draw()` takes NO arguments and reads `this.holds()`. `Hand` loses `holdsChanged`/local holds/effect; `App` loses its holds field/effect/`holdsChangedHandler`.

- [ ] **Step 1: Rewrite the GameService spec** — replace the whole of `src/app/game-service.spec.ts` with:

```typescript
import {TestBed} from '@angular/core/testing';
import {provideZonelessChangeDetection} from '@angular/core';
import {provideHttpClient} from '@angular/common/http';
import {HttpTestingController, provideHttpClientTesting} from '@angular/common/http/testing';
import {GameService} from './game-service';
import {Game, Payout} from './game';

const game: Game = {bet: 0, credits: 0, deckSize: 0, gameState: '', hand: [], handRank: '', id: '123'};
const payoutSchedule: Payout[] = [{hand: 'ROYAL_FLUSH', payouts: [250, 500, 750, 1000, 4000]}];

describe('GameService', () => {
  let service: GameService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideZonelessChangeDetection(), provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(GameService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('creates a game and loads the payout schedule', () => {
    service.createGame();

    const req = httpMock.expectOne('http://localhost:8080/game');
    expect(req.request.method).toEqual('POST');
    req.flush(game);

    const req2 = httpMock.expectOne('http://localhost:8080/game/payout-schedule');
    expect(req2.request.method).toEqual('GET');
    req2.flush(payoutSchedule);

    expect(service.game()).toEqual(game);
    expect(service.payoutSchedule()).toEqual(payoutSchedule);
  });

  it('deals a new hand', () => {
    service.game.set(game);

    service.deal();

    const req = httpMock.expectOne('http://localhost:8080/game/123/deal');
    expect(req.request.method).toEqual('PUT');
    const dealt: Game = {...game, gameState: 'READY_TO_DRAW'};
    req.flush(dealt);

    expect(service.game()).toEqual(dealt);
  });

  it('sets the current bet', () => {
    service.game.set(game);

    service.bet(3);

    const req = httpMock.expectOne('http://localhost:8080/game/123/bet?amount=3');
    expect(req.request.method).toEqual('PUT');
    const betGame: Game = {...game, bet: 3};
    req.flush(betGame);

    expect(service.game()).toEqual(betGame);
  });

  describe('holds', () => {
    it('toggles holds preserving selection order', () => {
      service.toggleHold(0);
      service.toggleHold(2);
      service.toggleHold(4);
      expect(service.holds()).toEqual([0, 2, 4]);

      service.toggleHold(2);
      expect(service.holds()).toEqual([0, 4]);
    });

    it('draws with the current holds', () => {
      service.game.set(game);
      service.toggleHold(1);
      service.toggleHold(3);

      service.draw();

      const req = httpMock.expectOne('http://localhost:8080/game/123/draw?holds=1,3');
      expect(req.request.method).toEqual('PUT');
      req.flush(game);
    });

    it('draws with no holds when none selected', () => {
      service.game.set(game);

      service.draw();

      const req = httpMock.expectOne('http://localhost:8080/game/123/draw?holds=');
      req.flush(game);
    });

    it('resets holds on every game update (stale-holds regression)', () => {
      service.game.set(game);
      service.toggleHold(0);
      service.toggleHold(2);

      service.deal();
      httpMock.expectOne('http://localhost:8080/game/123/deal').flush(game);

      expect(service.holds()).toEqual([]);
    });
  });
});
```

- [ ] **Step 2: Run to verify it fails**

Run: `cd poker-ui && npm test`
Expected: FAIL — `toggleHold` does not exist; `draw()` expects an argument.

- [ ] **Step 3: Implement GameService changes** — replace `src/app/game-service.ts` with:

```typescript
import {inject, Injectable, signal} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Game, Payout} from './game';

const REST_API_SERVER = 'http://localhost:8080';

@Injectable({providedIn: 'root'})
export class GameService {
  private readonly httpClient = inject(HttpClient);

  readonly game = signal<Game | null>(null);
  readonly payoutSchedule = signal<Payout[]>([]);
  readonly holds = signal<number[]>([]);

  createGame(): void {
    this.httpClient.post<Game>(`${REST_API_SERVER}/game`, null)
      .subscribe(game => this.updateGame(game));
    this.httpClient.get<Payout[]>(`${REST_API_SERVER}/game/payout-schedule`)
      .subscribe(payouts => this.payoutSchedule.set(payouts));
  }

  deal(): void {
    this.httpClient.put<Game>(`${this.gameUrl()}/deal`, null)
      .subscribe(game => this.updateGame(game));
  }

  draw(): void {
    this.httpClient.put<Game>(`${this.gameUrl()}/draw?holds=${this.holds().join(',')}`, null)
      .subscribe(game => this.updateGame(game));
  }

  bet(amount: number): void {
    this.httpClient.put<Game>(`${this.gameUrl()}/bet?amount=${amount}`, null)
      .subscribe(game => this.updateGame(game));
  }

  toggleHold(index: number): void {
    if (this.holds().includes(index)) {
      this.holds.update(holds => holds.filter(i => i !== index));
    } else {
      this.holds.update(holds => [...holds, index]);
    }
  }

  private updateGame(game: Game): void {
    this.game.set(game);
    this.holds.set([]);
  }

  private gameUrl(): string {
    return `${REST_API_SERVER}/game/${this.game()!.id}`;
  }
}
```

- [ ] **Step 4: Run service spec to verify it passes**

Run: `npm test`
Expected: GameService suite passes (7 tests); Hand and App suites now FAIL (they mock the old interface) — that is the next steps' work.

- [ ] **Step 5: Rewrite the Hand spec** — replace `src/app/hand/hand.spec.ts` with:

```typescript
import {ComponentFixture, TestBed} from '@angular/core/testing';
import {provideZonelessChangeDetection, signal} from '@angular/core';
import {vi} from 'vitest';
import {Hand} from './hand';
import {GameService} from '../game-service';
import {Game} from '../game';

describe('Hand', () => {
  let fixture: ComponentFixture<Hand>;
  const mockGame = signal<Game | null>(null);
  const mockHolds = signal<number[]>([]);
  const mockGameService = {game: mockGame, holds: mockHolds, toggleHold: vi.fn()};

  beforeEach(async () => {
    vi.clearAllMocks();
    mockGame.set(null);
    mockHolds.set([]);
    await TestBed.configureTestingModule({
      imports: [Hand],
      providers: [
        provideZonelessChangeDetection(),
        {provide: GameService, useValue: mockGameService},
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(Hand);
    fixture.detectChanges();
  });

  it('renders the hand from the game signal', async () => {
    mockGame.set({
      id: '123', credits: 1, hand: [{rank: 'TWO', suit: 'CLUB'}],
      deckSize: 2, gameState: 'READY_TO_DRAW', bet: 3, handRank: 'FLUSH',
    });
    await fixture.whenStable();

    const img: HTMLImageElement = fixture.nativeElement.querySelector('.hand img');
    expect(img.getAttribute('src')).toEqual('assets/cards/TWO_CLUB.png');
  });

  it('marks held cards using the service holds signal', async () => {
    mockGame.set({
      id: '123', credits: 1,
      hand: [{rank: 'TWO', suit: 'CLUB'}, {rank: 'THREE', suit: 'HEART'}],
      deckSize: 2, gameState: 'READY_TO_DRAW', bet: 3, handRank: null,
    });
    mockHolds.set([1]);
    await fixture.whenStable();

    const imgs: NodeListOf<HTMLImageElement> = fixture.nativeElement.querySelectorAll('.hand img');
    expect(imgs[0].className).toEqual('playing-card');
    expect(imgs[1].className).toEqual('held-playing-card');
  });

  it('delegates hold clicks to the service', async () => {
    mockGame.set({
      id: '123', credits: 1, hand: [{rank: 'TWO', suit: 'CLUB'}],
      deckSize: 2, gameState: 'READY_TO_DRAW', bet: 3, handRank: null,
    });
    await fixture.whenStable();

    (fixture.nativeElement.querySelector('.holdButton') as HTMLButtonElement).click();
    expect(mockGameService.toggleHold).toHaveBeenCalledWith(0);
  });

  it('shows the hand rank only when ready to deal', async () => {
    mockGame.set({
      id: '123', credits: 1, hand: [], deckSize: 2,
      gameState: 'READY_TO_DEAL', bet: 3, handRank: 'FLUSH',
    });
    await fixture.whenStable();
    expect(fixture.nativeElement.querySelector('.bestHand').textContent).toEqual('FLUSH');

    mockGame.set({
      id: '123', credits: 1, hand: [], deckSize: 2,
      gameState: 'READY_TO_DRAW', bet: 3, handRank: 'FLUSH',
    });
    await fixture.whenStable();
    expect(fixture.nativeElement.querySelector('.bestHand')).toBeNull();
  });
});
```

- [ ] **Step 6: Simplify Hand** — replace `src/app/hand/hand.ts` with (template `hand.html` and `hand.css` unchanged):

```typescript
import {Component, computed, inject} from '@angular/core';
import {GameService} from '../game-service';

@Component({
  selector: 'app-hand',
  templateUrl: './hand.html',
  styleUrl: './hand.css',
})
export class Hand {
  private readonly gameService = inject(GameService);

  protected readonly hand = computed(() => this.gameService.game()?.hand ?? []);
  protected readonly handRank = computed(() => this.gameService.game()?.handRank);
  protected readonly readyToDeal = computed(() => this.gameService.game()?.gameState === 'READY_TO_DEAL');

  protected toggleHold(index: number): void {
    this.gameService.toggleHold(index);
  }

  protected isHeld(index: number): boolean {
    return this.gameService.holds().includes(index);
  }
}
```

- [ ] **Step 7: Simplify App** — in `src/app/app.ts`: delete the `private holds` field, the constructor with its holds-reset effect, and `holdsChangedHandler`; change `draw()` to `this.gameService.draw();`; remove `effect` from the `@angular/core` import. Result:

```typescript
import {Component, computed, inject, OnInit} from '@angular/core';
import {GameService} from './game-service';
import {Hand} from './hand/hand';

@Component({
  selector: 'app-root',
  imports: [Hand],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App implements OnInit {
  private readonly gameService = inject(GameService);

  protected readonly payoutSchedule = this.gameService.payoutSchedule;
  protected readonly isGameInitialized = computed(() => this.gameService.game() !== null);
  protected readonly readyToDeal = computed(() => this.gameService.game()?.gameState === 'READY_TO_DEAL');
  protected readonly credits = computed(() => this.gameService.game()?.credits ?? 0);
  protected readonly bet = computed(() => this.gameService.game()?.bet ?? 0);

  ngOnInit(): void {
    this.gameService.createGame();
  }

  betOne(): void {
    this.gameService.bet(this.bet() === 5 ? 1 : this.bet() + 1);
  }

  betMax(): void {
    this.gameService.bet(5);
  }

  deal(): void {
    this.gameService.deal();
  }

  draw(): void {
    this.gameService.draw();
  }
}
```

In `src/app/app.html`, change `<app-hand (holdsChanged)="holdsChangedHandler($event)"></app-hand>` to `<app-hand></app-hand>`.

- [ ] **Step 8: Update the App spec** — in `src/app/app.spec.ts`: add `holds: signal<number[]>([])` to `mockGameService` (reset it in `beforeEach`); change `draw: vi.fn()` expectations — replace the two holds-related tests ("draws with the current holds", "resets holds on every game update (stale-holds bugfix)") with one delegation test, since holds behavior now lives in (and is tested on) `GameService`:

```typescript
  it('draws via the game service', () => {
    component.draw();
    expect(mockGameService.draw).toHaveBeenCalledWith();
  });
```

Remove the now-nonexistent `holdsChangedHandler` usage everywhere in the spec.

- [ ] **Step 9: Full unit suite green**

Run: `npm test`
Expected: PASS — App (10), Hand (4), GameService (7): 21 tests.

- [ ] **Step 10: E2E against live backend (unchanged suite must pass)**

Backend up per Global Constraints, then: `npm run e2e`
Expected: 16/16 PASS — proving player-visible behavior is unchanged.

- [ ] **Step 11: Commit**

```bash
git add -u
git commit -m "Move holds state into GameService as single source of truth

Removes the App/Hand dual-holds sync (output + parallel reset effects)
that caused the original stale-holds bug class. draw() now reads holds
from the service.

Co-Authored-By: Claude Fable 5 <noreply@anthropic.com>"
```

---

### Task 2: Surface backend errors as a dismissible alert

**Files:**
- Modify: `poker-ui/src/app/game-service.ts`
- Modify: `poker-ui/src/app/app.ts`
- Modify: `poker-ui/src/app/app.html`
- Modify: `CLAUDE.md` (one line)
- Test: `poker-ui/src/app/game-service.spec.ts`, `poker-ui/src/app/app.spec.ts`

**Interfaces:**
- Consumes: Task 1's `GameService` (`updateGame` private helper, `game`/`holds` signals, `draw()` no-arg).
- Produces: `GameService.errorMessage: WritableSignal<string | null>` and `dismissError(): void`. Backend error bodies are `{message: string}` (Spring `GameErrorResponse`, HTTP 400).

- [ ] **Step 1: Add failing service tests** — append inside the top-level `describe('GameService', ...)` block of `src/app/game-service.spec.ts` (no new imports needed in the spec file):

```typescript
  describe('errors', () => {
    it('surfaces the backend error message', () => {
      service.game.set(game);

      service.deal();
      httpMock.expectOne('http://localhost:8080/game/123/deal')
        .flush({message: 'Not enough credits'}, {status: 400, statusText: 'Bad Request'});

      expect(service.errorMessage()).toEqual('Not enough credits');
    });

    it('falls back to a generic message when the error has no body message', () => {
      service.game.set(game);

      service.bet(3);
      httpMock.expectOne('http://localhost:8080/game/123/bet?amount=3')
        .flush(null, {status: 500, statusText: 'Server Error'});

      expect(service.errorMessage()).toEqual('Something went wrong talking to the server');
    });

    it('clears the error on the next successful action', () => {
      service.game.set(game);
      service.errorMessage.set('stale error');

      service.deal();
      httpMock.expectOne('http://localhost:8080/game/123/deal').flush(game);

      expect(service.errorMessage()).toBeNull();
    });

    it('dismissError clears the message', () => {
      service.errorMessage.set('boom');
      service.dismissError();
      expect(service.errorMessage()).toBeNull();
    });
  });
```

- [ ] **Step 2: Run to verify it fails**

Run: `npm test`
Expected: FAIL — `errorMessage` does not exist on GameService.

- [ ] **Step 3: Implement in GameService** — in `src/app/game-service.ts`:

Add import: `import {HttpClient, HttpErrorResponse} from '@angular/common/http';`

Add signal below `holds`:
```typescript
  readonly errorMessage = signal<string | null>(null);
```

Add a private handler and a dismiss method:
```typescript
  dismissError(): void {
    this.errorMessage.set(null);
  }

  private handleError(error: HttpErrorResponse): void {
    this.errorMessage.set(error.error?.message ?? 'Something went wrong talking to the server');
  }
```

Change every game-mutating subscribe to an observer object, and clear the error in `updateGame`:
```typescript
  createGame(): void {
    this.httpClient.post<Game>(`${REST_API_SERVER}/game`, null)
      .subscribe({next: game => this.updateGame(game), error: err => this.handleError(err)});
    this.httpClient.get<Payout[]>(`${REST_API_SERVER}/game/payout-schedule`)
      .subscribe({next: payouts => this.payoutSchedule.set(payouts), error: err => this.handleError(err)});
  }

  deal(): void {
    this.httpClient.put<Game>(`${this.gameUrl()}/deal`, null)
      .subscribe({next: game => this.updateGame(game), error: err => this.handleError(err)});
  }

  draw(): void {
    this.httpClient.put<Game>(`${this.gameUrl()}/draw?holds=${this.holds().join(',')}`, null)
      .subscribe({next: game => this.updateGame(game), error: err => this.handleError(err)});
  }

  bet(amount: number): void {
    this.httpClient.put<Game>(`${this.gameUrl()}/bet?amount=${amount}`, null)
      .subscribe({next: game => this.updateGame(game), error: err => this.handleError(err)});
  }

  private updateGame(game: Game): void {
    this.game.set(game);
    this.holds.set([]);
    this.errorMessage.set(null);
  }
```

- [ ] **Step 4: Service tests green**

Run: `npm test`
Expected: GameService suite passes (11 tests).

- [ ] **Step 5: Add failing App tests** — append to the App spec's `describe('App', ...)`; add `errorMessage: signal<string | null>(null)` and `dismissError: vi.fn()` to `mockGameService`, resetting `errorMessage.set(null)` in `beforeEach`:

```typescript
  describe('error alert', () => {
    it('shows the backend error message', async () => {
      mockGameService.errorMessage.set('Not enough credits');
      await fixture.whenStable();

      expect(fixture.nativeElement.querySelector('.errorMessage').textContent)
        .toContain('Not enough credits');
    });

    it('is absent when there is no error', () => {
      expect(fixture.nativeElement.querySelector('.errorMessage')).toBeNull();
    });

    it('dismisses via the close button', async () => {
      mockGameService.errorMessage.set('boom');
      await fixture.whenStable();

      (fixture.nativeElement.querySelector('.errorMessage .btn-close') as HTMLButtonElement).click();
      expect(mockGameService.dismissError).toHaveBeenCalled();
    });
  });
```

- [ ] **Step 6: Run to verify it fails**

Run: `npm test`
Expected: FAIL — no `.errorMessage` element rendered.

- [ ] **Step 7: Implement in App** — in `src/app/app.ts` add:

```typescript
  protected readonly errorMessage = this.gameService.errorMessage;

  dismissError(): void {
    this.gameService.dismissError();
  }
```

In `src/app/app.html`, add ABOVE the `@if (isGameInitialized())` block (top of file, so createGame failures are visible even before the game area renders):

```html
@if (errorMessage()) {
  <div class="container">
    <div class="alert alert-danger alert-dismissible errorMessage" role="alert">
      {{errorMessage()}}
      <button type="button" class="btn-close" (click)="dismissError()" aria-label="Close"></button>
    </div>
  </div>
}
```

- [ ] **Step 8: Full unit suite green**

Run: `npm test`
Expected: PASS — App (13), Hand (4), GameService (11): 28 tests.

- [ ] **Step 9: E2E still green + build**

Backend up per Global Constraints, then: `npm run e2e` and `npm run build`
Expected: 16/16 PASS (no console-error regressions — handled errors no longer hit the console guard) and build success.

- [ ] **Step 10: Update CLAUDE.md** — in the sentence describing GameService signals, change `(game, payoutSchedule)` to `(game, payoutSchedule, holds, errorMessage)`.

- [ ] **Step 11: Commit**

```bash
git add -u
git commit -m "Surface backend errors as a dismissible alert

GameService captures HTTP failures in an errorMessage signal (backend
{message} body, generic fallback), cleared on the next successful
action or via the alert's close button.

Co-Authored-By: Claude Fable 5 <noreply@anthropic.com>"
```

---

## Completion

After Task 2, use superpowers:finishing-a-development-branch (verify suites, offer merge/PR options). **Never push without explicit permission.**
