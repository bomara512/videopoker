package net.bitbucketlist.videopoker;

import net.bitbucketlist.videopoker.deck.Card;
import net.bitbucketlist.videopoker.deck.Deck;
import net.bitbucketlist.videopoker.dto.CardDto;
import net.bitbucketlist.videopoker.dto.GameDto;
import net.bitbucketlist.videopoker.persistence.GameEntity;
import net.bitbucketlist.videopoker.scoring.PokerHandEnum;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

class GameMapperTest {

    GameMapper subject = new GameMapper();

    @Test
    void mapToDto_newGame() {
        UUID gameId = UUID.randomUUID();

        GameEntity gameEntity = new GameEntity(new Deck(), 1, 50);
        gameEntity.setId(gameId);
        gameEntity.setCurrentHand(emptyList());

        GameDto actual = subject.mapToDto(gameEntity);

        GameDto expected = new GameDto(gameId, 52, 1, 50, emptyList(), GameState.READY_TO_DEAL, null);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void mapToDto_handDealt() {
        UUID gameId = UUID.randomUUID();

        Deck deck = new Deck();
        List<Card> currentHand = deck.deal(5);

        GameEntity gameEntity = new GameEntity(deck, 1, 50);
        gameEntity.setId(gameId);
        gameEntity.setDeck(deck);
        gameEntity.setCurrentHand(currentHand);

        GameDto actual = subject.mapToDto(gameEntity);

        List<CardDto> expectedHand = currentHand
            .stream()
            .map(card -> new CardDto(card.getSuit(), card.getRank()))
            .collect(Collectors.toList());

        GameDto expected = new GameDto(gameId, 47, 1, 50, expectedHand, GameState.READY_TO_DEAL, PokerHandEnum.STRAIGHT_FLUSH);

        assertThat(actual).isEqualTo(expected);
    }
}
