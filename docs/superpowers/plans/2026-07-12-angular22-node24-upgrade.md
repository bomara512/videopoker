# Angular 22 / Node 24 UI Upgrade Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Replace the Angular 11 / Node 12 `poker-ui` with a fresh Angular 22 workspace on Node 24 LTS, porting all app code, unit tests, and e2e tests to modern idioms with identical game behavior.

**Architecture:** Fresh `ng new` scaffold replaces `poker-ui/` contents in place. Three app units port 1:1 — `game.ts` (interfaces), `GameService` (signals instead of RxJS Subject), `App`/`Hand` standalone components (`inject()`, `computed()`, `output()`, `@if`/`@for`). Unit tests move Jasmine/Karma → Vitest; e2e moves Protractor → Playwright.

**Tech Stack:** Angular 22.0.x, Node 24 LTS (nvm), Bootstrap 5.3 (CSS only), Vitest (Angular unit-test builder), Playwright.

**Spec:** `docs/superpowers/specs/2026-07-12-angular-node-upgrade-design.md`

## Global Constraints

- Node 24 LTS, pinned via `poker-ui/.nvmrc`. Every npm/ng/npx command in this plan must run with Node 24 active: `export PATH="$HOME/.nvm/versions/node/$(ls "$HOME/.nvm/versions/node" | grep '^v24' | sort -V | tail -1)/bin:$PATH"`.
- Angular 22.0.x. Latest stable as of 2026-07-12.
- All work on branch `upgrade/angular22-node24`. Never push. Commit messages end with `Co-Authored-By: Claude Fable 5 <noreply@anthropic.com>`.
- Behavior preservation (from spec): bet cycle 1→5 wraps to 1; Bet Max sets 5; holds are 0-based indexes, reset on every game update; card images `assets/cards/{RANK}_{SUIT}.png`; held card CSS class `held-playing-card`; hand rank shown only in `READY_TO_DEAL`; API base URL hardcoded `http://localhost:8080`.
- CSS hook classes used by e2e must survive the port: `game`, `payout`, `hand`, `bestHand`, `dealButton`, `drawButton`, `holdButton`, `betOneButton`, `betMaxButton`.
- **One deliberate bugfix** (approved deviation): today `App.holds` goes stale after a draw (Hand resets its copy, App keeps the old one, so the next draw can silently send old holds). The port resets App's holds on every game update, same as Hand. Covered by a unit test in Task 4.
- The working directory for all commands is `poker-ui/` unless a path says otherwise.

---

### Task 1: Node 24 toolchain + fresh Angular 22 scaffold replacing poker-ui in place

**Files:**
- Replace: entire contents of `poker-ui/` with `ng new` output
- Create: `poker-ui/.nvmrc`
- Preserve: `src/assets/cards/*.png` (55 files) → `public/assets/cards/`, `src/favicon.ico` → `public/favicon.ico`
- Delete (via replacement): `e2e/` (Protractor), `tslint.json`, `karma.conf.js`, `src/test.ts`, `src/polyfills.ts`, old `angular.json`/tsconfigs/`package.json`/`package-lock.json`

**Interfaces:**
- Consumes: nothing.
- Produces: a building, testing Angular 22 workspace with Bootstrap 5.3 CSS wired in `angular.json` styles; card images served at `/assets/cards/{RANK}_{SUIT}.png`; scaffold default component `App` in `src/app/app.ts` (replaced in Task 4).

- [ ] **Step 1: Create the branch**

```bash
cd /Users/bomara/workspace/dev/poker-ai-refresh/poker
git checkout -b upgrade/angular22-node24
```

- [ ] **Step 2: Install and activate Node 24**

```bash
export NVM_DIR="$HOME/.nvm" && . "$NVM_DIR/nvm.sh" && nvm install 24
export PATH="$HOME/.nvm/versions/node/$(ls "$HOME/.nvm/versions/node" | grep '^v24' | sort -V | tail -1)/bin:$PATH"
node --version   # Expected: v24.x.x
```

- [ ] **Step 3: Scaffold Angular 22 in the scratchpad**

```bash
cd "$SCRATCHPAD"   # the session scratchpad dir; any temp dir outside the repo works
npx -y @angular/cli@22 new poker-ui --style css --routing false --ssr false --zoneless --skip-git --package-manager npm --defaults
```

Expected: scaffold completes, `npm install` runs. Verify the pieces we rely on:

```bash
node -p "require('./poker-ui/package.json').dependencies['@angular/core']"   # Expected: ^22.x
grep -o '"builder": "[^"]*unit-test[^"]*"' poker-ui/angular.json             # Expected: @angular/build:unit-test (Vitest builder)
ls poker-ui/public                                                            # Expected: favicon.ico
```

If `ng new` prompts or flags differ (flag names occasionally change between majors), adapt but do not proceed without: css styles, no routing, no SSR, zoneless, Vitest unit-test builder. If the scaffold generated Karma instead of Vitest, stop and re-scaffold with the test-runner flag for v22 (`--test-runner vitest` or check `ng new --help`).

- [ ] **Step 4: Preserve assets, replace poker-ui contents**

```bash
cd /Users/bomara/workspace/dev/poker-ai-refresh/poker
mkdir -p "$SCRATCHPAD/keep" && cp -R poker-ui/src/assets/cards "$SCRATCHPAD/keep/cards" && cp poker-ui/src/favicon.ico "$SCRATCHPAD/keep/favicon.ico"
find poker-ui -mindepth 1 -maxdepth 1 -exec rm -rf {} +
cp -R "$SCRATCHPAD"/poker-ui/. poker-ui/
mkdir -p poker-ui/public/assets && cp -R "$SCRATCHPAD/keep/cards" poker-ui/public/assets/cards
cp "$SCRATCHPAD/keep/favicon.ico" poker-ui/public/favicon.ico
echo "24" > poker-ui/.nvmrc
ls poker-ui/public/assets/cards | wc -l   # Expected: 55
```

- [ ] **Step 5: Add Bootstrap 5.3 CSS**

```bash
cd poker-ui && npm install bootstrap@5
```

In `angular.json`, add Bootstrap to the build styles array (project → architect → build → options → styles):

```json
"styles": [
  "node_modules/bootstrap/dist/css/bootstrap.min.css",
  "src/styles.css"
]
```

Also apply the same styles array to the `test` target if it has its own styles option. Bootstrap JS is deliberately NOT added: the templates use no `data-bs-*` behaviors (verified against the old templates; the old `scripts` entry was dead weight).

- [ ] **Step 6: Set the page title**

In `src/index.html`, set `<title>Poker</title>` (old title was `PokerUi`; keep everything else the scaffold generated).

- [ ] **Step 7: Verify build and default test pass**

```bash
npm run build    # Expected: build completes, no errors
npm test         # Expected: Vitest runs scaffold's app.spec.ts, all green
```

- [ ] **Step 8: Commit**

```bash
cd /Users/bomara/workspace/dev/poker-ai-refresh/poker
git add -A poker-ui
git commit -m "Replace Angular 11 workspace with fresh Angular 22 scaffold

Fresh ng new (standalone, zoneless, Vitest) on Node 24 LTS (.nvmrc).
Card assets and favicon carried into public/. Bootstrap 5.3 CSS wired.
Protractor/tslint/Karma tooling deleted. App code port follows.

Co-Authored-By: Claude Fable 5 <noreply@anthropic.com>"
```

---

### Task 2: Port game model and GameService to signals (with Vitest spec)

**Files:**
- Create: `poker-ui/src/app/game.ts`
- Create: `poker-ui/src/app/game-service.ts`
- Test: `poker-ui/src/app/game-service.spec.ts`

**Interfaces:**
- Consumes: `provideHttpClient()` (wired into app config in Task 4; tests provide their own).
- Produces (used by Tasks 3–4):
  - `interface Card { suit: string; rank: string }`
  - `interface Game { id: string; deckSize: number; bet: number; credits: number; hand: Card[]; handRank: string | null; gameState: string }`
  - `interface Payout { hand: string; payouts: number[] }`
  - `class GameService` with `readonly game: WritableSignal<Game | null>`, `readonly payoutSchedule: WritableSignal<Payout[]>`, and `createGame(): void`, `deal(): void`, `draw(holds: number[]): void`, `bet(amount: number): void`.

- [ ] **Step 1: Write the model** (`src/app/game.ts`)

```typescript
export interface Card {
  suit: string;
  rank: string;
}

export interface Game {
  id: string;
  deckSize: number;
  bet: number;
  credits: number;
  hand: Card[];
  handRank: string | null;
  gameState: string;
}

export interface Payout {
  hand: string;
  payouts: number[];
}
```

- [ ] **Step 2: Write the failing spec** (`src/app/game-service.spec.ts`)

Port of the old Jasmine spec; same five behaviors, adapted to signals.

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

  it('draws new cards with holds', () => {
    service.game.set(game);

    service.draw([1, 2, 3]);

    const req = httpMock.expectOne('http://localhost:8080/game/123/draw?holds=1,2,3');
    expect(req.request.method).toEqual('PUT');
    req.flush(game);

    expect(service.game()).toEqual(game);
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
});
```

- [ ] **Step 3: Run the spec to verify it fails**

```bash
cd poker-ui && npm test
```

Expected: FAIL — cannot resolve `./game-service`.

- [ ] **Step 4: Implement GameService** (`src/app/game-service.ts`)

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

  createGame(): void {
    this.httpClient.post<Game>(`${REST_API_SERVER}/game`, null)
      .subscribe(game => this.game.set(game));
    this.httpClient.get<Payout[]>(`${REST_API_SERVER}/game/payout-schedule`)
      .subscribe(payouts => this.payoutSchedule.set(payouts));
  }

  deal(): void {
    this.httpClient.put<Game>(`${this.gameUrl()}/deal`, null)
      .subscribe(game => this.game.set(game));
  }

  draw(holds: number[]): void {
    this.httpClient.put<Game>(`${this.gameUrl()}/draw?holds=${holds.join(',')}`, null)
      .subscribe(game => this.game.set(game));
  }

  bet(amount: number): void {
    this.httpClient.put<Game>(`${this.gameUrl()}/bet?amount=${amount}`, null)
      .subscribe(game => this.game.set(game));
  }

  private gameUrl(): string {
    return `${REST_API_SERVER}/game/${this.game()!.id}`;
  }
}
```

- [ ] **Step 5: Run the spec to verify it passes**

```bash
npm test
```

Expected: PASS — 4 GameService tests green (scaffold app.spec still green).

- [ ] **Step 6: Commit**

```bash
git add src/app/game.ts src/app/game-service.ts src/app/game-service.spec.ts
git commit -m "Port game model and GameService to signals

Co-Authored-By: Claude Fable 5 <noreply@anthropic.com>"
```

---

### Task 3: Port Hand component

**Files:**
- Create: `poker-ui/src/app/hand/hand.ts`
- Create: `poker-ui/src/app/hand/hand.html`
- Create: `poker-ui/src/app/hand/hand.css`
- Test: `poker-ui/src/app/hand/hand.spec.ts`

**Interfaces:**
- Consumes: `GameService.game` signal (Task 2).
- Produces (used by Task 4): standalone component `Hand`, selector `app-hand`, with `readonly holdsChanged: OutputEmitterRef<number[]>` and public `readonly holds: WritableSignal<number[]>`, `toggleHold(index: number): void`, `isHeld(index: number): boolean`.

- [ ] **Step 1: Write the failing spec** (`src/app/hand/hand.spec.ts`)

```typescript
import {ComponentFixture, TestBed} from '@angular/core/testing';
import {provideZonelessChangeDetection, signal} from '@angular/core';
import {vi} from 'vitest';
import {Hand} from './hand';
import {GameService} from '../game-service';
import {Game} from '../game';

describe('Hand', () => {
  let component: Hand;
  let fixture: ComponentFixture<Hand>;
  const mockGame = signal<Game | null>(null);
  const mockGameService = {game: mockGame};

  beforeEach(async () => {
    mockGame.set(null);
    await TestBed.configureTestingModule({
      imports: [Hand],
      providers: [
        provideZonelessChangeDetection(),
        {provide: GameService, useValue: mockGameService},
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(Hand);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('renders the hand from game updates and resets holds', async () => {
    component.holds.set([1, 2, 3]);

    mockGame.set({
      id: '123',
      credits: 1,
      hand: [{rank: 'TWO', suit: 'CLUB'}],
      deckSize: 2,
      gameState: 'READY_TO_DRAW',
      bet: 3,
      handRank: 'FLUSH',
    });
    await fixture.whenStable();

    const img: HTMLImageElement = fixture.nativeElement.querySelector('.hand img');
    expect(img.getAttribute('src')).toEqual('assets/cards/TWO_CLUB.png');
    expect(component.holds()).toEqual([]);
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

  it('identifies whether a card is held', () => {
    component.holds.set([1, 2, 3]);

    expect(component.isHeld(0)).toBe(false);
    expect(component.isHeld(4)).toBe(false);
    expect(component.isHeld(1)).toBe(true);
    expect(component.isHeld(2)).toBe(true);
    expect(component.isHeld(3)).toBe(true);
  });

  it('toggles holds when selected', () => {
    component.toggleHold(0);
    component.toggleHold(2);
    component.toggleHold(4);
    expect(component.holds()).toEqual([0, 2, 4]);

    component.toggleHold(2);
    expect(component.holds()).toEqual([0, 4]);
  });

  it('emits hold change events', () => {
    const emitted = vi.fn();
    component.holdsChanged.subscribe(emitted);

    component.toggleHold(0);
    expect(emitted).toHaveBeenCalledWith([0]);

    component.toggleHold(2);
    expect(emitted).toHaveBeenCalledWith([0, 2]);

    component.toggleHold(2);
    expect(emitted).toHaveBeenCalledWith([0]);
  });
});
```

- [ ] **Step 2: Run to verify it fails**

```bash
npm test
```

Expected: FAIL — cannot resolve `./hand`.

- [ ] **Step 3: Implement the component**

`src/app/hand/hand.ts`:

```typescript
import {Component, computed, effect, inject, output, signal} from '@angular/core';
import {GameService} from '../game-service';

@Component({
  selector: 'app-hand',
  templateUrl: './hand.html',
  styleUrl: './hand.css',
})
export class Hand {
  private readonly gameService = inject(GameService);

  readonly holdsChanged = output<number[]>();
  readonly holds = signal<number[]>([]);

  protected readonly hand = computed(() => this.gameService.game()?.hand ?? []);
  protected readonly handRank = computed(() => this.gameService.game()?.handRank);
  protected readonly readyToDeal = computed(() => this.gameService.game()?.gameState === 'READY_TO_DEAL');

  constructor() {
    effect(() => {
      this.gameService.game();
      this.holds.set([]);
    });
  }

  toggleHold(index: number): void {
    if (this.isHeld(index)) {
      this.holds.update(holds => holds.filter(i => i !== index));
    } else {
      this.holds.update(holds => [...holds, index]);
    }
    this.holdsChanged.emit(this.holds());
  }

  isHeld(index: number): boolean {
    return this.holds().includes(index);
  }
}
```

`src/app/hand/hand.html`:

```html
<div class="hand">
  <table>
    <tr>
      @for (card of hand(); track $index) {
        <td>
          <img src="assets/cards/{{card.rank}}_{{card.suit}}.png"
               class="{{isHeld($index) ? 'held-playing-card' : 'playing-card'}}"
               alt="{{card.rank}}_{{card.suit}}"/>
        </td>
      }
    </tr>
    @if (!readyToDeal()) {
      <tr>
        @for (card of hand(); track $index) {
          <td>
            <button class="btn btn-outline-dark m-2 holdButton" (click)="toggleHold($index)">Hold</button>
          </td>
        }
      </tr>
    }
  </table>
  @if (readyToDeal()) {
    <div class="bestHand">{{handRank()}}</div>
  }
</div>
```

`src/app/hand/hand.css` (carried over verbatim from the old `hand.component.css`):

```css
.playing-card {
  width: 100px;
  border: white medium solid;
  border-radius: 10px
}

.held-playing-card {
  width: 100px;
  border: #ffc107 medium solid;
  border-radius: 10px
}
```

- [ ] **Step 4: Run to verify it passes**

```bash
npm test
```

Expected: PASS — Hand suite green (5 tests), earlier suites still green.

- [ ] **Step 5: Commit**

```bash
git add src/app/hand
git commit -m "Port Hand component to standalone with signals

Co-Authored-By: Claude Fable 5 <noreply@anthropic.com>"
```

---

### Task 4: Port App root component and bootstrap wiring

**Files:**
- Modify: `poker-ui/src/app/app.ts` (replace scaffold content)
- Modify: `poker-ui/src/app/app.html` (replace scaffold content)
- Modify: `poker-ui/src/app/app.css` (empty, like the old `app.component.css`)
- Modify: `poker-ui/src/app/app.config.ts` (add `provideHttpClient()`)
- Test: `poker-ui/src/app/app.spec.ts` (replace scaffold spec)

**Interfaces:**
- Consumes: `GameService` signals + methods (Task 2); `Hand` component with `holdsChanged` output (Task 3).
- Produces: bootstrappable root `App` (scaffold `main.ts` already bootstraps it; unchanged).

- [ ] **Step 1: Write the failing spec** (replace `src/app/app.spec.ts`)

```typescript
import {TestBed, ComponentFixture} from '@angular/core/testing';
import {provideZonelessChangeDetection, signal} from '@angular/core';
import {vi} from 'vitest';
import {App} from './app';
import {GameService} from './game-service';
import {Game, Payout} from './game';

describe('App', () => {
  const game: Game = {
    id: '123', credits: 1, hand: [], deckSize: 2,
    gameState: 'READY_TO_DEAL', bet: 3, handRank: 'testHandRank',
  };
  const payoutSchedule: Payout[] = [{hand: 'FLUSH', payouts: [6, 12, 18, 24, 30]}];

  const mockGame = signal<Game | null>(null);
  const mockPayouts = signal<Payout[]>([]);
  const mockGameService = {
    game: mockGame,
    payoutSchedule: mockPayouts,
    createGame: vi.fn(),
    deal: vi.fn(),
    draw: vi.fn(),
    bet: vi.fn(),
  };

  let fixture: ComponentFixture<App>;
  let component: App;

  beforeEach(async () => {
    vi.clearAllMocks();
    mockGame.set(null);
    mockPayouts.set([]);

    await TestBed.configureTestingModule({
      imports: [App],
      providers: [
        provideZonelessChangeDetection(),
        {provide: GameService, useValue: mockGameService},
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(App);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('initializes the game on startup', () => {
    expect(mockGameService.createGame).toHaveBeenCalled();
  });

  it('hides the table until the game is initialized', async () => {
    expect(fixture.nativeElement.querySelector('.game')).toBeNull();

    mockGame.set(game);
    mockPayouts.set(payoutSchedule);
    await fixture.whenStable();

    expect(fixture.nativeElement.querySelector('.game h1').textContent).toEqual('Total Credits: 1');
    expect(fixture.nativeElement.querySelector('.game h2').textContent).toEqual('Bet: 3');
    expect(fixture.nativeElement.querySelectorAll('.payout tbody tr').length).toEqual(1);
  });

  it('shows deal/bet buttons when ready to deal and draw button when not', async () => {
    mockGame.set(game);
    await fixture.whenStable();
    expect(fixture.nativeElement.querySelector('.dealButton')).not.toBeNull();
    expect(fixture.nativeElement.querySelector('.drawButton')).toBeNull();

    mockGame.set({...game, gameState: 'READY_TO_DRAW'});
    await fixture.whenStable();
    expect(fixture.nativeElement.querySelector('.dealButton')).toBeNull();
    expect(fixture.nativeElement.querySelector('.drawButton')).not.toBeNull();
  });

  describe('betOne', () => {
    it('increases bet by one', async () => {
      mockGame.set({...game, bet: 3});
      await fixture.whenStable();
      component.betOne();
      expect(mockGameService.bet).toHaveBeenCalledWith(4);
    });

    it('rolls maximum bet back to one', async () => {
      mockGame.set({...game, bet: 5});
      await fixture.whenStable();
      component.betOne();
      expect(mockGameService.bet).toHaveBeenCalledWith(1);
    });
  });

  describe('betMax', () => {
    it.each([1, 3, 5])('sets bet to max from %s', async (bet: number) => {
      mockGame.set({...game, bet});
      await fixture.whenStable();
      component.betMax();
      expect(mockGameService.bet).toHaveBeenCalledWith(5);
    });
  });

  it('deals via the game service', () => {
    component.deal();
    expect(mockGameService.deal).toHaveBeenCalled();
  });

  it('draws with the current holds', () => {
    component.holdsChangedHandler([0, 2, 4]);
    component.draw();
    expect(mockGameService.draw).toHaveBeenCalledWith([0, 2, 4]);
  });

  it('resets holds on every game update (stale-holds bugfix)', async () => {
    component.holdsChangedHandler([0, 2, 4]);

    mockGame.set({...game});
    await fixture.whenStable();

    component.draw();
    expect(mockGameService.draw).toHaveBeenCalledWith([]);
  });
});
```

- [ ] **Step 2: Run to verify it fails**

```bash
npm test
```

Expected: FAIL — scaffold `App` has none of these members/templates.

- [ ] **Step 3: Implement**

`src/app/app.config.ts` — add HTTP client to the scaffold-generated providers list:

```typescript
import {provideHttpClient} from '@angular/common/http';
```

and inside `providers: [...]` add `provideHttpClient()`. Keep all scaffold-generated providers (zoneless change detection, error listeners) as they are.

`src/app/app.ts`:

```typescript
import {Component, computed, effect, inject, OnInit} from '@angular/core';
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

  private holds: number[] = [];

  constructor() {
    // Reset alongside Hand's own reset so a later draw can't send stale holds.
    effect(() => {
      this.gameService.game();
      this.holds = [];
    });
  }

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
    this.gameService.draw(this.holds);
  }

  holdsChangedHandler(holds: number[]): void {
    this.holds = holds;
  }
}
```

`src/app/app.html` (replace scaffold placeholder entirely with this):

```html
@if (isGameInitialized()) {
  <div class="container game">
    <div class="text-center"><h1>Total Credits: {{credits()}}</h1></div>
    <div class="text-center"><h2>Bet: {{bet()}}</h2></div>
    <div class="row payout">
      <table class="table table-sm table-bordered table-striped">
        <thead>
        <tr>
          <th></th>
          <th>1</th>
          <th>2</th>
          <th>3</th>
          <th>4</th>
          <th>5</th>
        </tr>
        </thead>
        <tbody>
          @for (payout of payoutSchedule(); track payout.hand) {
            <tr>
              <td>{{payout.hand}}</td>
              @for (amount of payout.payouts; track $index) {
                <td>{{amount}}</td>
              }
            </tr>
          }
        </tbody>
      </table>
    </div>
    <app-hand (holdsChanged)="holdsChangedHandler($event)"></app-hand>
    @if (readyToDeal()) {
      <div class="row row-cols-sm-6 deal">
        <button class="btn btn-dark m-2 betOneButton" (click)="betOne()">Bet 1</button>
        <button class="btn btn-dark m-2 betMaxButton" (click)="betMax()">Bet Max</button>
        <button class="btn btn-success m-2 dealButton" (click)="deal()">Deal</button>
      </div>
    } @else {
      <div class="row row-cols-sm-6 deal">
        <button class="btn btn-success m-2 drawButton" (click)="draw()">Draw</button>
      </div>
    }
  </div>
}
```

`src/app/app.css`: replace scaffold content with an empty file (parity with old `app.component.css`).

- [ ] **Step 4: Run all tests, verify green**

```bash
npm test
```

Expected: PASS — App (11 tests), Hand (5), GameService (4).

- [ ] **Step 5: Build and smoke-check**

```bash
npm run build
```

Expected: success. Optionally serve against a running backend for a visual check — full e2e comes next task.

- [ ] **Step 6: Commit**

```bash
git add src/app/app.ts src/app/app.html src/app/app.css src/app/app.spec.ts src/app/app.config.ts
git commit -m "Port App root component; fix stale-holds-after-draw bug

App now resets its holds on every game update, mirroring Hand, so a
draw after a fresh deal can no longer send the previous round's holds.

Co-Authored-By: Claude Fable 5 <noreply@anthropic.com>"
```

---

### Task 5: Playwright e2e suite

**Files:**
- Create: `poker-ui/playwright.config.ts`
- Create: `poker-ui/e2e/poker.spec.ts`
- Modify: `poker-ui/package.json` (add `e2e` script, Playwright dev dep)
- Modify: `poker-ui/.gitignore` (add `/playwright-report/`, `/test-results/`)

**Interfaces:**
- Consumes: the running app (Task 4) and the real backend at `http://localhost:8080` (docker Redis + bootRun).
- Produces: `npm run e2e` — the ported Protractor game-flow suite on Playwright.

- [ ] **Step 1: Install Playwright**

```bash
cd poker-ui
npm install -D @playwright/test
npx playwright install chromium
```

- [ ] **Step 2: Write `playwright.config.ts`**

```typescript
import {defineConfig} from '@playwright/test';

export default defineConfig({
  testDir: './e2e',
  fullyParallel: false,
  workers: 1,
  use: {
    baseURL: 'http://localhost:4200',
  },
  webServer: {
    command: 'npm start',
    url: 'http://localhost:4200',
    reuseExistingServer: true,
    timeout: 120_000,
  },
});
```

- [ ] **Step 3: Write the e2e suite** (`e2e/poker.spec.ts`)

Faithful port of the Protractor flow: one browser page across the whole serial suite, state accumulating New Game → Deal → Draw. Requires the backend (see Step 4).

```typescript
import {test, expect, Page} from '@playwright/test';

test.describe.configure({mode: 'serial'});

test.describe('Poker', () => {
  let page: Page;
  const consoleErrors: string[] = [];

  const creditsText = () => page.locator('.game h1');
  const betText = () => page.locator('.game h2');
  const handImages = () => page.locator('.hand tr td img');
  const holdButtons = () => page.locator('.holdButton');
  const dealButton = () => page.locator('.dealButton');
  const drawButton = () => page.locator('.drawButton');
  const bestHand = () => page.locator('.bestHand');

  const handSrcs = async () => handImages().evaluateAll(
    imgs => imgs.map(img => img.getAttribute('src')));

  test.beforeAll(async ({browser}) => {
    page = await browser.newPage();
    page.on('console', msg => {
      if (msg.type() === 'error') consoleErrors.push(msg.text());
    });
    await page.goto('/');
  });

  test.afterEach(() => {
    expect(consoleErrors).toEqual([]);
  });

  test.describe('New Game', () => {
    test('displays credits', async () => {
      await expect(creditsText()).toHaveText('Total Credits: 50');
    });

    test('displays default current bet', async () => {
      await expect(betText()).toHaveText('Bet: 1');
    });

    test('displays payout schedule', async () => {
      await expect(page.locator('.payout table tbody tr')).toHaveCount(9);

      const royalFlushRow = page.locator('.payout table tbody tr', {hasText: 'ROYAL_FLUSH'});
      await expect(royalFlushRow.locator('td')).toHaveText(['ROYAL_FLUSH', '250', '500', '750', '1000', '4000']);

      const jacksRow = page.locator('.payout table tbody tr', {hasText: 'JACKS_OR_BETTER'});
      await expect(jacksRow.locator('td')).toHaveText(['JACKS_OR_BETTER', '1', '2', '3', '4', '5']);
    });

    test('does not display undealt hand', async () => {
      await expect(handImages()).toHaveCount(0);
    });

    test('lets user deal', async () => {
      await expect(dealButton()).toBeVisible();
      await expect(drawButton()).toHaveCount(0);
    });
  });

  test.describe('Deal', () => {
    test('does not allow user to hold cards', async () => {
      await expect(holdButtons()).toHaveCount(0);
    });

    test('does not indicate best hand', async () => {
      await expect(bestHand()).toHaveText('');
    });

    test('allows user to increase current bet by 1', async () => {
      await page.locator('.betOneButton').click();
      await expect(betText()).toHaveText('Bet: 2');
    });

    test('allows user to increase current bet to max', async () => {
      await page.locator('.betMaxButton').click();
      await expect(betText()).toHaveText('Bet: 5');
    });

    test('displays fresh hand when deal button is clicked', async () => {
      await dealButton().click();
      await expect(handImages()).toHaveCount(5);
      for (const src of await handSrcs()) {
        expect(src).toMatch(/^assets\/cards\/[A-Z]+_[A-Z]+\.png$/);
      }
    });

    test('decreases credits by amount of bet', async () => {
      await expect(creditsText()).toHaveText('Total Credits: 45');
    });
  });

  test.describe('Draw', () => {
    let originalHand: (string | null)[];

    test('lets user draw new cards', async () => {
      await expect(dealButton()).toHaveCount(0);
      await expect(drawButton()).toBeVisible();
    });

    test('allows user to hold cards', async () => {
      await expect(holdButtons()).toHaveCount(5);
      originalHand = await handSrcs();
      await holdButtons().nth(1).click();
      await holdButtons().nth(3).click();
    });

    test('displays new hand with held cards', async () => {
      await drawButton().click();
      await expect(drawButton()).toHaveCount(0);

      const newHand = await handSrcs();
      expect(newHand[0]).not.toEqual(originalHand[0]);
      expect(newHand[1]).toEqual(originalHand[1]);
      expect(newHand[2]).not.toEqual(originalHand[2]);
      expect(newHand[3]).toEqual(originalHand[3]);
      expect(newHand[4]).not.toEqual(originalHand[4]);
    });

    test('indicates best hand out of drawn cards', async () => {
      await expect(bestHand()).toBeVisible();
    });

    test('lets user deal new hand', async () => {
      await expect(dealButton()).toBeVisible();
      await expect(drawButton()).toHaveCount(0);
    });
  });
});
```

Add to `package.json` scripts: `"e2e": "playwright test"`.

- [ ] **Step 4: Start the backend and run e2e**

```bash
cd /Users/bomara/workspace/dev/poker-ai-refresh/poker
docker compose -f poker-api/docker-compose.yml up -d
export JAVA_HOME=~/Library/Java/JavaVirtualMachines/azul-13.0.14/Contents/Home
./gradlew :poker-api:bootRun   # run in background; wait for "Started PokerApplication"
```

Then:

```bash
cd poker-ui && npm run e2e
```

Expected: 16 tests pass. Each run needs a fresh page load (the suite creates its own game via `page.goto`), so re-runs are safe. Stop bootRun and `docker compose down` afterwards if you started them.

- [ ] **Step 5: Commit**

```bash
git add playwright.config.ts e2e package.json package-lock.json .gitignore
git commit -m "Port Protractor e2e suite to Playwright

Co-Authored-By: Claude Fable 5 <noreply@anthropic.com>"
```

---

### Task 6: Documentation and final verification

**Files:**
- Modify: `poker-ui/README.md`
- Modify: `README.md` (root)
- Modify: `CLAUDE.md`

**Interfaces:**
- Consumes: everything above, finished and green.
- Produces: docs matching the new reality.

- [ ] **Step 1: Rewrite `poker-ui/README.md`**

```markdown
# PokerUi

Angular 22 frontend for Poker. Requires Node 24 (`.nvmrc` — run `nvm use`).

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
```

- [ ] **Step 2: Update root `README.md`**

Replace the UI sentence added earlier:

```markdown
For the web UI, see [poker-ui/README.md](poker-ui/README.md) (`npm start` serves it at http://localhost:4200).
```

with:

```markdown
For the web UI (Angular 22, Node 24 — `nvm use` picks it up from `.nvmrc`), see
[poker-ui/README.md](poker-ui/README.md); `npm start` serves it at http://localhost:4200.
```

- [ ] **Step 3: Update `CLAUDE.md`**

Update the Overview line for the UI module to `poker-ui — Angular 22 frontend (Node 24 via .nvmrc; built with npm, not Gradle)`.

Replace the "Frontend (from poker-ui/)" commands section with:

```markdown
### Frontend (from poker-ui/)

Requires Node 24: `nvm use` (reads `.nvmrc`).

```bash
npm start        # ng serve, http://localhost:4200
npm test         # Vitest unit tests; single file: ng test --include='**/game-service.spec.ts'
npm run e2e      # Playwright; starts ng serve itself, needs backend + Redis running
npm run build    # ng build
```
```

Also update the architecture note about the UI hardcoding `http://localhost:8080` if the file path it references changed (`game.service.ts` → `src/app/game-service.ts`), and mention state is held in `GameService` signals.

- [ ] **Step 4: Final full verification**

```bash
cd poker-ui
npm run build   # PASS
npm test        # PASS (20 unit tests)
npm run e2e     # PASS (16 tests, backend running as in Task 5 Step 4)
```

- [ ] **Step 5: Commit**

```bash
cd /Users/bomara/workspace/dev/poker-ai-refresh/poker
git add README.md CLAUDE.md poker-ui/README.md
git commit -m "Update docs for Angular 22 / Node 24 UI

Co-Authored-By: Claude Fable 5 <noreply@anthropic.com>"
```

---

## Completion

After Task 6, use superpowers:finishing-a-development-branch — run the full verification once more, then present merge/PR options to the user. **Never push without explicit permission.**
