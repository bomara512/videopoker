package net.bitbucketlist.videopoker.persistence;

import lombok.Data;
import lombok.NonNull;
import net.bitbucketlist.videopoker.GameState;
import net.bitbucketlist.videopoker.deck.Card;
import net.bitbucketlist.videopoker.deck.Deck;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@RedisHash(value = "games", timeToLive = 60 * 60 * 24 * 7)
public class GameEntity {
    @Id
    private UUID id;

    @NonNull
    private Deck deck;

    @NonNull
    private Integer currentBet;

    @NonNull
    private List<Card> currentHand = new ArrayList<>();

    @NonNull
    private GameState gameState = GameState.READY_TO_DEAL;

    @NonNull
    private Integer currentBalance;
}
