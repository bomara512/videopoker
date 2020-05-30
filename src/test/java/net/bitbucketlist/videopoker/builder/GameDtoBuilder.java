package net.bitbucketlist.videopoker.builder;

import net.bitbucketlist.videopoker.GameState;
import net.bitbucketlist.videopoker.dto.CardDto;
import net.bitbucketlist.videopoker.dto.GameDto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class GameDtoBuilder {
    private UUID id;
    private int cardsRemainingInDeck = 52;
    private Integer currentBet = 1;
    private List<CardDto> currentHand = new ArrayList<>();
    private GameState gameState;

    private GameDtoBuilder() {
    }

    public static GameDtoBuilder gameDtoBuilder() {
        return new GameDtoBuilder();
    }

    public GameDtoBuilder id(UUID id) {
        this.id = id;
        return this;
    }

    public GameDtoBuilder cardsRemainingInDeck(int cardsRemainingInDeck) {
        this.cardsRemainingInDeck = cardsRemainingInDeck;
        return this;
    }

    public GameDtoBuilder currentBet(int currentBet) {
        this.currentBet = currentBet;
        return this;
    }

    public GameDtoBuilder currentHand(List<CardDto> currentHand) {
        this.currentHand = currentHand;
        return this;
    }

    public GameDtoBuilder gameState(GameState gameState) {
        this.gameState = gameState;
        return this;
    }

    public GameDto build() {
        return new GameDto(id, cardsRemainingInDeck, currentBet, currentHand, gameState);
    }
}
