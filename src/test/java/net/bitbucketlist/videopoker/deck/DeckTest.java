package net.bitbucketlist.videopoker.deck;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DeckTest {
    private static List<Card> UNSHUFFLED_DECK;

    private final Deck subject = new Deck();

    @BeforeAll
    static void beforeAll() {
        UNSHUFFLED_DECK = new ArrayList<>();
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                UNSHUFFLED_DECK.add(new Card(suit, rank));
            }
        }
    }

    @Test
    void newDeck_has52Cards() {
        assertThat(subject.size()).isEqualTo(52);
    }

    @Test
    void newDeck_isUnshuffled() {
        assertThat(subject.deal(52)).isEqualTo(UNSHUFFLED_DECK);
    }

    @Test
    void deal() {
        List<Card> actual = subject.deal(5);

        assertThat(actual).isEqualTo(UNSHUFFLED_DECK.subList(0, 5));

        assertThat(subject.size()).isEqualTo(47);
    }

    @Test
    void deal_notEnoughCardsLeft_throwsException() {
        subject.deal(51);

        assertThatThrownBy(() -> subject.deal(2))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Can't deal 2 cards, only 1 card(s) left in deck");
    }

    @Test
    void shuffle() {
        subject.shuffle();

        List<Card> actual = subject.deal(52);

        assertThat(actual).isNotEqualTo(UNSHUFFLED_DECK);
    }
}
