package net.bitbucketlist.videopoker.deck;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    List<Card> deck = new ArrayList<>(52);

    public Deck() {
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                deck.add(new Card(suit, rank));
            }
        }
    }

    public List<Card> deal(int numberOfCards) {
        if (size() < numberOfCards) {
            throw new IllegalArgumentException(
                String.format("Can't deal %s cards, only %s card(s) left in deck", numberOfCards, size())
            );
        }

        List<Card> dealtCards = new ArrayList<>();
        for (int i = 0; i < numberOfCards; i++) {
            Card dealtCard = deck.remove(0);
            dealtCards.add(dealtCard);
        }
        return dealtCards;
    }

    public int size() {
        return deck.size();
    }

    public void shuffle() {
        Collections.shuffle(deck);
    }
}
