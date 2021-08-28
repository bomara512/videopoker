import {AppComponent} from './app.component';
import {TestBed} from '@angular/core/testing';
import {GameService} from "./game.service";
import {Game, Payout} from "./game";
import {HandComponent} from "./hand/hand.component";
import {of} from "rxjs";

describe('AppComponent', () => {
  const mockGameService = jasmine.createSpyObj(['createGame', 'getPayoutSchedule', 'deal', 'draw', 'bet']);
  const game: Game = {
    id: '123',
    credits: 1,
    hand: [],
    deckSize: 2,
    gameState: "READY_TO_DEAL",
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
    component.gameService.gameUpdate$ = of(game);
    mockGameService.getPayoutSchedule.and.returnValue(payoutSchedule);
  });

  it('should initialize game', () => {
    fixture.detectChanges();

    expect(mockGameService.createGame).toHaveBeenCalled();
  });

  it('should subscribe to game updates', () => {
    fixture.detectChanges();

    expect(component.payoutSchedule).toEqual(payoutSchedule);
    expect(component.isGameInitialized).toEqual(true);
    expect(component.readyToDeal).toEqual(true);
    expect(component.credits).toEqual(1);
    expect(component.bet).toEqual(3);
  });

  describe('betOne', () => {
    it('should increase bet by one', () => {
      component.bet = 3;
      component.betOne();
      expect(mockGameService.bet).toHaveBeenCalledWith(4);
    });

    it('should roll maximum bet back to one', () => {
      component.bet = 5;
      component.betOne();
      expect(mockGameService.bet).toHaveBeenCalledWith(1);
    });
  });

  describe('betMax', () => {
    it('should set minimum bet to max', () => {
      component.bet = 1;
      component.betMax();
      expect(mockGameService.bet).toHaveBeenCalledWith(5);
    });

    it('should set non-min/max bet to max', () => {
      component.bet = 3;
      component.betMax();
      expect(mockGameService.bet).toHaveBeenCalledWith(5);
    });

    it('should set maximum bet to max', () => {
      component.bet = 5;
      component.betMax();
      expect(mockGameService.bet).toHaveBeenCalledWith(5);
    });
  });

  describe('deal', () => {
    it('should call game service "deal" when deal button clicked', () => {
      component.deal();

      expect(mockGameService.deal).toHaveBeenCalled();
    });
  });

  describe('draw', () => {
    it('should call game service "draw" when draw button clicked', () => {
      const holds: number[] = [0, 2, 4];

      component.holds = holds;

      component.draw();

      expect(mockGameService.draw).toHaveBeenCalledWith(holds);
    });
  });

  describe('holdsChangedHandler', () => {
    it('should update holds', () => {
      const holds: number[] = [0, 2, 4];

      component.holds = [];

      component.holdsChangedHandler(holds);

      expect(component.holds).toEqual(holds);
    });
  });
});
