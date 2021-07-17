import {ComponentFixture, TestBed} from '@angular/core/testing';

import {HandComponent} from './hand.component';
import {of} from "rxjs";
import {Game} from "../game";
import {GameService} from "../game.service";

describe('HandComponent', () => {
  let component: HandComponent;
  let fixture: ComponentFixture<HandComponent>;
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

  beforeEach(async () => {
    mockGameService.deal.and.returnValue(of(game));
    mockGameService.draw.and.returnValue(of(game));
    mockGameService.bet.and.returnValue(of(game));

    await TestBed.configureTestingModule({
      declarations: [HandComponent],
      providers: [{provide: GameService, useValue: mockGameService}]
    })
      .compileComponents();

    fixture = TestBed.createComponent(HandComponent);
    component = fixture.componentInstance;
    component.gameService.game = game;
    fixture.detectChanges();
  });

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
});
