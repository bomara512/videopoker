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

        if (gameEntity.getGameState() != GameState.READY_TO_DEAL) {
            throw new InvalidGameStateException("Unable to deal for gameId: " + gameId);
        }

        gameEntity.setGameState(GameState.READY_TO_DRAW);

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

    public GameDto draw(UUID gameId, List<Integer> holds) {
        GameEntity gameEntity = getGameEntity(gameId);

        if (gameEntity.getGameState() != GameState.READY_TO_DRAW) {
            throw new InvalidGameStateException("Unable to draw for gameId: " + gameId);
        }

        gameEntity.setGameState(GameState.READY_TO_DEAL);

        List<Card> currentHand = gameEntity.getCurrentHand();

        for (int i = 0; i < currentHand.size(); i++) {
            if (!holds.contains(i)) {
                currentHand.set(i, gameEntity.getDeck().deal(1).get(0));
            }
        }

        gameEntity = gameRepository.save(gameEntity);

        return gameMapper.mapToDto(gameEntity);
    }
}
