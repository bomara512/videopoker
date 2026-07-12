import {Component, computed, effect, inject, OnInit} from '@angular/core';
import {GameService} from './game-service';
import {Hand} from './hand/hand';

@Component({
  selector: 'app-root',
  imports: [Hand],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App implements OnInit {
  private readonly gameService = inject(GameService);

  protected readonly payoutSchedule = this.gameService.payoutSchedule;
  protected readonly isGameInitialized = computed(() => this.gameService.game() !== null);
  protected readonly readyToDeal = computed(() => this.gameService.game()?.gameState === 'READY_TO_DEAL');
  protected readonly credits = computed(() => this.gameService.game()?.credits ?? 0);
  protected readonly bet = computed(() => this.gameService.game()?.bet ?? 0);

  private holds: number[] = [];

  constructor() {
    // Reset alongside Hand's own reset so a later draw can't send stale holds.
    effect(() => {
      this.gameService.game();
      this.holds = [];
    });
  }

  ngOnInit(): void {
    this.gameService.createGame();
  }

  betOne(): void {
    this.gameService.bet(this.bet() === 5 ? 1 : this.bet() + 1);
  }

  betMax(): void {
    this.gameService.bet(5);
  }

  deal(): void {
    this.gameService.deal();
  }

  draw(): void {
    this.gameService.draw(this.holds);
  }

  holdsChangedHandler(holds: number[]): void {
    this.holds = holds;
  }
}
