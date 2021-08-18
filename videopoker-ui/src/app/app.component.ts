import {Component, OnInit} from '@angular/core';
import {GameService} from "./game.service";
import {Payout} from "./game";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'videopoker-ui';
  gameService: GameService;
  isGameInitialized: boolean = false;
  isReadyToBet: boolean = false;
  credits: number = 0;
  bet: number = 0;
  payoutSchedule: Payout[] = [];

  constructor(gameService: GameService) {
    this.gameService = gameService;
  }

  ngOnInit(): void {
    this.gameService.createGame();
    this.gameService.gameUpdate$.subscribe(
      gameUpdate => {
        this.payoutSchedule = this.gameService.getPayoutSchedule();
        this.isGameInitialized = true;
        this.isReadyToBet = gameUpdate.gameState == 'READY_TO_DEAL';
        this.credits = gameUpdate.credits;
        this.bet = gameUpdate.bet;
      });
  }

  betOne() {
    let bet: number;
    if (this.bet === 5) {
      bet = 1;
    } else {
      bet = this.bet + 1;
    }
    this.gameService.bet(bet)
  }

  betMax() {
    let bet: number = 5;
    this.gameService.bet(bet)
  }
}
