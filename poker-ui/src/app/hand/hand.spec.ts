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
