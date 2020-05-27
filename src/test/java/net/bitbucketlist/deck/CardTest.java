package net.bitbucketlist.deck;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static net.bitbucketlist.deck.Suit.*;
import static org.assertj.core.api.Assertions.assertThat;

class CardTest {

    private static final Map<Suit, Character> SUIT_DISPLAYS = Map.of(
        HEART, 'H',
        CLUB, 'C',
        DIAMOND, 'D',
        SPADE, 'S'
    );

    private static final List<String> RANK_DISPLAYS = List.of(
        "A", "2", "3", "4", "5", "6", "7",
        "8", "9", "10", "J", "Q", "K"
    );

    @Test
    void getDisplay() {
        Arrays.stream(Suit.values()).forEach(suit -> {
            List<String> actual = Arrays
                .stream(Rank.values())
                .map(rank -> new Card(suit, rank).getDisplay())
                .collect(toList());

            List<String> expected = RANK_DISPLAYS
                .stream()
                .map(rankDisplay -> rankDisplay + SUIT_DISPLAYS.get(suit))
                .collect(toList());

            assertThat(actual).isEqualTo(expected);
        });
    }
}
