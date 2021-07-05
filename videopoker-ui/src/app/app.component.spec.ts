import {AppComponent} from './app.component';
import {TestBed} from '@angular/core/testing';
import {GameService} from "./game.service";
import {of} from "rxjs";
import {Game, Payouts} from "./game";

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
  const payoutSchedule: Payouts[] = [{hand: "testHand", payouts: [1, 2, 3]}];
  let fixture: any;
  let component: any;

  beforeEach(async () => {
    mockGameService.getPayoutSchedule.and.returnValue(of(payoutSchedule));
    mockGameService.createGame.and.returnValue(of(game));
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
  });

  it('should initialize game', () => {
    fixture.detectChanges();

    expect(component.payoutSchedule).toEqual(payoutSchedule);
    expect(component.game).toEqual(game);
  })

  describe('Deal', () => {
    it('should call game service when deal button clicked', () => {
      component.game = game;
      component.deal();

      fixture.detectChanges();

      expect(mockGameService.deal).toHaveBeenCalledWith('123');
    });

    it('should toggle holds when selected', () => {
      component.game = game;
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
        component.game = game;
        component.game.bet = 1;

        component.betOne();

        expect(mockGameService.bet).toHaveBeenCalledWith('123', 2);
      });

      it('should rollover to minimum bet if current bet is already maximum', () => {
        component.game = game;
        component.game.bet = 5;

        component.betOne();

        expect(mockGameService.bet).toHaveBeenCalledWith('123', 1);
      });
    });

    describe('betMax', () => {
      it('should increase current bet to maximum', () => {
        component.game = game;
        component.game.bet = 1;

        component.betMax();

        expect(mockGameService.bet).toHaveBeenCalledWith('123', 5);
      });
    });
  });

  describe('Draw', () => {
    const holds: number[] = [0, 2, 4];

    beforeEach(() => {
      component.game = game;
      component.holds = holds;
    });

    it('should call game service "draw" when draw button clicked', () => {
      component.draw();
      fixture.detectChanges();

      expect(mockGameService.draw).toHaveBeenCalledWith(game.id, holds);
    });

    it('should reset holds', () => {
      component.draw();
      fixture.detectChanges();

      expect(component.holds).toEqual([]);
    })
  });
});
