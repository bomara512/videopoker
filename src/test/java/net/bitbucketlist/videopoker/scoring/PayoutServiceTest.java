package net.bitbucketlist.videopoker.scoring;

import net.bitbucketlist.videopoker.util.TestPokerHands;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PayoutServiceTest {

    PayoutService subject = new PayoutService();

    @Test
    void calculatePayout_royalFlush() {
        assertThat(subject.calculatePayout(TestPokerHands.ROYAL_FLUSH, 1)).isEqualTo(250);
        assertThat(subject.calculatePayout(TestPokerHands.ROYAL_FLUSH, 2)).isEqualTo(500);
        assertThat(subject.calculatePayout(TestPokerHands.ROYAL_FLUSH, 3)).isEqualTo(750);
        assertThat(subject.calculatePayout(TestPokerHands.ROYAL_FLUSH, 4)).isEqualTo(1000);
        assertThat(subject.calculatePayout(TestPokerHands.ROYAL_FLUSH, 5)).isEqualTo(4000);
    }

    @Test
    void calculatePayout_straightFlush() {
        assertThat(subject.calculatePayout(TestPokerHands.STRAIGHT_FLUSH, 1)).isEqualTo(50);
        assertThat(subject.calculatePayout(TestPokerHands.STRAIGHT_FLUSH, 2)).isEqualTo(100);
        assertThat(subject.calculatePayout(TestPokerHands.STRAIGHT_FLUSH, 3)).isEqualTo(150);
        assertThat(subject.calculatePayout(TestPokerHands.STRAIGHT_FLUSH, 4)).isEqualTo(200);
        assertThat(subject.calculatePayout(TestPokerHands.STRAIGHT_FLUSH, 5)).isEqualTo(250);
    }

    @Test
    void calculatePayout_FourOfAKind() {
        assertThat(subject.calculatePayout(TestPokerHands.FOUR_OF_A_KIND, 1)).isEqualTo(25);
        assertThat(subject.calculatePayout(TestPokerHands.FOUR_OF_A_KIND, 2)).isEqualTo(50);
        assertThat(subject.calculatePayout(TestPokerHands.FOUR_OF_A_KIND, 3)).isEqualTo(75);
        assertThat(subject.calculatePayout(TestPokerHands.FOUR_OF_A_KIND, 4)).isEqualTo(100);
        assertThat(subject.calculatePayout(TestPokerHands.FOUR_OF_A_KIND, 5)).isEqualTo(125);
    }

    @Test
    void calculatePayout_fullHouse() {
        assertThat(subject.calculatePayout(TestPokerHands.FULL_HOUSE, 1)).isEqualTo(9);
        assertThat(subject.calculatePayout(TestPokerHands.FULL_HOUSE, 2)).isEqualTo(18);
        assertThat(subject.calculatePayout(TestPokerHands.FULL_HOUSE, 3)).isEqualTo(27);
        assertThat(subject.calculatePayout(TestPokerHands.FULL_HOUSE, 4)).isEqualTo(36);
        assertThat(subject.calculatePayout(TestPokerHands.FULL_HOUSE, 5)).isEqualTo(45);
    }

    @Test
    void calculatePayout_flush() {
        assertThat(subject.calculatePayout(TestPokerHands.FLUSH, 1)).isEqualTo(6);
        assertThat(subject.calculatePayout(TestPokerHands.FLUSH, 2)).isEqualTo(12);
        assertThat(subject.calculatePayout(TestPokerHands.FLUSH, 3)).isEqualTo(18);
        assertThat(subject.calculatePayout(TestPokerHands.FLUSH, 4)).isEqualTo(24);
        assertThat(subject.calculatePayout(TestPokerHands.FLUSH, 5)).isEqualTo(30);
    }

    @Test
    void calculatePayout_straight() {
        assertThat(subject.calculatePayout(TestPokerHands.STRAIGHT, 1)).isEqualTo(4);
        assertThat(subject.calculatePayout(TestPokerHands.STRAIGHT, 2)).isEqualTo(8);
        assertThat(subject.calculatePayout(TestPokerHands.STRAIGHT, 3)).isEqualTo(12);
        assertThat(subject.calculatePayout(TestPokerHands.STRAIGHT, 4)).isEqualTo(16);
        assertThat(subject.calculatePayout(TestPokerHands.STRAIGHT, 5)).isEqualTo(20);
    }

    @Test
    void calculatePayout_threeOfAKind() {
        assertThat(subject.calculatePayout(TestPokerHands.THREE_OF_A_KIND, 1)).isEqualTo(3);
        assertThat(subject.calculatePayout(TestPokerHands.THREE_OF_A_KIND, 2)).isEqualTo(6);
        assertThat(subject.calculatePayout(TestPokerHands.THREE_OF_A_KIND, 3)).isEqualTo(9);
        assertThat(subject.calculatePayout(TestPokerHands.THREE_OF_A_KIND, 4)).isEqualTo(12);
        assertThat(subject.calculatePayout(TestPokerHands.THREE_OF_A_KIND, 5)).isEqualTo(15);
    }

    @Test
    void calculatePayout_twoPair() {
        assertThat(subject.calculatePayout(TestPokerHands.TWO_PAIR, 1)).isEqualTo(2);
        assertThat(subject.calculatePayout(TestPokerHands.TWO_PAIR, 2)).isEqualTo(4);
        assertThat(subject.calculatePayout(TestPokerHands.TWO_PAIR, 3)).isEqualTo(6);
        assertThat(subject.calculatePayout(TestPokerHands.TWO_PAIR, 4)).isEqualTo(8);
        assertThat(subject.calculatePayout(TestPokerHands.TWO_PAIR, 5)).isEqualTo(10);
    }

    @Test
    void calculatePayout_jacksOrBetter() {
        assertThat(subject.calculatePayout(TestPokerHands.JACKS_OR_BETTER, 1)).isEqualTo(1);
        assertThat(subject.calculatePayout(TestPokerHands.JACKS_OR_BETTER, 2)).isEqualTo(2);
        assertThat(subject.calculatePayout(TestPokerHands.JACKS_OR_BETTER, 3)).isEqualTo(3);
        assertThat(subject.calculatePayout(TestPokerHands.JACKS_OR_BETTER, 4)).isEqualTo(4);
        assertThat(subject.calculatePayout(TestPokerHands.JACKS_OR_BETTER, 5)).isEqualTo(5);
    }

    @Test
    void calculatePayout_highCard() {
        assertThat(subject.calculatePayout(TestPokerHands.HIGH_CARD, 1)).isEqualTo(0);
        assertThat(subject.calculatePayout(TestPokerHands.HIGH_CARD, 2)).isEqualTo(0);
        assertThat(subject.calculatePayout(TestPokerHands.HIGH_CARD, 3)).isEqualTo(0);
        assertThat(subject.calculatePayout(TestPokerHands.HIGH_CARD, 4)).isEqualTo(0);
        assertThat(subject.calculatePayout(TestPokerHands.HIGH_CARD, 5)).isEqualTo(0);
    }
}
