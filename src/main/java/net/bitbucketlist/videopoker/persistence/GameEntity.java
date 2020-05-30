package net.bitbucketlist.videopoker.persistence;

import lombok.Data;
import lombok.NonNull;
import net.bitbucketlist.videopoker.deck.Deck;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.UUID;

@Data
@RedisHash("games")
public class GameEntity {
    @Id
    private UUID id;

    @NonNull
    private Deck deck;

    @NonNull
    private Integer currentBet;
}
