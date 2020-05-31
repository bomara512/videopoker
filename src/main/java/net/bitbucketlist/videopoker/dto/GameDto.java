package net.bitbucketlist.videopoker.dto;

import lombok.Value;
import net.bitbucketlist.videopoker.GameState;

import java.util.List;
import java.util.UUID;

@Value
public class GameDto {
    UUID id;
    int cardsRemainingInDeck;
    int currentBet;
    int currentBalance;
    List<CardDto> currentHand;
    GameState gameState;
}
