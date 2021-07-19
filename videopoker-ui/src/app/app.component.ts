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

  ngOnInit(): void {
    this.gameService.createGame();
  }

  isGameInitialized() {
    return this.gameService.isReadyToPlay();
  }

  getPayoutSchedule() {
    return this.gameService.getPayoutSchedule();
  }

  getCredits() {
    return this.gameService.getCredits();
  }

  getBet() {
    return this.gameService.getBet();
  }

  betOne() {
    let bet: number;
    if (this.gameService.getBet() === 5) {
      bet = 1;
    } else {
      bet = this.gameService.getBet() + 1;
    }
    this.gameService.bet(bet)
  }

  betMax() {
    let bet: number = 5;
    this.gameService.bet(bet)
  }

  isReadyToBet() {
    return this.gameService.isReadyToBet();
  }
}
