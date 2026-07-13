import {Component, computed, inject} from '@angular/core';
import {GameService} from '../game-service';

@Component({
  selector: 'app-hand',
  templateUrl: './hand.html',
  styleUrl: './hand.css',
})
export class Hand {
  private readonly gameService = inject(GameService);

  protected readonly hand = computed(() => this.gameService.game()?.hand ?? []);
  protected readonly handRank = computed(() => this.gameService.game()?.handRank);
  protected readonly readyToDeal = computed(() => this.gameService.game()?.gameState === 'READY_TO_DEAL');

  protected toggleHold(index: number): void {
    this.gameService.toggleHold(index);
  }

  protected isHeld(index: number): boolean {
    return this.gameService.holds().includes(index);
  }
}
