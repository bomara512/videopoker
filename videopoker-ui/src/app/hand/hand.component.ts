import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {GameService} from "../game.service";
import {Card} from "../game";

@Component({
  selector: 'app-hand',
  templateUrl: './hand.component.html',
  styleUrls: ['./hand.component.css']
})
export class HandComponent implements OnInit {
  gameService: GameService;
  holds: number[] = [];
  readyToDeal: boolean = true;
  hand: Card[] = [];
  handRank?: string;

  @Output() holdsChanged: EventEmitter<number[]> = new EventEmitter();

  constructor(gameService: GameService) {
    this.gameService = gameService;
  }

  ngOnInit(): void {
    this.gameService.gameUpdate$.subscribe(
      gameUpdate => {
        this.hand = gameUpdate.hand;
        this.handRank = gameUpdate.handRank;
        this.readyToDeal = gameUpdate.gameState == 'READY_TO_DEAL';
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

    this.holdsChanged.emit(this.holds);
  }

  isHeld(index: number) {
    return this.holds.indexOf(index) !== -1;
  }
}
