package net.bitbucketlist.videopoker;

import net.bitbucketlist.videopoker.deck.Card;
import net.bitbucketlist.videopoker.deck.Deck;
import net.bitbucketlist.videopoker.deck.Rank;
import net.bitbucketlist.videopoker.deck.Suit;
import net.bitbucketlist.videopoker.dto.GameDto;
import net.bitbucketlist.videopoker.persistence.GameEntity;
import net.bitbucketlist.videopoker.persistence.GameRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static net.bitbucketlist.videopoker.builder.GameDtoBuilder.gameDtoBuilder;
import static net.bitbucketlist.videopoker.builder.GameEntityBuilder.gameEntityBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {
    private static final Deck UNSHUFFLED_DECK = new Deck();

    @Mock
    GameRepository mockGameRepository;

    @Mock
    GameMapper mockGameMapper;

    @InjectMocks
    GameService subject;

    UUID gameId;
    GameEntity gameEntity;
    ArgumentCaptor<GameEntity> gameEntityCaptor = ArgumentCaptor.forClass(GameEntity.class);

    @BeforeEach
    void setUp() {
        gameId = UUID.randomUUID();
        gameEntity = gameEntityBuilder().id(gameId).build();
    }

    @Test
    void createGame() {
        when(mockGameRepository.save(any(GameEntity.class))).thenReturn(gameEntity);

        GameDto expected = gameDtoBuilder().build();
        when(mockGameMapper.mapToDto(any(GameEntity.class))).thenReturn(expected);

        GameDto actual = subject.createGame();

        assertThat(actual).isSameAs(expected);

        verify(mockGameRepository).save(gameEntityCaptor.capture());

        assertThat(gameEntityCaptor.getValue().getDeck()).isNotEqualTo(UNSHUFFLED_DECK);
        assertThat(gameEntityCaptor.getValue().getCurrentBet()).isEqualTo(1);
        assertThat(gameEntityCaptor.getValue().getCurrentHand()).isEmpty();
        assertThat(gameEntityCaptor.getValue().getDeck().size()).isEqualTo(52);
        assertThat(gameEntityCaptor.getValue().getCurrentBalance()).isEqualTo(50);
    }

    @Test
    void setCurrentBet() {
        when(mockGameRepository.findById(gameId)).thenReturn(Optional.of(gameEntity));

        subject.setCurrentBet(gameId, 5);

        verify(mockGameRepository).save(gameEntityCaptor.capture());

        assertThat(gameEntityCaptor.getValue().getCurrentBet()).isEqualTo(5);
    }

    @Test
    void setCurrentBet_gameNotFound() {
        when(mockGameRepository.findById(gameId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subject.setCurrentBet(gameId, 5))
            .isInstanceOf(InvalidGameStateException.class)
            .hasMessage(String.format("Game %s does not exist", gameId));
    }

    @Test
    void deal() {
        when(mockGameRepository.findById(gameId)).thenReturn(Optional.of(gameEntity));

        GameDto expected = gameDtoBuilder().build();
        when(mockGameMapper.mapToDto(gameEntity)).thenReturn(expected);

        GameDto actual = subject.deal(gameId);

        verify(mockGameRepository).save(gameEntityCaptor.capture());

        assertThat(actual).isSameAs(expected);
        assertThat(gameEntityCaptor.getValue().getCurrentHand().size()).isEqualTo(5);
        assertThat(gameEntityCaptor.getValue().getCurrentBalance()).isEqualTo(49);
    }

    @Test
    void deal_gameNotFound() {
        when(mockGameRepository.findById(gameId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subject.deal(gameId))
            .isInstanceOf(InvalidGameStateException.class)
            .hasMessage(String.format("Game %s does not exist", gameId));
    }

    @Test
    void deal_invalidState() {
        gameEntity.setGameState(GameState.READY_TO_DRAW);
        when(mockGameRepository.findById(gameId)).thenReturn(Optional.of(gameEntity));

        assertThatThrownBy(() -> subject.deal(gameId))
            .isInstanceOf(InvalidGameStateException.class)
            .hasMessage("Unable to deal for gameId: " + gameId);
    }

    @Test
    void draw() {
        List<Card> originalHand = new ArrayList<>();
        originalHand.add(new Card(Suit.HEART, Rank.ACE));
        originalHand.add(new Card(Suit.HEART, Rank.TWO));
        originalHand.add(new Card(Suit.HEART, Rank.THREE));
        originalHand.add(new Card(Suit.HEART, Rank.FOUR));
        originalHand.add(new Card(Suit.HEART, Rank.FIVE));

        gameEntity.setGameState(GameState.READY_TO_DRAW);
        gameEntity.setCurrentHand(new ArrayList<>(originalHand));

        when(mockGameRepository.findById(gameId)).thenReturn(Optional.of(gameEntity));

        subject.draw(gameId, List.of(0, 4));

        verify(mockGameRepository).save(gameEntityCaptor.capture());

        assertThat(gameEntityCaptor.getValue().getGameState()).isEqualTo(GameState.READY_TO_DEAL);

        List<Card> updatedHand = gameEntityCaptor.getValue().getCurrentHand();

        assertThat(updatedHand.get(0)).isEqualTo(originalHand.get(0));
        assertThat(updatedHand.get(1)).isNotEqualTo(originalHand.get(1));
        assertThat(updatedHand.get(2)).isNotEqualTo(originalHand.get(2));
        assertThat(updatedHand.get(3)).isNotEqualTo(originalHand.get(3));
        assertThat(updatedHand.get(4)).isEqualTo(originalHand.get(4));
    }

    @Test
    void draw_gameNotFound() {
        when(mockGameRepository.findById(gameId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subject.deal(gameId))
            .isInstanceOf(InvalidGameStateException.class)
            .hasMessage(String.format("Game %s does not exist", gameId));
    }

    @Test
    void draw_invalidState() {
        gameEntity.setGameState(GameState.READY_TO_DEAL);
        when(mockGameRepository.findById(gameId)).thenReturn(Optional.of(gameEntity));

        assertThatThrownBy(() -> subject.draw(gameId, Collections.emptyList()))
            .isInstanceOf(InvalidGameStateException.class)
            .hasMessage("Unable to draw for gameId: " + gameId);
    }
}

