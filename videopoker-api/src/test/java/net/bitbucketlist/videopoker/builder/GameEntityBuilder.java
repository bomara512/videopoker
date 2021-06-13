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
    private Integer bet = 1;
    private Integer credits = 50;
    private List<Card> hand = new ArrayList<>();

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

    public GameEntityBuilder bet(Integer bet) {
        this.bet = bet;
        return this;
    }

    public GameEntityBuilder credits(Integer credits) {
        this.credits = credits;
        return this;
    }

    public GameEntityBuilder hand(List<Card> hand) {
        this.hand = hand;
        return this;
    }

    public GameEntity build() {
        GameEntity gameEntity = new GameEntity(deck, bet, credits);
        gameEntity.setId(id);
        gameEntity.setHand(hand);

        return gameEntity;
    }
}
