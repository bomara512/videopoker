package net.bitbucketlist.videopoker;

import net.bitbucketlist.videopoker.deck.Deck;
import net.bitbucketlist.videopoker.dto.GameDto;
import net.bitbucketlist.videopoker.persistence.GameEntity;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class GameMapperTest {

    GameMapper subject = new GameMapper();

    @Test
    void mapToDto_newGame() {
        UUID gameId = UUID.randomUUID();

        GameEntity gameEntity = new GameEntity(new Deck(), 1);
        gameEntity.setId(gameId);

        GameDto actual = subject.mapToDto(gameEntity);

        GameDto expected = new GameDto(gameId, 52, 1);

        assertThat(actual).isEqualTo(expected);
    }
}
