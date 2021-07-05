export class Card {
  suit!: string;
  rank!: string;
}

export class Game {
  id!: string;
  deckSize!: number;
  bet!: number;
  credits!: number;
  hand!: Card[];
  handRank!: string;
  gameState!: string;
}

export class Payouts {
  hand!: string;
  payouts!: number[];
}
