package net.bitbucketlist.videopoker;

import net.bitbucketlist.videopoker.deck.Card;
import net.bitbucketlist.videopoker.deck.Deck;
import net.bitbucketlist.videopoker.deck.Rank;
import net.bitbucketlist.videopoker.deck.Suit;
import net.bitbucketlist.videopoker.dto.GameDto;
import net.bitbucketlist.videopoker.persistence.GameEntity;
import net.bitbucketlist.videopoker.persistence.GameRepository;
import net.bitbucketlist.videopoker.scoring.PayoutService;
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
    @Mock
    GameRepository mockGameRepository;
    @Mock
    GameMapper mockGameMapper;
    @Mock
    PayoutService mockPayoutService;
    @InjectMocks
    GameService subject;
    UUID gameId;
    GameEntity gameEntity;
    ArgumentCaptor<GameEntity> gameEntityCaptor = ArgumentCaptor.forClass(GameEntity.class);
    private Deck UNSHUFFLED_DECK;

    @BeforeEach
    void setUp() {
        gameId = UUID.randomUUID();
        gameEntity = gameEntityBuilder().id(gameId).build();
        UNSHUFFLED_DECK = new Deck();
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
        assertThat(gameEntityCaptor.getValue().getBet()).isEqualTo(1);
        assertThat(gameEntityCaptor.getValue().getHand()).isEmpty();
        assertThat(gameEntityCaptor.getValue().getDeck().size()).isEqualTo(52);
        assertThat(gameEntityCaptor.getValue().getCredits()).isEqualTo(50);
        assertThat(gameEntityCaptor.getValue().getGameState()).isEqualTo(GameState.READY_TO_DEAL);
    }

    @Test
    void setBet() {
        when(mockGameRepository.findById(gameId)).thenReturn(Optional.of(gameEntity));

        subject.setBet(gameId, 5);

        verify(mockGameRepository).save(gameEntityCaptor.capture());

        assertThat(gameEntityCaptor.getValue().getBet()).isEqualTo(5);
    }

    @Test
    void setBet_gameNotFound() {
        when(mockGameRepository.findById(gameId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subject.setBet(gameId, 5))
            .isInstanceOf(InvalidGameStateException.class)
            .hasMessage(String.format("Game %s does not exist", gameId));
    }

    @Test
    void setBet_overMaxBet() {
        when(mockGameRepository.findById(gameId)).thenReturn(Optional.of(gameEntity));

        subject.setBet(gameId, 5);

        assertThatThrownBy(() -> subject.setBet(gameId, 6))
            .isInstanceOf(InvalidGameStateException.class)
            .hasMessage("Bet must be between 1 and 5. gameId: " + gameId);
    }

    @Test
    void setBet_underMinBet() {
        when(mockGameRepository.findById(gameId)).thenReturn(Optional.of(gameEntity));

        subject.setBet(gameId, 1);

        assertThatThrownBy(() -> subject.setBet(gameId, -1))
            .isInstanceOf(InvalidGameStateException.class)
            .hasMessage("Bet must be between 1 and 5. gameId: " + gameId);
    }

    @Test
    void setBet_notEnoughCredits() {
        gameEntity.setCredits(3);
        when(mockGameRepository.findById(gameId)).thenReturn(Optional.of(gameEntity));

        assertThatThrownBy(() -> subject.setBet(gameId, 5))
            .isInstanceOf(InvalidGameStateException.class)
            .hasMessage(
                String.format("Not enough credits (%s requested, %s available). gameId: %s", 5, 3, gameId)
            );
    }

    @Test
    void deal() {
        when(mockGameRepository.findById(gameId)).thenReturn(Optional.of(gameEntity));

        GameDto expected = gameDtoBuilder().build();
        when(mockGameMapper.mapToDto(gameEntity)).thenReturn(expected);

        GameDto actual = subject.deal(gameId);

        verify(mockGameRepository).save(gameEntityCaptor.capture());

        assertThat(actual).isSameAs(expected);
        assertThat(gameEntityCaptor.getValue().getHand().size()).isEqualTo(5);
        assertThat(gameEntityCaptor.getValue().getCredits()).isEqualTo(49);
        assertThat(gameEntityCaptor.getValue().getGameState()).isEqualTo(GameState.READY_TO_DRAW);
    }

    @Test
    void deal_notEnoughCredits() {
        gameEntity.setCredits(3);
        gameEntity.setBet(5);
        when(mockGameRepository.findById(gameId)).thenReturn(Optional.of(gameEntity));

        assertThatThrownBy(() -> subject.deal(gameId))
            .isInstanceOf(InvalidGameStateException.class)
            .hasMessage(
                String.format("Not enough credits (%s requested, %s available). gameId: %s", 5, 3, gameId)
            );
    }

    @Test
    void deal_notEnoughCardsLeft_startsFreshDeck() {
        gameEntity.getDeck().deal(42);
        when(mockGameRepository.findById(gameId)).thenReturn(Optional.of(gameEntity));

        GameDto expected = gameDtoBuilder().build();
        when(mockGameMapper.mapToDto(gameEntity)).thenReturn(expected);

        GameDto actual = subject.deal(gameId);

        verify(mockGameRepository).save(gameEntityCaptor.capture());

        assertThat(actual).isSameAs(expected);
        assertThat(gameEntityCaptor.getValue().getHand().size()).isEqualTo(5);
        assertThat(gameEntityCaptor.getValue().getDeck().size()).isEqualTo(47);
        assertThat(gameEntityCaptor.getValue().getGameState()).isEqualTo(GameState.READY_TO_DRAW);

        List<Card> freshDeck = new ArrayList<>();
        freshDeck.addAll(gameEntityCaptor.getValue().getHand());
        freshDeck.addAll(gameEntityCaptor.getValue().getDeck().deal(47));
        assertThat(freshDeck).isNotEqualTo(UNSHUFFLED_DECK.deal(52));
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
        gameEntity.setHand(new ArrayList<>(originalHand));
        gameEntity.setCredits(100);

        when(mockGameRepository.findById(gameId)).thenReturn(Optional.of(gameEntity));
        when(mockPayoutService.calculatePayout(gameEntity.getHand(), gameEntity.getBet())).thenReturn(200);

        subject.draw(gameId, List.of(0, 4));

        verify(mockGameRepository).save(gameEntityCaptor.capture());

        assertThat(gameEntityCaptor.getValue().getGameState()).isEqualTo(GameState.READY_TO_DEAL);
        assertThat(gameEntityCaptor.getValue().getCredits()).isEqualTo(300);

        List<Card> updatedHand = gameEntityCaptor.getValue().getHand();

        assertThat(updatedHand.get(0)).isEqualTo(originalHand.get(0));
        assertThat(updatedHand.get(1)).isNotEqualTo(originalHand.get(1));
        assertThat(updatedHand.get(2)).isNotEqualTo(originalHand.get(2));
        assertThat(updatedHand.get(3)).isNotEqualTo(originalHand.get(3));
        assertThat(updatedHand.get(4)).isEqualTo(originalHand.get(4));
    }

    @Test
    void draw_notEnoughCardsLeft_throwsException() {
        gameEntity.getDeck().deal(45);
        gameEntity.setHand(gameEntity.getDeck().deal(5));
        gameEntity.setGameState(GameState.READY_TO_DRAW);

        when(mockGameRepository.findById(gameId)).thenReturn(Optional.of(gameEntity));

        assertThatThrownBy(() -> subject.draw(gameId, Collections.emptyList()))
            .isInstanceOf(InvalidGameStateException.class)
            .hasMessage("Unable to draw for gameId: " + gameId);
    }

    @Test
    void draw_notEnoughCardsLeft_startsFreshDeck() {
        gameEntity.getDeck().deal(42);
        gameEntity.setHand(gameEntity.getDeck().deal(5));
        gameEntity.setGameState(GameState.READY_TO_DRAW);

        when(mockGameRepository.findById(gameId)).thenReturn(Optional.of(gameEntity));

        GameDto expected = gameDtoBuilder().build();
        when(mockGameMapper.mapToDto(gameEntity)).thenReturn(expected);

        GameDto actual = subject.draw(gameId, List.of(0, 1, 2, 3, 4));

        verify(mockGameRepository).save(gameEntityCaptor.capture());

        assertThat(actual).isSameAs(expected);
        assertThat(gameEntityCaptor.getValue().getHand().size()).isEqualTo(5);
        assertThat(gameEntityCaptor.getValue().getDeck().size()).isEqualTo(52);
        assertThat(gameEntityCaptor.getValue().getGameState()).isEqualTo(GameState.READY_TO_DEAL);

        List<Card> freshDeck = new ArrayList<>();
        freshDeck.addAll(gameEntityCaptor.getValue().getHand());
        freshDeck.addAll(gameEntityCaptor.getValue().getDeck().deal(47));
        assertThat(freshDeck).isNotEqualTo(UNSHUFFLED_DECK.deal(52));
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

    @Test
    void getAllGames() {
        GameEntity gameEntity1 = gameEntityBuilder().id(UUID.randomUUID()).build();
        GameEntity gameEntity2 = gameEntityBuilder().id(UUID.randomUUID()).build();
        when(mockGameRepository.findAll()).thenReturn(List.of(gameEntity1, gameEntity2));

        GameDto gameDto1 = gameDtoBuilder().build();
        GameDto gameDto2 = gameDtoBuilder().build();
        when(mockGameMapper.mapToDto(gameEntity1)).thenReturn(gameDto1);
        when(mockGameMapper.mapToDto(gameEntity2)).thenReturn(gameDto2);

        List<GameDto> actual = subject.getAllGames();

        assertThat(actual).containsExactly(gameDto1, gameDto2);
    }
}

