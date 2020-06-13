package net.bitbucketlist.videopoker.builder;

import net.bitbucketlist.videopoker.GameState;
import net.bitbucketlist.videopoker.dto.CardDto;
import net.bitbucketlist.videopoker.dto.GameDto;
import net.bitbucketlist.videopoker.scoring.PokerHandEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class GameDtoBuilder {
    private UUID id;
    private int deckSize = 52;
    private Integer bet = 1;
    private Integer balance = 50;
    private List<CardDto> hand = new ArrayList<>();
    private PokerHandEnum handRank;
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

    public GameDtoBuilder deckSize(int deckSize) {
        this.deckSize = deckSize;
        return this;
    }

    public GameDtoBuilder bet(int bet) {
        this.bet = bet;
        return this;
    }

    public GameDtoBuilder balance(int balance) {
        this.balance = balance;
        return this;
    }

    public GameDtoBuilder hand(List<CardDto> hand) {
        this.hand = hand;
        return this;
    }

    public GameDtoBuilder handRank(PokerHandEnum handRank) {
        this.handRank = handRank;
        return this;
    }

    public GameDtoBuilder gameState(GameState gameState) {
        this.gameState = gameState;
        return this;
    }

    public GameDto build() {
        return new GameDto(id, deckSize, bet, balance, hand, handRank, gameState);
    }
}
