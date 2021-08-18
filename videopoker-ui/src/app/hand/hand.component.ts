import {Component, OnInit} from '@angular/core';
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
  readyToDraw: boolean = false;
  hand: Card[] = [];
  handRank?: string;

  constructor(gameService: GameService) {
    this.gameService = gameService;
  }

  ngOnInit(): void {
    this.gameService.gameUpdate$.subscribe(
      gameUpdate => {
        this.hand = gameUpdate.hand;
        this.handRank = gameUpdate.handRank;
      });
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

  isHeld(index: number) {
    return this.holds.indexOf(index) !== -1;
  }
}
