package net.bitbucketlist.videopoker;

import lombok.RequiredArgsConstructor;
import net.bitbucketlist.videopoker.deck.Card;
import net.bitbucketlist.videopoker.deck.Deck;
import net.bitbucketlist.videopoker.dto.GameDto;
import net.bitbucketlist.videopoker.persistence.GameEntity;
import net.bitbucketlist.videopoker.persistence.GameRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GameService {
    private final GameRepository gameRepository;
    private final GameMapper gameMapper;

    public GameDto createGame() {
        GameEntity gameEntity = new GameEntity(new Deck(), 1);
        GameEntity game = gameRepository.save(gameEntity);

        return gameMapper.mapToDto(game);
    }

    public GameDto setCurrentBet(UUID gameId, int currentBet) {
        GameEntity gameEntity = getGameEntity(gameId);

        gameEntity.setCurrentBet(currentBet);

        gameRepository.save(gameEntity);

        return gameMapper.mapToDto(gameEntity);
    }

    public GameDto deal(UUID gameId) {
        GameEntity gameEntity = getGameEntity(gameId);

        List<Card> cards = gameEntity.getDeck().deal(5);

        gameEntity.setCurrentHand(cards);

        gameRepository.save(gameEntity);

        return gameMapper.mapToDto(gameEntity);
    }

    private GameEntity getGameEntity(UUID gameId) {
        return gameRepository
            .findById(gameId)
            .orElseThrow(() -> new InvalidGameStateException(
                    String.format("Game %s does not exist", gameId)
                )
            );
    }
}
