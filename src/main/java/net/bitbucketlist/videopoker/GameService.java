package net.bitbucketlist.videopoker;

import lombok.RequiredArgsConstructor;
import net.bitbucketlist.videopoker.deck.Deck;
import net.bitbucketlist.videopoker.dto.GameDto;
import net.bitbucketlist.videopoker.persistence.GameEntity;
import net.bitbucketlist.videopoker.persistence.GameRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class GameService {
    private final GameRepository gameRepository;
    private final GameMapper gameMapper;

    public GameDto createGame() {
        GameEntity gameEntity = new GameEntity(new Deck(), 1, new ArrayList<>());
        GameEntity game = gameRepository.save(gameEntity);

        return gameMapper.mapToDto(game);
    }
}
