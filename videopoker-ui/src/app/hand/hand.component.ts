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

  constructor(gameService: GameService) {
    this.gameService = gameService;
  }

  private getGame() {
    return this.gameService.game;
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

  isReadyToDraw() {
    return this.getGame().gameState == 'READY_TO_DRAW';
  }

  isReadyToDeal() {
    return this.getGame().gameState == 'READY_TO_DEAL';
  }

  getHand() {
    return this.getGame().hand;
  }

  getHandRank() {
    return this.getGame().handRank;
  }
}
