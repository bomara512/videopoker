import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class GameService {

  private REST_API_SERVER = 'http://localhost:8080';

  constructor(private httpClient: HttpClient) {
  }

  public createGame() {
    return this.httpClient.post(this.REST_API_SERVER + '/game', null);
  }

  public getPayoutSchedule() {
    return this.httpClient.get(this.REST_API_SERVER + '/game/payout-schedule');
  }

  public deal(gameId: string) {
    return this.httpClient.put(this.REST_API_SERVER + '/game/' + gameId + '/deal', null);
  }

  public draw(gameId: string, holds: number[]) {
    return this.httpClient.put(this.REST_API_SERVER + '/game/' + gameId + '/draw?holds=' + holds, null);
  }

  public bet(gameId: string, amount: number) {
    return this.httpClient.put(this.REST_API_SERVER + '/game/' + gameId + '/bet?amount=' + amount, null);
  }
}
