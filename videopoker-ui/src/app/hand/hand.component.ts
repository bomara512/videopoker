import {Component} from '@angular/core';
import {GameService} from "../game.service";

@Component({
  selector: 'app-hand',
  templateUrl: './hand.component.html',
  styleUrls: ['./hand.component.css']
})
export class HandComponent {
  gameService: GameService;
  holds: number[] = [];
  readyToDeal: boolean = true;
  readyToDraw: boolean = false;

  constructor(gameService: GameService) {
    this.gameService = gameService;
  }

  deal(): void {
    this.gameService.deal();
    this.readyToDeal = false;
    this.readyToDraw = true;
  }

  draw() {
    this.gameService.draw(this.holds);
    this.holds = [];
    this.readyToDeal = true;
    this.readyToDraw = false;
  }

  toggleHold(index: number) {
    let holdIndex = this.holds.indexOf(index);
    if (holdIndex === -1) {
      this.holds.push(index);
    } else {
      this.holds.splice(holdIndex, 1);
    }
  }

  getHand() {
    return this.gameService.getHand();
  }

  getHandRank() {
    return this.gameService.getHandRank();
  }
}
