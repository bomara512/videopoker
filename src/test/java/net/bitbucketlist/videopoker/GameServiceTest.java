package net.bitbucketlist.videopoker;

import net.bitbucketlist.videopoker.deck.Deck;
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

import java.util.Optional;
import java.util.UUID;

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

        GameDto expected = new GameDto();
        when(mockGameMapper.mapToDto(any(GameEntity.class))).thenReturn(expected);

        GameDto actual = subject.createGame();

        assertThat(actual).isSameAs(expected);

        verify(mockGameRepository).save(gameEntityCaptor.capture());

        assertThat(gameEntityCaptor.getValue().getDeck()).isNotEqualTo(UNSHUFFLED_DECK);
        assertThat(gameEntityCaptor.getValue().getCurrentBet()).isEqualTo(1);
        assertThat(gameEntityCaptor.getValue().getCurrentHand()).isEmpty();
        assertThat(gameEntityCaptor.getValue().getDeck().size()).isEqualTo(52);
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

        GameDto expected = new GameDto();
        when(mockGameMapper.mapToDto(gameEntity)).thenReturn(expected);

        GameDto actual = subject.deal(gameId);

        verify(mockGameRepository).save(gameEntityCaptor.capture());

        assertThat(actual).isSameAs(expected);
        assertThat(gameEntityCaptor.getValue().getCurrentHand().size()).isEqualTo(5);
    }

    @Test
    void deal_gameNotFound() {
        when(mockGameRepository.findById(gameId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subject.deal(gameId))
            .isInstanceOf(InvalidGameStateException.class)
            .hasMessage(String.format("Game %s does not exist", gameId));
    }
}

