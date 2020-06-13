package net.bitbucketlist.videopoker;

import net.bitbucketlist.videopoker.dto.CardDto;
import net.bitbucketlist.videopoker.dto.GameDto;
import net.bitbucketlist.videopoker.persistence.GameEntity;
import net.bitbucketlist.videopoker.scoring.PokerHand;
import org.springframework.stereotype.Component;

import static java.util.stream.Collectors.toList;

@Component
public class GameMapper {
    public GameDto mapToDto(GameEntity gameEntity) {
        return new GameDto(
            gameEntity.getId(),
            gameEntity.getDeck().size(),
            gameEntity.getBet(),
            gameEntity.getCredits(),
            gameEntity.getHand()
                .stream()
                .map(card -> new CardDto(card.getSuit(), card.getRank()))
                .collect(toList()),
            new PokerHand(gameEntity.getHand()).calculateBestHand(),
            gameEntity.getGameState()
        );
    }
}
