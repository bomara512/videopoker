import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Game, Payout} from "./game";

@Injectable({
  providedIn: 'root'
})
export class GameService {

  private REST_API_SERVER = 'http://localhost:8080';

  private game!: Game;
  private payoutSchedule!: Payout[];

  constructor(private httpClient: HttpClient) {
  }

  createGame() {
    this.httpClient.post(this.REST_API_SERVER + '/game', null)
      .subscribe((data: any) => {
        this.game = data;
      });

    this.httpClient.get(this.REST_API_SERVER + '/game/payout-schedule')
      .subscribe((data: any) => {
        this.payoutSchedule = data;
      });
  }

  getPayoutSchedule() {
    return this.payoutSchedule;
  }

  isNewGame() {
    return this.getHand().length === 0;
  }

  isReadyToPlay() {
    return this.game !== undefined;
  }

  isReadyToBet() {
    return this.game.gameState == 'READY_TO_DEAL';
  }

  getBet() {
    return this.game.bet;
  }

  getCredits() {
    return this.game.credits;
  }

  getHand() {
    return this.game.hand;
  }

  getHandRank() {
    return this.game.handRank;
  }

  deal() {
    this.httpClient.put(this.REST_API_SERVER + '/game/' + this.game.id + '/deal', null)
      .subscribe((data: any) => {
        this.game = data;
      });
  }

  draw(holds: number[]) {
    this.httpClient.put(this.REST_API_SERVER + '/game/' + this.game.id + '/draw?holds=' + holds, null)
      .subscribe((data: any) => {
        this.game = data;
      });
  }

  bet(amount: number) {
    this.httpClient.put(this.REST_API_SERVER + '/game/' + this.game.id + '/bet?amount=' + amount, null)
      .subscribe((data: any) => {
        this.game = data;
      });
  }
}
