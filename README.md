# ♥ ♠ VideoPoker ♣ ♦

Backend for simple game of video poker. Springboard to more ambitious multi-player card games.

Current state is rudimentary. Run locally with dockerized redis, and curl as "ui". Web frontend coming....

1. Install docker, curl, jq.
2. `> docker compose up`
3. `> ./gradlew build && java -jar build/libs/videopoker-0.0.1-SNAPSHOT.jar`

New Game
```bash
$ curl -s -X POST "http://localhost:8080/game" | jq
{
  "id": "2a8ab9d6-8f0c-463c-8036-81223dea7c62",
  "deckSize": 52,
  "bet": 1,
  "credits": 50,
  "hand": [],
  "handRank": null,
  "gameState": "READY_TO_DEAL"
}
```

Deal
```bash
$ curl -s -X PUT "http://localhost:8080/game/2a8ab9d6-8f0c-463c-8036-81223dea7c62/deal" | jq
{
  "id": "2a8ab9d6-8f0c-463c-8036-81223dea7c62",
  "deckSize": 47,
  "bet": 1,
  "credits": 49,
  "hand": [
    {
      "suit": "CLUB",
      "rank": "FOUR"
    },
    {
      "suit": "DIAMOND",
      "rank": "TEN"
    },
    {
      "suit": "HEART",
      "rank": "SIX"
    },
    {
      "suit": "DIAMOND",
      "rank": "SIX"
    },
    {
      "suit": "SPADE",
      "rank": "JACK"
    }
  ],
  "handRank": "HIGH_CARD",
  "gameState": "READY_TO_DRAW"
}
```

Draw, with Holds
```bash
$ curl -s -X PUT "http://localhost:8080/game/2a8ab9d6-8f0c-463c-8036-81223dea7c62/draw?holds=2,3" | jq
{
  "id": "2a8ab9d6-8f0c-463c-8036-81223dea7c62",
  "deckSize": 44,
  "bet": 1,
  "credits": 51,
  "hand": [
    {
      "suit": "CLUB",
      "rank": "KING"
    },
    {
      "suit": "CLUB",
      "rank": "FIVE"
    },
    {
      "suit": "HEART",
      "rank": "SIX"
    },
    {
      "suit": "DIAMOND",
      "rank": "SIX"
    },
    {
      "suit": "DIAMOND",
      "rank": "FIVE"
    }
  ],
  "handRank": "TWO_PAIR",
  "gameState": "READY_TO_DEAL"
}
```

Increase Bet
```bash
$ curl -s -X PUT "http://localhost:8080/game/2a8ab9d6-8f0c-463c-8036-81223dea7c62/bet?amount=5" | jq
{
  "id": "2a8ab9d6-8f0c-463c-8036-81223dea7c62",
  "deckSize": 44,
  "bet": 5,
  "credits": 51,
  "hand": [
    {
      "suit": "CLUB",
      "rank": "KING"
    },
    {
      "suit": "CLUB",
      "rank": "FIVE"
    },
    {
      "suit": "HEART",
      "rank": "SIX"
    },
    {
      "suit": "DIAMOND",
      "rank": "SIX"
    },
    {
      "suit": "DIAMOND",
      "rank": "FIVE"
    }
  ],
  "handRank": "TWO_PAIR",
  "gameState": "READY_TO_DEAL"
}
```


