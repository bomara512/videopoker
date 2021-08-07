import {ComponentFixture, TestBed} from '@angular/core/testing';

import {HandComponent} from './hand.component';
import {of} from "rxjs";
import {Game} from "../game";
import {GameService} from "../game.service";

describe('HandComponent', () => {
  let component: HandComponent;
  let fixture: ComponentFixture<HandComponent>;
  const mockGameService = jasmine.createSpyObj(['deal', 'draw', 'getHand', 'getHandRank']);
  const hand = [{suit: 'SPADES', rank: 'ACE'}];
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

    await TestBed.configureTestingModule({
      declarations: [HandComponent],
      providers: [{provide: GameService, useValue: mockGameService}]
    })
      .compileComponents();

    fixture = TestBed.createComponent(HandComponent);
    component = fixture.componentInstance;
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

      expect(component.isHeld(0)).toBeTrue();
      expect(component.isHeld(4)).toBeTrue();
      expect(component.isHeld(1)).toBeFalse();
      expect(component.isHeld(2)).toBeFalse();
      expect(component.isHeld(3)).toBeFalse();
    });

    it('should disable deal and enable draw', () => {
      component.readyToDeal = true;
      component.readyToDraw = false;

      component.deal();

      expect(component.readyToDeal).toBeFalse();
      expect(component.readyToDraw).toBeTrue();
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
    });

    it('should disable draw and enable deal', () => {
      component.readyToDraw = true;
      component.readyToDeal = false;

      component.draw();

      expect(component.readyToDraw).toBeFalse();
      expect(component.readyToDeal).toBeTrue();
    });
  });

  describe('Game State', () => {
    it('should return current hand', () => {
      mockGameService.getHand.and.returnValue(hand);

      expect(component.getHand()).toEqual(hand)
    });

    it('should return current handRank', () => {
      mockGameService.getHandRank.and.returnValue('FULL_HOUSE');

      expect(component.getHandRank()).toEqual('FULL_HOUSE');
    });
  });
});
