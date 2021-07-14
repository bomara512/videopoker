import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Game, Payout} from "./game";

@Injectable({
  providedIn: 'root'
})
export class GameService {

  private REST_API_SERVER = 'http://localhost:8080';

  game!: Game;
  payoutSchedule!: Payout[];

  constructor(private httpClient: HttpClient) {
  }

  public createGame() {
    this.httpClient.post(this.REST_API_SERVER + '/game', null)
      .subscribe((data: any) => {
        this.game = data;
      });
  }

  public getPayoutSchedule() {
    this.httpClient.get(this.REST_API_SERVER + '/game/payout-schedule')
      .subscribe((data: any) => {
        this.payoutSchedule = data;
      });
  }

  public deal() {
    this.httpClient.put(this.REST_API_SERVER + '/game/' + this.game.id + '/deal', null)
      .subscribe((data: any) => {
        this.game = data;
      });
  }

  public draw(holds: number[]) {
    this.httpClient.put(this.REST_API_SERVER + '/game/' + this.game.id + '/draw?holds=' + holds, null)
      .subscribe((data: any) => {
        this.game = data;
      });
  }

  public bet(amount: number) {
    this.httpClient.put(this.REST_API_SERVER + '/game/' + this.game.id + '/bet?amount=' + amount, null)
      .subscribe((data: any) => {
        this.game = data;
      });
  }
}
