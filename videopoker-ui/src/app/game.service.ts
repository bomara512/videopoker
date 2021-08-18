import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Game, Payout} from "./game";
import {Subject} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class GameService {

  private REST_API_SERVER = 'http://localhost:8080';
  private game!: Game;
  private payoutSchedule!: Payout[];
  private gameUpdateSource = new Subject<Game>();

  public gameUpdate$ = this.gameUpdateSource.asObservable();

  constructor(private httpClient: HttpClient) {
  }

  createGame() {
    this.httpClient.get(this.REST_API_SERVER + '/game/payout-schedule')
      .subscribe((data: any) => {
        this.payoutSchedule = data;
      });

    this.httpClient.post(this.REST_API_SERVER + '/game', null)
      .subscribe((data: any) => {
        this.game = data;
        this.gameUpdateSource.next(data);
      });
  }

  getPayoutSchedule() {
    return this.payoutSchedule;
  }

  deal() {
    this.httpClient.put(this.REST_API_SERVER + '/game/' + this.game.id + '/deal', null)
      .subscribe((data: any) => {
        this.game = data;
        this.gameUpdateSource.next(data);
      });
  }

  draw(holds: number[]) {
    this.httpClient.put(this.REST_API_SERVER + '/game/' + this.game.id + '/draw?holds=' + holds, null)
      .subscribe((data: any) => {
        this.game = data;
        this.gameUpdateSource.next(data);
      });
  }

  bet(amount: number) {
    this.httpClient.put(this.REST_API_SERVER + '/game/' + this.game.id + '/bet?amount=' + amount, null)
      .subscribe((data: any) => {
        this.game = data;
        this.gameUpdateSource.next(data);
      });
  }
}
