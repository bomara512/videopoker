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

  it('sets the current bet', () => {
    service.game.set(game);

    service.bet(3);

    const req = httpMock.expectOne('http://localhost:8080/game/123/bet?amount=3');
    expect(req.request.method).toEqual('PUT');
    const betGame: Game = {...game, bet: 3};
    req.flush(betGame);

    expect(service.game()).toEqual(betGame);
  });

  describe('holds', () => {
    it('toggles holds preserving selection order', () => {
      service.toggleHold(0);
      service.toggleHold(2);
      service.toggleHold(4);
      expect(service.holds()).toEqual([0, 2, 4]);

      service.toggleHold(2);
      expect(service.holds()).toEqual([0, 4]);
    });

    it('draws with the current holds', () => {
      service.game.set(game);
      service.toggleHold(1);
      service.toggleHold(3);

      service.draw();

      const req = httpMock.expectOne('http://localhost:8080/game/123/draw?holds=1,3');
      expect(req.request.method).toEqual('PUT');
      req.flush(game);
    });

    it('draws with no holds when none selected', () => {
      service.game.set(game);

      service.draw();

      const req = httpMock.expectOne('http://localhost:8080/game/123/draw?holds=');
      req.flush(game);
    });

    it('resets holds on every game update (stale-holds regression)', () => {
      service.game.set(game);
      service.toggleHold(0);
      service.toggleHold(2);

      service.deal();
      httpMock.expectOne('http://localhost:8080/game/123/deal').flush(game);

      expect(service.holds()).toEqual([]);
    });
  });

  describe('errors', () => {
    it('surfaces the backend error message', () => {
      service.game.set(game);

      service.deal();
      httpMock.expectOne('http://localhost:8080/game/123/deal')
        .flush({message: 'Not enough credits'}, {status: 400, statusText: 'Bad Request'});

      expect(service.errorMessage()).toEqual('Not enough credits');
    });

    it('falls back to a generic message when the error has no body message', () => {
      service.game.set(game);

      service.bet(3);
      httpMock.expectOne('http://localhost:8080/game/123/bet?amount=3')
        .flush(null, {status: 500, statusText: 'Server Error'});

      expect(service.errorMessage()).toEqual('Something went wrong talking to the server');
    });

    it('clears the error on the next successful action', () => {
      service.game.set(game);
      service.errorMessage.set('stale error');

      service.deal();
      httpMock.expectOne('http://localhost:8080/game/123/deal').flush(game);

      expect(service.errorMessage()).toBeNull();
    });

    it('dismissError clears the message', () => {
      service.errorMessage.set('boom');
      service.dismissError();
      expect(service.errorMessage()).toBeNull();
    });

    it('preserves holds when an action fails', () => {
      service.game.set(game);
      service.toggleHold(1);
      service.toggleHold(3);

      service.draw();
      httpMock.expectOne('http://localhost:8080/game/123/draw?holds=1,3')
        .flush({message: 'boom'}, {status: 400, statusText: 'Bad Request'});

      expect(service.holds()).toEqual([1, 3]);
      expect(service.errorMessage()).toEqual('boom');
    });
  });
});
