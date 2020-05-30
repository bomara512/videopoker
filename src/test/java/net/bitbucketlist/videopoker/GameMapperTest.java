package net.bitbucketlist.videopoker;

import net.bitbucketlist.videopoker.deck.Card;
import net.bitbucketlist.videopoker.deck.Deck;
import net.bitbucketlist.videopoker.dto.CardDto;
import net.bitbucketlist.videopoker.dto.GameDto;
import net.bitbucketlist.videopoker.persistence.GameEntity;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static net.bitbucketlist.videopoker.builder.GameDtoBuilder.gameDtoBuilder;
import static net.bitbucketlist.videopoker.builder.GameEntityBuilder.gameEntityBuilder;
import static org.assertj.core.api.Assertions.assertThat;

class GameMapperTest {

    GameMapper subject = new GameMapper();

    @Test
    void mapToDto_newGame() {
        UUID gameId = UUID.randomUUID();

        GameEntity gameEntity = new GameEntity(new Deck(), 1);
        gameEntity.setId(gameId);

        GameDto actual = subject.mapToDto(gameEntity);

        GameDto expected = new GameDto(gameId, 52, 1, emptyList());

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void mapToDto_handDealt() {
        UUID gameId = UUID.randomUUID();

        Deck deck = new Deck();
        List<Card> currentHand = deck.deal(5);

        GameEntity gameEntity = gameEntityBuilder()
            .id(gameId)
            .currentBet(1)
            .deck(deck)
            .currentHand(currentHand)
            .build();

        GameDto actual = subject.mapToDto(gameEntity);

        List<CardDto> expectedHand = currentHand
            .stream()
            .map(card -> new CardDto(card.getSuit(), card.getRank()))
            .collect(Collectors.toList());

        GameDto expected = gameDtoBuilder()
            .id(gameId)
            .currentBet(1)
            .cardsRemainingInDeck(47)
            .currentHand(expectedHand)
            .build();

        assertThat(actual).isEqualTo(expected);
    }
}
