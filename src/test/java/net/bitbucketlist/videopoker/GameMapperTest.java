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
        gameEntity.setHand(emptyList());

        GameDto actual = subject.mapToDto(gameEntity);

        GameDto expected = new GameDto(gameId, 52, 1, 50, emptyList(), null, GameState.READY_TO_DEAL);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void mapToDto_handDealt() {
        UUID gameId = UUID.randomUUID();

        Deck deck = new Deck();
        List<Card> hand = deck.deal(5);

        GameEntity gameEntity = new GameEntity(deck, 1, 50);
        gameEntity.setId(gameId);
        gameEntity.setDeck(deck);
        gameEntity.setHand(hand);

        GameDto actual = subject.mapToDto(gameEntity);

        List<CardDto> expectedHand = hand
            .stream()
            .map(card -> new CardDto(card.getSuit(), card.getRank()))
            .collect(Collectors.toList());

        GameDto expected = new GameDto(gameId, 47, 1, 50, expectedHand, PokerHandEnum.STRAIGHT_FLUSH, GameState.READY_TO_DEAL);

        assertThat(actual).isEqualTo(expected);
    }
}
