import {AppComponent} from './app.component';
import {TestBed} from '@angular/core/testing';
import {GameService} from "./game.service";
import {of} from "rxjs";
import {Game} from "./game";

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
        AppComponent
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

  describe('Deal', () => {
    it('should call game service when deal button clicked', () => {
      component.deal();

      expect(mockGameService.deal).toHaveBeenCalled();
    });

    it('should toggle holds when selected', () => {
      component.holds = [];

      component.toggleHold(0);
      component.toggleHold(2);
      component.toggleHold(4);

      expect(component.holds).toEqual([0, 2, 4]);

      component.toggleHold(2);

      expect(component.holds).toEqual([0, 4]);
    });

    describe('betOne', () => {
      it('should increase current bet by 1', () => {
        component.gameService.game.bet = 1;

        component.betOne();

        expect(mockGameService.bet).toHaveBeenCalledWith(2);
      });

      it('should rollover to minimum bet if current bet is already maximum', () => {
        component.gameService.game.bet = 5;

        component.betOne();

        expect(mockGameService.bet).toHaveBeenCalledWith(1);
      });
    });

    describe('betMax', () => {
      it('should increase current bet to maximum', () => {
        component.gameService.game.bet = 1;

        component.betMax();

        expect(mockGameService.bet).toHaveBeenCalledWith(5);
      });
    });
  });

  describe('Draw', () => {
    const holds: number[] = [0, 2, 4];

    beforeEach(() => {
      component.holds = holds;
    });

    it('should call game service "draw" when draw button clicked', () => {
      component.draw();

      expect(mockGameService.draw).toHaveBeenCalledWith(holds);
    });

    it('should reset holds', () => {
      component.draw();

      expect(component.holds).toEqual([]);
    })
  });

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

    it('should return the current hand', () => {
      component.gameService.game.hand = [{suit: "CLUB", rank: "FOUR"}];

      expect(component.getHand()).toEqual([{suit: "CLUB", rank: "FOUR"}]);
    });

    it('should return the hand rank', () => {
      component.gameService.game.handRank = 'FULL_HOUSE';

      expect(component.getHandRank()).toEqual('FULL_HOUSE');
    });

    it('should identify if the game is new', () => {
      component.gameService.game.hand = [];

      expect(component.isNewGame()).toBeTrue();

      component.gameService.game.hand = [{suit: "CLUB", rank: "FOUR"}];

      expect(component.isNewGame()).toBeFalse();
    });

    it('should identify if ready to draw', () => {
      component.gameService.game.gameState = 'READY_TO_DRAW';

      expect(component.isReadyToDraw()).toBeTrue();

      component.gameService.game.gameState = 'READY_TO_DEAL';

      expect(component.isReadyToDraw()).toBeFalse();
    });

    it('should identify if ready to deal', () => {
      component.gameService.game.gameState = 'READY_TO_DEAL';

      expect(component.isReadyToDeal()).toBeTrue();

      component.gameService.game.gameState = 'READY_TO_DRAW';

      expect(component.isReadyToDeal()).toBeFalse();
    });
  });

  //
  // getHandRank() {
  //
  // }


});
