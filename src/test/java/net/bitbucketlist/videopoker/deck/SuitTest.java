package net.bitbucketlist.videopoker.deck;

import org.junit.jupiter.api.Test;

import static net.bitbucketlist.videopoker.deck.Suit.*;
import static org.assertj.core.api.Assertions.assertThat;

class SuitTest {

    @Test
    void values() {
        assertThat(Suit.values()).containsExactly(
            HEART,
            CLUB,
            DIAMOND,
            SPADE
        );
    }
}
