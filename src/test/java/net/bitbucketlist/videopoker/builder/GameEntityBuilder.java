package net.bitbucketlist.videopoker.builder;

import net.bitbucketlist.videopoker.deck.Card;
import net.bitbucketlist.videopoker.deck.Deck;
import net.bitbucketlist.videopoker.persistence.GameEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class GameEntityBuilder {
    private UUID id;
    private Deck deck = new Deck();
    private Integer currentBet = 1;
    private Integer currentBalance = 50;
    private List<Card> currentHand = new ArrayList<>();

    private GameEntityBuilder() {
    }

    public static GameEntityBuilder gameEntityBuilder() {
        return new GameEntityBuilder();
    }

    public GameEntityBuilder id(UUID id) {
        this.id = id;
        return this;
    }

    public GameEntityBuilder deck(Deck deck) {
        this.deck = deck;
        return this;
    }

    public GameEntityBuilder currentBet(Integer currentBet) {
        this.currentBet = currentBet;
        return this;
    }

    public GameEntityBuilder currentBalance(Integer currentBalance) {
        this.currentBalance = currentBalance;
        return this;
    }

    public GameEntityBuilder currentHand(List<Card> currentHand) {
        this.currentHand = currentHand;
        return this;
    }

    public GameEntity build() {
        GameEntity gameEntity = new GameEntity(deck, currentBet, currentBalance);
        gameEntity.setId(id);
        gameEntity.setCurrentHand(currentHand);

        return gameEntity;
    }
}
