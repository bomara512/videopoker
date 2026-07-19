package net.bitbucketlist.poker;

import net.bitbucketlist.poker.dto.CardDto;
import net.bitbucketlist.poker.dto.GameDto;
import net.bitbucketlist.poker.persistence.GameEntity;
import net.bitbucketlist.poker.scoring.PokerHand;
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
