import {TestBed} from '@angular/core/testing';

import {GameService} from './game.service';
import {Game, Payout} from "./game";
import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";
import {HttpClient} from "@angular/common/http";

const game: Game = {bet: 0, credits: 0, deckSize: 0, gameState: "", hand: [], handRank: "", id: "123"};

describe('GameService', () => {
  let service: GameService;
  let httpMock: HttpTestingController;
  let httpClient: HttpClient;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [GameService, HttpClient]
    });
    service = TestBed.inject(GameService);
    httpMock = TestBed.inject(HttpTestingController);
    httpClient = TestBed.inject(HttpClient);
    service.game = game;
  });

  it('should call backend service to retrieve payout schedule', async () => {
    const payoutSchedule: Payout[] = [];

    service.getPayoutSchedule();

    const req = httpMock.expectOne('http://localhost:8080/game/payout-schedule');
    req.flush(payoutSchedule);

    expect(req.request.method).toEqual('GET');
    expect(service.payoutSchedule).toEqual(payoutSchedule);

    httpMock.verify();
  });

  it('should call backend service to deal new hand', () => {
    service.deal()

    const req = httpMock.expectOne('http://localhost:8080/game/123/deal');
    req.flush(game);

    expect(req.request.method).toEqual('PUT');
    expect(service.game).toEqual(game);

    httpMock.verify();
  });

  it('should call backend service to draw new cards', () => {
    service.draw([1, 2, 3]);

    const req = httpMock.expectOne('http://localhost:8080/game/123/draw?holds=1,2,3');
    req.flush(game);

    expect(req.request.method).toEqual('PUT');
    expect(service.game).toEqual(game);

    httpMock.verify();
  });

  it('should call backend service to set current bet', () => {
    service.bet(3)

    const req = httpMock.expectOne('http://localhost:8080/game/123/bet?amount=3');
    req.flush(game);

    expect(req.request.method).toEqual('PUT');
    expect(service.game).toEqual(game);

    httpMock.verify();
  });
});
