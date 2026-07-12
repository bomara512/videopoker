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
