package net.bitbucketlist.videopoker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.bitbucketlist.videopoker.GameState;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameDto {
    private UUID id;
    private int cardsRemainingInDeck;
    private int currentBet;
    private List<CardDto> currentHand;
    private GameState gameState;
}