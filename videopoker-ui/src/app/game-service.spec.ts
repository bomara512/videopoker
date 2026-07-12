import {TestBed} from '@angular/core/testing';
import {provideZonelessChangeDetection} from '@angular/core';
import {provideHttpClient} from '@angular/common/http';
import {HttpTestingController, provideHttpClientTesting} from '@angular/common/http/testing';
import {GameService} from './game-service';
import {Game, Payout} from './game';

const game: Game = {bet: 0, credits: 0, deckSize: 0, gameState: '', hand: [], handRank: '', id: '123'};
const payoutSchedule: Payout[] = [{hand: 'ROYAL_FLUSH', payouts: [250, 500, 750, 1000, 4000]}];

describe('GameService', () => {
  let service: GameService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideZonelessChangeDetection(), provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(GameService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('creates a game and loads the payout schedule', () => {
    service.createGame();

    const req = httpMock.expectOne('http://localhost:8080/game');
    expect(req.request.method).toEqual('POST');
    req.flush(game);

    const req2 = httpMock.expectOne('http://localhost:8080/game/payout-schedule');
    expect(req2.request.method).toEqual('GET');
    req2.flush(payoutSchedule);

    expect(service.game()).toEqual(game);
    expect(service.payoutSchedule()).toEqual(payoutSchedule);
  });

  it('deals a new hand', () => {
    service.game.set(game);

    service.deal();

    const req = httpMock.expectOne('http://localhost:8080/game/123/deal');
    expect(req.request.method).toEqual('PUT');
    const dealt: Game = {...game, gameState: 'READY_TO_DRAW'};
    req.flush(dealt);

    expect(service.game()).toEqual(dealt);
  });

  it('draws new cards with holds', () => {
    service.game.set(game);

    service.draw([1, 2, 3]);

    const req = httpMock.expectOne('http://localhost:8080/game/123/draw?holds=1,2,3');
    expect(req.request.method).toEqual('PUT');
    req.flush(game);

    expect(service.game()).toEqual(game);
  });

  it('sets the current bet', () => {
    service.game.set(game);

    service.bet(3);

    const req = httpMock.expectOne('http://localhost:8080/game/123/bet?amount=3');
    expect(req.request.method).toEqual('PUT');
    const betGame: Game = {...game, bet: 3};
    req.flush(betGame);

    expect(service.game()).toEqual(betGame);
  });
});
