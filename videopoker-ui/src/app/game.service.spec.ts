import {TestBed} from '@angular/core/testing';

import {GameService} from './game.service';
import {Payouts} from "./game";
import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";
import {HttpClient} from "@angular/common/http";

describe('GameService', () => {
  let service: GameService;
  let httpMock: HttpTestingController;
  let httpClient: HttpClient;

  const game: any = {};
  const gameId: string = '123';

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [GameService, HttpClient]
    });
    service = TestBed.inject(GameService);
    httpMock = TestBed.inject(HttpTestingController);
    httpClient = TestBed.inject(HttpClient);
  });

  it('should call backend service to retrieve payout schedule', async () => {
    const payoutSchedule: Payouts[] = [];

    service.getPayoutSchedule().subscribe((data) => {
      expect(data).toEqual(payoutSchedule);
    });

    const req = httpMock.expectOne('http://localhost:8080/game/payout-schedule');

    expect(req.request.method).toEqual('GET');

    req.flush(payoutSchedule);

    httpMock.verify();
  });

  it('should call backend service to deal new hand', async () => {
    service.deal(gameId).subscribe((data) => {
      expect(data).toEqual(game);
    });

    const req = httpMock.expectOne('http://localhost:8080/game/' + gameId + "/deal");

    expect(req.request.method).toEqual('PUT');

    req.flush(game);

    httpMock.verify();
  });

  it('should call backend service to draw new cards', async () => {
    service.draw(gameId, [1, 2, 3]).subscribe((data) => {
      expect(data).toEqual(game);
    });

    const req = httpMock.expectOne('http://localhost:8080/game/' + gameId + "/draw?holds=1,2,3");

    expect(req.request.method).toEqual('PUT');

    req.flush(game);

    httpMock.verify();
  });

  it('should call backend service to set current bet', async () => {
    await service.bet(gameId, 3).subscribe((data) => {
      expect(data).toEqual(game);
    });

    const req = httpMock.expectOne('http://localhost:8080/game/' + gameId + "/bet?amount=3");

    expect(req.request.method).toEqual('PUT');

    req.flush(game);

    httpMock.verify();
  });
});
