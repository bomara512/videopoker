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

  getPayoutSchedule() {
    return this.gameService.payoutSchedule;
  }

  getCredits() {
    return this.getGame().credits;
  }

  getBet() {
    return this.getGame().bet;
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

  isReadyToBet() {
    return this.getGame().gameState == 'READY_TO_DEAL';
  }
}
