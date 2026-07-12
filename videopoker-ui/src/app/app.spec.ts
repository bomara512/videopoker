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
