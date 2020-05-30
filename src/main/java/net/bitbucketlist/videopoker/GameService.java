package net.bitbucketlist.videopoker;

import lombok.RequiredArgsConstructor;
import net.bitbucketlist.videopoker.deck.Deck;
import net.bitbucketlist.videopoker.dto.GameDto;
import net.bitbucketlist.videopoker.persistence.GameEntity;
import net.bitbucketlist.videopoker.persistence.GameRepository;
import org.springframework.stereotype.Service;

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
        GameEntity gameEntity = gameRepository
            .findById(gameId)
            .orElseThrow(() -> new InvalidGameStateException(
                    String.format("Game %s does not exist", gameId)
                )
            );

        gameEntity.setCurrentBet(currentBet);

        gameRepository.save(gameEntity);

        return gameMapper.mapToDto(gameEntity);
    }
}
