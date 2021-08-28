import {ComponentFixture, TestBed} from '@angular/core/testing';

import {HandComponent} from './hand.component';
import {of} from "rxjs";
import {GameService} from "../game.service";

describe('HandComponent', () => {
  let component: HandComponent;
  let fixture: ComponentFixture<HandComponent>;
  const mockGameService = jasmine.createSpyObj(['']);

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [HandComponent],
      providers: [{provide: GameService, useValue: mockGameService}]
    }).compileComponents();

    fixture = TestBed.createComponent(HandComponent);
    component = fixture.componentInstance;
  });

  describe('Hand', () => {
    it('should subscribe to game updates', () => {
      component.holds = [1, 2, 3];

      component.gameService.gameUpdate$ = of({
        id: '123',
        credits: 1,
        hand: [{rank: 'TWO', suit: 'CLUB'}],
        deckSize: 2,
        gameState: 'READY_TO_DRAW',
        bet: 3,
        handRank: "FLUSH"
      });

      fixture.detectChanges();

      expect(component.hand).toEqual([{rank: 'TWO', suit: 'CLUB'}]);
      expect(component.handRank).toEqual('FLUSH');
      expect(component.readyToDeal).toEqual(false);
      expect(component.holds).toEqual([]);
    });

    it('should identify if a card is held or not', () => {
      component.holds = [1, 2, 3];

      expect(component.isHeld(0)).toBeFalse();
      expect(component.isHeld(4)).toBeFalse();
      expect(component.isHeld(1)).toBeTrue();
      expect(component.isHeld(2)).toBeTrue();
      expect(component.isHeld(3)).toBeTrue();
    });

    describe('toggleHold', () => {
      it('should toggle holds when selected', () => {
        component.holds = [];

        component.toggleHold(0);
        component.toggleHold(2);
        component.toggleHold(4);

        expect(component.holds).toEqual([0, 2, 4]);

        component.toggleHold(2);

        expect(component.holds).toEqual([0, 4]);
      });

      it('should emit hold change events', () => {
        spyOn(component.holdsChanged, 'emit');
        component.holds = [];

        component.toggleHold(0);
        expect(component.holdsChanged.emit).toHaveBeenCalledWith([0]);

        component.toggleHold(2);
        expect(component.holdsChanged.emit).toHaveBeenCalledWith([0, 2]);

        component.toggleHold(2);
        expect(component.holdsChanged.emit).toHaveBeenCalledWith([0]);
      });
    });
  });
});
