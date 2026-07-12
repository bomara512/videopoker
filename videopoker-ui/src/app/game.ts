export interface Card {
  suit: string;
  rank: string;
}

export interface Game {
  id: string;
  deckSize: number;
  bet: number;
  credits: number;
  hand: Card[];
  handRank: string | null;
  gameState: string;
}

export interface Payout {
  hand: string;
  payouts: number[];
}
