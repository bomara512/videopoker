package net.bitbucketlist.videopoker.scoring;

import net.bitbucketlist.videopoker.deck.Card;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static net.bitbucketlist.videopoker.deck.Rank.*;
import static net.bitbucketlist.videopoker.deck.Suit.*;
import static net.bitbucketlist.videopoker.util.TestPokerHands.*;
import static org.assertj.core.api.Assertions.assertThat;

class PokerHandTest {

    @Test
    void isRoyalFlush() {
        PokerHand subject = new PokerHand(ROYAL_FLUSH);
        assertThat(subject.isRoyalFlush()).isEqualTo(true);
    }

    @Test
    void isRoyalFlush_notFlush_returnsFalse() {
        PokerHand subject = new PokerHand(
            List.of(
                new Card(HEART, ACE),
                new Card(HEART, KING),
                new Card(HEART, QUEEN),
                new Card(HEART, JACK),
                new Card(SPADE, TEN)
            ));

        assertThat(subject.isRoyalFlush()).isEqualTo(false);
    }

    @Test
    void isRoyalFlush_notStraight_returnsFalse() {
        PokerHand subject = new PokerHand(
            List.of(
                new Card(HEART, ACE),
                new Card(HEART, KING),
                new Card(HEART, QUEEN),
                new Card(HEART, JACK),
                new Card(HEART, FOUR)
            ));

        assertThat(subject.isRoyalFlush()).isEqualTo(false);
    }

    @Test
    void isRoyalFlush_notRoyal_returnsFalse() {
        PokerHand subject = new PokerHand(
            List.of(
                new Card(HEART, KING),
                new Card(HEART, QUEEN),
                new Card(HEART, JACK),
                new Card(HEART, TEN),
                new Card(HEART, NINE)
            ));

        assertThat(subject.isRoyalFlush()).isEqualTo(false);
    }

    @Test
    void isStraightFlush() {
        PokerHand subject = new PokerHand(STRAIGHT_FLUSH);
        assertThat(subject.isStraightFlush()).isEqualTo(true);
    }

    @Test
    void isStraightFlush_notStraight_returnsFalse() {
        PokerHand subject = new PokerHand(
            List.of(
                new Card(HEART, EIGHT),
                new Card(HEART, FIVE),
                new Card(HEART, FOUR),
                new Card(HEART, THREE),
                new Card(HEART, TWO)
            ));

        assertThat(subject.isStraightFlush()).isEqualTo(false);
    }

    @Test
    void isStraightFlush_notFlush_returnsFalse() {
        PokerHand subject = new PokerHand(
            List.of(
                new Card(HEART, SIX),
                new Card(HEART, FIVE),
                new Card(HEART, FOUR),
                new Card(HEART, THREE),
                new Card(SPADE, TWO)
            ));

        assertThat(subject.isStraightFlush()).isEqualTo(false);
    }

    @Test
    void isStraightFlush_royalFlush_returnsFalse() {
        PokerHand subject = new PokerHand(ROYAL_FLUSH);
        assertThat(subject.isStraightFlush()).isEqualTo(false);
    }

    @Test
    void isFourOfAKind() {
        PokerHand subject = new PokerHand(FOUR_OF_A_KIND);
        assertThat(subject.isFourOfAKind()).isEqualTo(true);
    }

    @Test
    void isFourOfAKind_returnsFalse() {
        PokerHand subject = new PokerHand(
            List.of(
                new Card(HEART, SEVEN),
                new Card(CLUB, SEVEN),
                new Card(SPADE, SEVEN),
                new Card(DIAMOND, SIX),
                new Card(SPADE, SIX)
            )
        );

        assertThat(subject.isFourOfAKind()).isEqualTo(false);
    }

    @Test
    void isFullHouse() {
        PokerHand subject = new PokerHand(FULL_HOUSE);
        assertThat(subject.isFullHouse()).isEqualTo(true);
    }

    @Test
    void isFullHouse_returnsFalse() {
        PokerHand subject = new PokerHand(
            List.of(
                new Card(HEART, SEVEN),
                new Card(CLUB, SEVEN),
                new Card(SPADE, SEVEN),
                new Card(DIAMOND, SIX),
                new Card(SPADE, FIVE)
            )
        );

        assertThat(subject.isFullHouse()).isEqualTo(false);
    }

    @Test
    void isFlush() {
        PokerHand subject = new PokerHand(FLUSH);
        assertThat(subject.isFlush()).isEqualTo(true);
    }

    @Test
    void isFlush_returnsFalse() {
        PokerHand subject = new PokerHand(
            List.of(
                new Card(HEART, NINE),
                new Card(HEART, SIX),
                new Card(SPADE, FIVE),
                new Card(HEART, THREE),
                new Card(HEART, ACE)
            )
        );

        assertThat(subject.isFlush()).isEqualTo(false);
    }

    @Test
    void isFlush_royalFlush_returnFalse() {
        PokerHand subject = new PokerHand(ROYAL_FLUSH);
        assertThat(subject.isFlush()).isEqualTo(false);
    }

    @Test
    void isFlush_straightFlush_returnFalse() {
        PokerHand subject = new PokerHand(STRAIGHT_FLUSH);
        assertThat(subject.isFlush()).isEqualTo(false);
    }

    @Test
    void isStraight() {
        PokerHand subject = new PokerHand(STRAIGHT);
        assertThat(subject.isStraight()).isEqualTo(true);
    }

    @Test
    void isStraight_returnsFalse() {
        PokerHand subject = new PokerHand(
            List.of(
                new Card(CLUB, JACK),
                new Card(DIAMOND, QUEEN),
                new Card(HEART, KING),
                new Card(CLUB, ACE),
                new Card(CLUB, THREE)
            )
        );

        assertThat(subject.isStraight()).isEqualTo(false);
    }

    @Test
    void isStraight_straightFlush_returnsFalse() {
        PokerHand subject = new PokerHand(STRAIGHT_FLUSH);
        assertThat(subject.isStraight()).isEqualTo(false);
    }

    @Test
    void isStraight_containsPair_returnsFalse() {
        PokerHand subject = new PokerHand(
            List.of(
                new Card(SPADE, QUEEN),
                new Card(DIAMOND, QUEEN),
                new Card(HEART, JACK),
                new Card(SPADE, NINE),
                new Card(DIAMOND, EIGHT)
            )
        );

        assertThat(subject.isStraight()).isEqualTo(false);
    }

    @Test
    void isThreeOfAKind() {
        PokerHand subject = new PokerHand(THREE_OF_A_KIND);
        assertThat(subject.isThreeOfAKind()).isEqualTo(true);
    }

    @Test
    void isThreeOfAKind_returnsFalse() {
        PokerHand subject = new PokerHand(
            List.of(
                new Card(HEART, SEVEN),
                new Card(CLUB, SEVEN),
                new Card(SPADE, FIVE),
                new Card(DIAMOND, EIGHT),
                new Card(SPADE, SIX)
            )
        );

        assertThat(subject.isThreeOfAKind()).isEqualTo(false);
    }

    @Test
    void isThreeOfAKind_fourOfAKind_returnsFalse() {
        PokerHand subject = new PokerHand(FOUR_OF_A_KIND);
        assertThat(subject.isThreeOfAKind()).isEqualTo(false);
    }

    @Test
    void isThreeOfAKind_fullHouse_returnsFalse() {
        PokerHand subject = new PokerHand(FULL_HOUSE);
        assertThat(subject.isThreeOfAKind()).isEqualTo(false);
    }

    @Test
    void isTwoPair() {
        PokerHand subject = new PokerHand(TWO_PAIR);
        assertThat(subject.isTwoPair()).isEqualTo(true);
    }

    @Test
    void isTwoPair_returnsFalse() {
        PokerHand subject = new PokerHand(
            List.of(
                new Card(HEART, SEVEN),
                new Card(CLUB, SEVEN),
                new Card(SPADE, FOUR),
                new Card(DIAMOND, TWO),
                new Card(SPADE, SIX)
            )
        );

        assertThat(subject.isTwoPair()).isEqualTo(false);
    }

    @Test
    void isTwoPair_fourOfAKind_returnsFalse() {
        PokerHand subject = new PokerHand(FOUR_OF_A_KIND);
        assertThat(subject.isTwoPair()).isEqualTo(false);
    }

    @Test
    void isTwoPair_fullHouse_returnsFalse() {
        PokerHand subject = new PokerHand(FULL_HOUSE);
        assertThat(subject.isTwoPair()).isEqualTo(false);
    }

    @Test
    void isJacksOrBetter() {
        PokerHand subject = new PokerHand(JACKS_OR_BETTER);
        assertThat(subject.isJacksOrBetter()).isEqualTo(true);
    }

    @Test
    void isJacksOrBetter_returnsFalse() {
        PokerHand subject = new PokerHand(
            List.of(
                new Card(HEART, JACK),
                new Card(CLUB, SEVEN),
                new Card(SPADE, FOUR),
                new Card(DIAMOND, TWO),
                new Card(SPADE, SIX)
            )
        );

        assertThat(subject.isJacksOrBetter()).isEqualTo(false);
    }

    @Test
    void isJacksOrBetter_threeOfAKind_returnsFalse() {
        PokerHand subject = new PokerHand(THREE_OF_A_KIND);
        assertThat(subject.isJacksOrBetter()).isEqualTo(false);
    }

    @Test
    void isJacksOrBetter_fourOfAKind_returnsFalse() {
        PokerHand subject = new PokerHand(FOUR_OF_A_KIND);
        assertThat(subject.isJacksOrBetter()).isEqualTo(false);
    }

    @Test
    void isJacksOrBetter_fullHouse_returnsFalse() {
        PokerHand subject = new PokerHand(FULL_HOUSE);
        assertThat(subject.isJacksOrBetter()).isEqualTo(false);
    }

    @Test
    void isJacksOrBetter_twoPair_returnsFalse() {
        PokerHand subject = new PokerHand(TWO_PAIR);
        assertThat(subject.isJacksOrBetter()).isEqualTo(false);
    }

    @Test
    void calculateBestHand() {
        assertThat(new PokerHand(Collections.emptyList()).calculateBestHand()).isEqualTo(null);
        assertThat(new PokerHand(ROYAL_FLUSH).calculateBestHand()).isEqualTo(PokerHandEnum.ROYAL_FLUSH);
        assertThat(new PokerHand(STRAIGHT_FLUSH).calculateBestHand()).isEqualTo(PokerHandEnum.STRAIGHT_FLUSH);
        assertThat(new PokerHand(FOUR_OF_A_KIND).calculateBestHand()).isEqualTo(PokerHandEnum.FOUR_OF_A_KIND);
        assertThat(new PokerHand(FULL_HOUSE).calculateBestHand()).isEqualTo(PokerHandEnum.FULL_HOUSE);
        assertThat(new PokerHand(FLUSH).calculateBestHand()).isEqualTo(PokerHandEnum.FLUSH);
        assertThat(new PokerHand(STRAIGHT).calculateBestHand()).isEqualTo(PokerHandEnum.STRAIGHT);
        assertThat(new PokerHand(THREE_OF_A_KIND).calculateBestHand()).isEqualTo(PokerHandEnum.THREE_OF_A_KIND);
        assertThat(new PokerHand(TWO_PAIR).calculateBestHand()).isEqualTo(PokerHandEnum.TWO_PAIR);
        assertThat(new PokerHand(JACKS_OR_BETTER).calculateBestHand()).isEqualTo(PokerHandEnum.JACKS_OR_BETTER);
        assertThat(new PokerHand(List.of(
            new Card(HEART, TEN),
            new Card(CLUB, TEN),
            new Card(SPADE, SEVEN),
            new Card(DIAMOND, THREE),
            new Card(SPADE, TWO)
        )).calculateBestHand()).isEqualTo(PokerHandEnum.HIGH_CARD);

    }
}


