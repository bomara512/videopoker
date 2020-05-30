package net.bitbucketlist.videopoker;

import net.bitbucketlist.videopoker.dto.GameDto;
import net.bitbucketlist.videopoker.persistence.GameEntity;
import org.springframework.stereotype.Component;

@Component
public class GameMapper {
    public GameDto mapToDto(GameEntity gameEntity) {
        return new GameDto(
            gameEntity.getId(),
            gameEntity.getDeck().size(),
            gameEntity.getCurrentBet()
        );
    }
}
