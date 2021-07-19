import {AppComponent} from './app.component';
import {TestBed} from '@angular/core/testing';
import {GameService} from "./game.service";
import {Game, Payout} from "./game";
import {HandComponent} from "./hand/hand.component";

describe('AppComponent', () => {
  const mockGameService = jasmine.createSpyObj(['createGame', 'getPayoutSchedule', 'isReadyToPlay', "getPayoutSchedule", 'getBet', 'getHand', 'getHandRank', 'getCredits', 'isReadyToBet']);
  const game: Game = {
    id: '123',
    credits: 1,
    hand: [],
    deckSize: 2,
    gameState: "testGameState",
    bet: 3,
    handRank: "testHandRank"
  };
  const payoutSchedule: Payout[] = [];

  let fixture: any;
  let component: any;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        AppComponent,
        HandComponent
      ],
      providers: [{provide: GameService, useValue: mockGameService}]
    }).compileComponents();

    fixture = TestBed.createComponent(AppComponent);
    component = fixture.debugElement.componentInstance;
    component.gameService.game = game;
  });

  it('should initialize game', () => {
    fixture.detectChanges();

    expect(mockGameService.createGame).toHaveBeenCalled();
  })

  describe('Game State', () => {
    it('should identify if the game is initialized', () => {
      mockGameService.isReadyToPlay.and.returnValue(false);

      expect(component.isGameInitialized()).toBeFalse();

      mockGameService.isReadyToPlay.and.returnValue(true);

      expect(component.isGameInitialized()).toBeTrue();
    });

    it('should return the payout schedule', () => {
      mockGameService.getPayoutSchedule.and.returnValue(payoutSchedule);

      expect(component.getPayoutSchedule()).toEqual(payoutSchedule);
    });

    it('should return the current credits', () => {
      mockGameService.getCredits.and.returnValue(25);

      expect(component.getCredits()).toEqual(25);
    });

    it('should return the current bet', () => {
      mockGameService.getBet.and.returnValue(3);

      expect(component.getBet()).toEqual(3);
    });

    it('should identify if ready to bet', () => {
      mockGameService.isReadyToBet.and.returnValue(true);

      expect(component.isReadyToBet()).toBeTrue();

      mockGameService.isReadyToBet.and.returnValue(false);

      expect(component.isReadyToBet()).toBeFalse();
    });
  });
});
