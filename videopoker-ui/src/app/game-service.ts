import {inject, Injectable, signal} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Game, Payout} from './game';

const REST_API_SERVER = 'http://localhost:8080';

@Injectable({providedIn: 'root'})
export class GameService {
  private readonly httpClient = inject(HttpClient);

  readonly game = signal<Game | null>(null);
  readonly payoutSchedule = signal<Payout[]>([]);

  createGame(): void {
    this.httpClient.post<Game>(`${REST_API_SERVER}/game`, null)
      .subscribe(game => this.game.set(game));
    this.httpClient.get<Payout[]>(`${REST_API_SERVER}/game/payout-schedule`)
      .subscribe(payouts => this.payoutSchedule.set(payouts));
  }

  deal(): void {
    this.httpClient.put<Game>(`${this.gameUrl()}/deal`, null)
      .subscribe(game => this.game.set(game));
  }

  draw(holds: number[]): void {
    this.httpClient.put<Game>(`${this.gameUrl()}/draw?holds=${holds.join(',')}`, null)
      .subscribe(game => this.game.set(game));
  }

  bet(amount: number): void {
    this.httpClient.put<Game>(`${this.gameUrl()}/bet?amount=${amount}`, null)
      .subscribe(game => this.game.set(game));
  }

  private gameUrl(): string {
    return `${REST_API_SERVER}/game/${this.game()!.id}`;
  }
}
