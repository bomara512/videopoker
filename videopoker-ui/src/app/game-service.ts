import {inject, Injectable, signal} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Game, Payout} from './game';

const REST_API_SERVER = 'http://localhost:8080';

@Injectable({providedIn: 'root'})
export class GameService {
  private readonly httpClient = inject(HttpClient);

  readonly game = signal<Game | null>(null);
  readonly payoutSchedule = signal<Payout[]>([]);
  readonly holds = signal<number[]>([]);

  createGame(): void {
    this.httpClient.post<Game>(`${REST_API_SERVER}/game`, null)
      .subscribe(game => this.updateGame(game));
    this.httpClient.get<Payout[]>(`${REST_API_SERVER}/game/payout-schedule`)
      .subscribe(payouts => this.payoutSchedule.set(payouts));
  }

  deal(): void {
    this.httpClient.put<Game>(`${this.gameUrl()}/deal`, null)
      .subscribe(game => this.updateGame(game));
  }

  draw(): void {
    this.httpClient.put<Game>(`${this.gameUrl()}/draw?holds=${this.holds().join(',')}`, null)
      .subscribe(game => this.updateGame(game));
  }

  bet(amount: number): void {
    this.httpClient.put<Game>(`${this.gameUrl()}/bet?amount=${amount}`, null)
      .subscribe(game => this.updateGame(game));
  }

  toggleHold(index: number): void {
    if (this.holds().includes(index)) {
      this.holds.update(holds => holds.filter(i => i !== index));
    } else {
      this.holds.update(holds => [...holds, index]);
    }
  }

  private updateGame(game: Game): void {
    this.game.set(game);
    this.holds.set([]);
  }

  private gameUrl(): string {
    return `${REST_API_SERVER}/game/${this.game()!.id}`;
  }
}
