import {Component, OnInit} from '@angular/core';
import {GameService} from "./game.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'videopoker-ui';
  gameService: GameService;
  holds: number[] = [];

  constructor(gameService: GameService) {
    this.gameService = gameService;
  }

  private getGame() {
    return this.gameService.game;
  }

  ngOnInit(): void {
    this.gameService.createGame();
    this.gameService.getPayoutSchedule()
  }

  isGameInitialized() {
    return this.getGame();
  }

  isNewGame() {
    return this.getGame().hand.length == 0;
  }

  deal(): void {
    this.gameService.deal();
  }

  draw() {
    this.gameService.draw(this.holds);
    this.holds = [];
  }

  toggleHold(index: number) {
    let holdIndex = this.holds.indexOf(index);
    if (holdIndex === -1) {
      this.holds.push(index);
    } else {
      this.holds.splice(holdIndex, 1);
    }
  }

  betOne() {
    let bet: number;
    if (this.getGame().bet === 5) {
      bet = 1;
    } else {
      bet = this.getGame().bet + 1;
    }
    this.gameService.bet(bet)
  }

  betMax() {
    let bet: number = 5;
    this.gameService.bet(bet)
  }

  isReadyToDraw() {
    return this.getGame().gameState == 'READY_TO_DRAW';
  }

  isReadyToDeal() {
    return this.getGame().gameState == 'READY_TO_DEAL';
  }

  getCredits() {
    return this.getGame().credits;
  }

  getBet() {
    return this.getGame().bet;
  }

  getHand() {
    return this.getGame().hand;
  }

  getHandRank() {
    return this.getGame().handRank;
  }

  getPayoutSchedule() {
    return this.gameService.payoutSchedule;
  }
}
