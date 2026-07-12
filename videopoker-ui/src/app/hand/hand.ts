import {Component, computed, effect, inject, output, signal} from '@angular/core';
import {GameService} from '../game-service';

@Component({
  selector: 'app-hand',
  templateUrl: './hand.html',
  styleUrl: './hand.css',
})
export class Hand {
  private readonly gameService = inject(GameService);

  readonly holdsChanged = output<number[]>();
  readonly holds = signal<number[]>([]);

  protected readonly hand = computed(() => this.gameService.game()?.hand ?? []);
  protected readonly handRank = computed(() => this.gameService.game()?.handRank);
  protected readonly readyToDeal = computed(() => this.gameService.game()?.gameState === 'READY_TO_DEAL');

  constructor() {
    effect(() => {
      this.gameService.game();
      this.holds.set([]);
    });
  }

  toggleHold(index: number): void {
    if (this.isHeld(index)) {
      this.holds.update(holds => holds.filter(i => i !== index));
    } else {
      this.holds.update(holds => [...holds, index]);
    }
    this.holdsChanged.emit(this.holds());
  }

  isHeld(index: number): boolean {
    return this.holds().includes(index);
  }
}
