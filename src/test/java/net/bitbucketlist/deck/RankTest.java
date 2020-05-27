package net.bitbucketlist.deck;

import org.junit.jupiter.api.Test;

import static net.bitbucketlist.deck.Rank.*;
import static org.assertj.core.api.Assertions.assertThat;

class RankTest {

    @Test
    void values() {
        assertThat(Rank.values()).containsExactly(
            ACE,
            TWO,
            THREE,
            FOUR,
            FIVE,
            SIX,
            SEVEN,
            EIGHT,
            NINE,
            TEN,
            JACK,
            QUEEN,
            KING
        );
    }
}
