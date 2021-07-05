import {Component, OnInit} from '@angular/core';
import {Game, Payouts} from "./game";
import {GameService} from "./game.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'videopoker-ui';
  game!: Game;
  payoutSchedule!: Payouts[];
  gameService: GameService;
  holds: number[] = [];

  constructor(gameService: GameService) {
    this.gameService = gameService;
  }

  ngOnInit(): void {
    this.gameService.createGame().subscribe((data: any) => {
      this.game = data;
    });

    this.gameService.getPayoutSchedule().subscribe((data: any) => {
      this.payoutSchedule = data;
    });
  }

  deal(): void {
    this.gameService.deal(this.game.id).subscribe((data: any) => {
      this.game = data;
    });
  }

  draw() {
    this.gameService.draw(this.game.id, this.holds).subscribe((data: any) => {
      this.game = data;
      this.holds = [];
    });
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
    if (this.game.bet === 5) {
      bet = 1;
    } else {
      bet = this.game.bet + 1;
    }

    this.gameService.bet(this.game.id, bet).subscribe((data: any) => {
      this.game = data;
    });
  }

  betMax() {
    let bet: number = 5;

    this.gameService.bet(this.game.id, bet).subscribe((data: any) => {
      this.game = data;
    });
  }
}
