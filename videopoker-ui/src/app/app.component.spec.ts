import {AppComponent} from './app.component';
import {TestBed} from '@angular/core/testing';
import {GameService} from "./game.service";
import {of} from "rxjs";
import {Game} from "./game";
import {HandComponent} from "./hand/hand.component";

describe('AppComponent', () => {
  const mockGameService = jasmine.createSpyObj(['createGame', 'getPayoutSchedule', 'deal', 'draw', 'bet']);
  const game: Game = {
    id: '123',
    credits: 1,
    hand: [],
    deckSize: 2,
    gameState: "testGameState",
    bet: 3,
    handRank: "testHandRank"
  };
  let fixture: any;
  let component: any;

  beforeEach(async () => {
    mockGameService.deal.and.returnValue(of(game));
    mockGameService.draw.and.returnValue(of(game));
    mockGameService.bet.and.returnValue(of(game));

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

    expect(mockGameService.getPayoutSchedule).toHaveBeenCalled();
    expect(mockGameService.createGame).toHaveBeenCalled();
  })

  describe('Game State', () => {
    it('should identify if the game is initialized', () => {
      component.gameService.game = null;

      expect(component.isGameInitialized()).toBeFalsy();

      component.gameService.game = game;

      expect(component.isGameInitialized()).toBeTruthy();
    });

    it('should return the current credits', () => {
      component.gameService.game.credits = 25;

      expect(component.getCredits()).toEqual(25);
    });

    it('should return the current bet', () => {
      component.gameService.game.bet = 3;

      expect(component.getBet()).toEqual(3);
    });

    it('should identify if ready to bet', () => {
      component.gameService.game.gameState = 'READY_TO_DEAL';

      expect(component.isReadyToBet()).toBeTrue();

      component.gameService.game.gameState = 'READY_TO_DRAW';

      expect(component.isReadyToBet()).toBeFalse();
    });
  });
});
