import {inject, Injectable, signal} from '@angular/core';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import {Game, Payout} from './game';

@Injectable({providedIn: 'root'})
export class GameService {
  private readonly httpClient = inject(HttpClient);

  readonly game = signal<Game | null>(null);
  readonly payoutSchedule = signal<Payout[]>([]);
  readonly holds = signal<number[]>([]);
  readonly errorMessage = signal<string | null>(null);

  createGame(): void {
    this.httpClient.post<Game>('/game', null)
      .subscribe({next: game => this.updateGame(game), error: err => this.handleError(err)});
    this.httpClient.get<Payout[]>('/game/payout-schedule')
      .subscribe({next: payouts => this.payoutSchedule.set(payouts), error: err => this.handleError(err)});
  }

  deal(): void {
    this.httpClient.put<Game>(`${this.gameUrl()}/deal`, null)
      .subscribe({next: game => this.updateGame(game), error: err => this.handleError(err)});
  }

  draw(): void {
    this.httpClient.put<Game>(`${this.gameUrl()}/draw?holds=${this.holds().join(',')}`, null)
      .subscribe({next: game => this.updateGame(game), error: err => this.handleError(err)});
  }

  bet(amount: number): void {
    this.httpClient.put<Game>(`${this.gameUrl()}/bet?amount=${amount}`, null)
      .subscribe({next: game => this.updateGame(game), error: err => this.handleError(err)});
  }

  toggleHold(index: number): void {
    if (this.holds().includes(index)) {
      this.holds.update(holds => holds.filter(i => i !== index));
    } else {
      this.holds.update(holds => [...holds, index]);
    }
  }

  dismissError(): void {
    this.errorMessage.set(null);
  }

  private handleError(error: HttpErrorResponse): void {
    this.errorMessage.set(error.error?.message ?? 'Something went wrong talking to the server');
  }

  private updateGame(game: Game): void {
    this.game.set(game);
    this.holds.set([]);
    this.errorMessage.set(null);
  }

  private gameUrl(): string {
    return `/game/${this.game()!.id}`;
  }
}
