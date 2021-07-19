import {TestBed} from '@angular/core/testing';

import {GameService} from './game.service';
import {Game, Payout} from "./game";
import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";
import {HttpClient} from "@angular/common/http";

const game: Game = {bet: 0, credits: 0, deckSize: 0, gameState: "", hand: [], handRank: "", id: "123"};
const payoutSchedule: Payout[] = [];

describe('GameService', () => {
  let service: GameService;
  let httpMock: HttpTestingController;
  let httpClient: HttpClient;
  const hand = [{suit: 'SPADES', rank: 'ACE'}];

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [GameService, HttpClient]
    });
    service = TestBed.inject(GameService);
    httpMock = TestBed.inject(HttpTestingController);
    httpClient = TestBed.inject(HttpClient);
  });

  it('should call backend service to create game', async () => {
    service.createGame();

    const req = httpMock.expectOne('http://localhost:8080/game');
    req.flush(game);

    const req2 = httpMock.expectOne('http://localhost:8080/game/payout-schedule');
    req2.flush(payoutSchedule);

    expect(req.request.method).toEqual('POST');
    expect(service['game']).toEqual(game);

    expect(req2.request.method).toEqual('GET');
    expect(service['payoutSchedule']).toEqual(payoutSchedule);

    httpMock.verify();
  });

  it('should call backend service to deal new hand', () => {
    service['game'] = game;

    service.deal()

    const req = httpMock.expectOne('http://localhost:8080/game/123/deal');
    req.flush(game);

    expect(req.request.method).toEqual('PUT');
    expect(service['game']).toEqual(game);

    httpMock.verify();
  });

  it('should call backend service to draw new cards', () => {
    service['game'] = game;

    service.draw([1, 2, 3]);

    const req = httpMock.expectOne('http://localhost:8080/game/123/draw?holds=1,2,3');
    req.flush(game);

    expect(req.request.method).toEqual('PUT');
    expect(service['game']).toEqual(game);

    httpMock.verify();
  });

  it('should call backend service to set current bet', () => {
    service['game'] = game;

    service.bet(3)

    const req = httpMock.expectOne('http://localhost:8080/game/123/bet?amount=3');
    req.flush(game);

    expect(req.request.method).toEqual('PUT');
    expect(service['game']).toEqual(game);

    httpMock.verify();
  });

  describe('Initial State', () => {
    it('should identify if ready to play', () => {
      expect(service.isReadyToPlay()).toBeFalse();

      service['game'] = game;
      expect(service.isReadyToPlay()).toBeTrue();
    });

    it('should identify if new game', () => {
      service['game'] = game;
      service['game'].hand = [];
      expect(service.isNewGame()).toBeTrue();

      service['game'].hand = hand;
      expect(service.isNewGame()).toBeFalse();
    });
  });

  describe('Game State', () => {
    beforeEach(() => {
      service['game'] = game;
    });

    it('should identify if ready to bet', () => {
      service['game'].gameState = 'READY_TO_DEAL';
      expect(service.isReadyToBet()).toBeTrue();

      service['game'].gameState = 'READY_TO_DRAW';
      expect(service.isReadyToBet()).toBeFalse();
    });

    it('should return payout schedule', () => {
      service['payoutSchedule'] = payoutSchedule;
      expect(service.getPayoutSchedule()).toEqual(payoutSchedule);
    });

    it('should return current bet', () => {
      service['game'].bet = 2;
      expect(service.getBet()).toEqual(2)
    });

    it('should return current credits', () => {
      service['game'].credits = 25;
      expect(service.getCredits()).toEqual(25);
    });

    it('should return current hand', () => {
      service['game'].hand = hand;
      expect(service.getHand()).toEqual(hand)
    });

    it('should return current handRank', () => {
      service['game'].handRank = 'FULL_HOUSE';
      expect(service.getHandRank()).toEqual('FULL_HOUSE');
    });
  });
});
