package net.bitbucketlist.videopoker.scoring;

import net.bitbucketlist.videopoker.deck.Card;
import net.bitbucketlist.videopoker.deck.Rank;
import net.bitbucketlist.videopoker.deck.Suit;
import net.bitbucketlist.videopoker.util.TestPokerHands;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PokerHandTest {

    @Test
    void isRoyalFlush() {
        PokerHand subject = new PokerHand(TestPokerHands.ROYAL_FLUSH);
        assertThat(subject.isRoyalFlush()).isEqualTo(true);
    }

    @Test
    void isRoyalFlush_notFlush_returnsFalse() {
        PokerHand subject = new PokerHand(
            List.of(
                new Card(Suit.HEART, Rank.ACE),
                new Card(Suit.HEART, Rank.KING),
                new Card(Suit.HEART, Rank.QUEEN),
                new Card(Suit.HEART, Rank.JACK),
                new Card(Suit.SPADE, Rank.TEN)
            ));

        assertThat(subject.isRoyalFlush()).isEqualTo(false);
    }

    @Test
    void isRoyalFlush_notStraight_returnsFalse() {
        PokerHand subject = new PokerHand(
            List.of(
                new Card(Suit.HEART, Rank.ACE),
                new Card(Suit.HEART, Rank.KING),
                new Card(Suit.HEART, Rank.QUEEN),
                new Card(Suit.HEART, Rank.JACK),
                new Card(Suit.HEART, Rank.FOUR)
            ));

        assertThat(subject.isRoyalFlush()).isEqualTo(false);
    }

    @Test
    void isRoyalFlush_notRoyal_returnsFalse() {
        PokerHand subject = new PokerHand(
            List.of(
                new Card(Suit.HEART, Rank.KING),
                new Card(Suit.HEART, Rank.QUEEN),
                new Card(Suit.HEART, Rank.JACK),
                new Card(Suit.HEART, Rank.TEN),
                new Card(Suit.HEART, Rank.NINE)
            ));

        assertThat(subject.isRoyalFlush()).isEqualTo(false);
    }

    @Test
    void isStraightFlush() {
        PokerHand subject = new PokerHand(TestPokerHands.STRAIGHT_FLUSH);
        assertThat(subject.isStraightFlush()).isEqualTo(true);
    }

    @Test
    void isStraightFlush_notStraight_returnsFalse() {
        PokerHand subject = new PokerHand(
            List.of(
                new Card(Suit.HEART, Rank.EIGHT),
                new Card(Suit.HEART, Rank.FIVE),
                new Card(Suit.HEART, Rank.FOUR),
                new Card(Suit.HEART, Rank.THREE),
                new Card(Suit.HEART, Rank.TWO)
            ));

        assertThat(subject.isStraightFlush()).isEqualTo(false);
    }

    @Test
    void isStraightFlush_notFlush_returnsFalse() {
        PokerHand subject = new PokerHand(
            List.of(
                new Card(Suit.HEART, Rank.SIX),
                new Card(Suit.HEART, Rank.FIVE),
                new Card(Suit.HEART, Rank.FOUR),
                new Card(Suit.HEART, Rank.THREE),
                new Card(Suit.SPADE, Rank.TWO)
            ));

        assertThat(subject.isStraightFlush()).isEqualTo(false);
    }

    @Test
    void isStraightFlush_royalFlush_returnsFalse() {
        PokerHand subject = new PokerHand(
            List.of(
                new Card(Suit.HEART, Rank.ACE),
                new Card(Suit.HEART, Rank.KING),
                new Card(Suit.HEART, Rank.QUEEN),
                new Card(Suit.HEART, Rank.JACK),
                new Card(Suit.SPADE, Rank.TEN)
            )
        );

        assertThat(subject.isStraightFlush()).isEqualTo(false);
    }

    @Test
    void isFourOfAKind() {
        PokerHand subject = new PokerHand(TestPokerHands.FOUR_OF_A_KIND);
        assertThat(subject.isFourOfAKind()).isEqualTo(true);
    }

    @Test
    void isFourOfAKind_returnsFalse() {
        PokerHand subject = new PokerHand(
            List.of(
                new Card(Suit.HEART, Rank.SEVEN),
                new Card(Suit.CLUB, Rank.SEVEN),
                new Card(Suit.SPADE, Rank.SEVEN),
                new Card(Suit.DIAMOND, Rank.SIX),
                new Card(Suit.SPADE, Rank.SIX)
            )
        );

        assertThat(subject.isFourOfAKind()).isEqualTo(false);
    }

    @Test
    void isFullHouse() {
        PokerHand subject = new PokerHand(TestPokerHands.FULL_HOUSE);
        assertThat(subject.isFullHouse()).isEqualTo(true);
    }

    @Test
    void isFullHouse_returnsFalse() {
        PokerHand subject = new PokerHand(
            List.of(
                new Card(Suit.HEART, Rank.SEVEN),
                new Card(Suit.CLUB, Rank.SEVEN),
                new Card(Suit.SPADE, Rank.SEVEN),
                new Card(Suit.DIAMOND, Rank.SIX),
                new Card(Suit.SPADE, Rank.FIVE)
            )
        );

        assertThat(subject.isFullHouse()).isEqualTo(false);
    }

    @Test
    void isFlush() {
        PokerHand subject = new PokerHand(TestPokerHands.FLUSH);
        assertThat(subject.isFlush()).isEqualTo(true);
    }

    @Test
    void isFlush_returnsFalse() {
        PokerHand subject = new PokerHand(
            List.of(
                new Card(Suit.HEART, Rank.NINE),
                new Card(Suit.HEART, Rank.SIX),
                new Card(Suit.SPADE, Rank.FIVE),
                new Card(Suit.HEART, Rank.THREE),
                new Card(Suit.HEART, Rank.ACE)
            )
        );

        assertThat(subject.isFlush()).isEqualTo(false);
    }

    @Test
    void isFlush_royalFlush_returnFalse() {
        PokerHand subject = new PokerHand(
            List.of(
                new Card(Suit.HEART, Rank.ACE),
                new Card(Suit.HEART, Rank.KING),
                new Card(Suit.HEART, Rank.QUEEN),
                new Card(Suit.HEART, Rank.JACK),
                new Card(Suit.HEART, Rank.TEN)
            )
        );

        assertThat(subject.isFlush()).isEqualTo(false);
    }

    @Test
    void isFlush_straightFlush_returnFalse() {
        PokerHand subject = new PokerHand(
            List.of(
                new Card(Suit.HEART, Rank.SIX),
                new Card(Suit.HEART, Rank.FIVE),
                new Card(Suit.HEART, Rank.FOUR),
                new Card(Suit.HEART, Rank.THREE),
                new Card(Suit.HEART, Rank.TWO)
            )
        );

        assertThat(subject.isFlush()).isEqualTo(false);
    }

    @Test
    void isStraight() {
        PokerHand subject = new PokerHand(TestPokerHands.STRAIGHT);
        assertThat(subject.isStraight()).isEqualTo(true);
    }

    @Test
    void isStraight_returnsFalse() {
        PokerHand subject = new PokerHand(
            List.of(
                new Card(Suit.CLUB, Rank.EIGHT),
                new Card(Suit.HEART, Rank.SEVEN),
                new Card(Suit.SPADE, Rank.SIX),
                new Card(Suit.HEART, Rank.FIVE),
                new Card(Suit.DIAMOND, Rank.THREE)
            )
        );

        assertThat(subject.isStraight()).isEqualTo(false);
    }

    @Test
    void isStraight_straightFlush_returnsFalse() {
        PokerHand subject = new PokerHand(
            List.of(
                new Card(Suit.HEART, Rank.EIGHT),
                new Card(Suit.HEART, Rank.SEVEN),
                new Card(Suit.HEART, Rank.SIX),
                new Card(Suit.HEART, Rank.FIVE),
                new Card(Suit.HEART, Rank.FOUR)
            )
        );

        assertThat(subject.isStraight()).isEqualTo(false);
    }

    @Test
    void isStraight_containsPair_returnsFalse() {
        PokerHand subject = new PokerHand(
            List.of(
                new Card(Suit.SPADE, Rank.QUEEN),
                new Card(Suit.DIAMOND, Rank.QUEEN),
                new Card(Suit.HEART, Rank.JACK),
                new Card(Suit.SPADE, Rank.NINE),
                new Card(Suit.DIAMOND, Rank.EIGHT)
            )
        );

        assertThat(subject.isStraight()).isEqualTo(false);
    }

    @Test
    void isThreeOfAKind() {
        PokerHand subject = new PokerHand(TestPokerHands.THREE_OF_A_KIND);
        assertThat(subject.isThreeOfAKind()).isEqualTo(true);
    }

    @Test
    void isThreeOfAKind_returnsFalse() {
        PokerHand subject = new PokerHand(
            List.of(
                new Card(Suit.HEART, Rank.SEVEN),
                new Card(Suit.CLUB, Rank.SEVEN),
                new Card(Suit.SPADE, Rank.FIVE),
                new Card(Suit.DIAMOND, Rank.EIGHT),
                new Card(Suit.SPADE, Rank.SIX)
            )
        );

        assertThat(subject.isThreeOfAKind()).isEqualTo(false);
    }

    @Test
    void isThreeOfAKind_fourOfAKind_returnsFalse() {
        PokerHand subject = new PokerHand(
            List.of(
                new Card(Suit.HEART, Rank.SEVEN),
                new Card(Suit.CLUB, Rank.SEVEN),
                new Card(Suit.SPADE, Rank.SEVEN),
                new Card(Suit.DIAMOND, Rank.SEVEN),
                new Card(Suit.SPADE, Rank.SIX)
            )
        );

        assertThat(subject.isThreeOfAKind()).isEqualTo(false);
    }

    @Test
    void isThreeOfAKind_fullHouse_returnsFalse() {
        PokerHand subject = new PokerHand(
            List.of(
                new Card(Suit.HEART, Rank.SEVEN),
                new Card(Suit.CLUB, Rank.SEVEN),
                new Card(Suit.SPADE, Rank.SEVEN),
                new Card(Suit.DIAMOND, Rank.FIVE),
                new Card(Suit.SPADE, Rank.FIVE)
            )
        );

        assertThat(subject.isThreeOfAKind()).isEqualTo(false);
    }

    @Test
    void isTwoPair() {
        PokerHand subject = new PokerHand(TestPokerHands.TWO_PAIR);
        assertThat(subject.isTwoPair()).isEqualTo(true);
    }

    @Test
    void isTwoPair_returnsFalse() {
        PokerHand subject = new PokerHand(
            List.of(
                new Card(Suit.HEART, Rank.SEVEN),
                new Card(Suit.CLUB, Rank.SEVEN),
                new Card(Suit.SPADE, Rank.FOUR),
                new Card(Suit.DIAMOND, Rank.TWO),
                new Card(Suit.SPADE, Rank.SIX)
            )
        );

        assertThat(subject.isTwoPair()).isEqualTo(false);
    }

    @Test
    void isTwoPair_fourOfAKind_returnsFalse() {
        PokerHand subject = new PokerHand(
            List.of(
                new Card(Suit.HEART, Rank.SEVEN),
                new Card(Suit.CLUB, Rank.SEVEN),
                new Card(Suit.SPADE, Rank.SEVEN),
                new Card(Suit.DIAMOND, Rank.SEVEN),
                new Card(Suit.SPADE, Rank.SIX)
            )
        );

        assertThat(subject.isTwoPair()).isEqualTo(false);
    }

    @Test
    void isTwoPair_fullHouse_returnsFalse() {
        PokerHand subject = new PokerHand(
            List.of(
                new Card(Suit.HEART, Rank.SEVEN),
                new Card(Suit.CLUB, Rank.SEVEN),
                new Card(Suit.SPADE, Rank.SEVEN),
                new Card(Suit.DIAMOND, Rank.SIX),
                new Card(Suit.SPADE, Rank.SIX)
            )
        );

        assertThat(subject.isTwoPair()).isEqualTo(false);
    }

    @Test
    void isJacksOrBetter() {
        PokerHand subject = new PokerHand(TestPokerHands.JACKS_OR_BETTER);
        assertThat(subject.isJacksOrBetter()).isEqualTo(true);
    }

    @Test
    void isJacksOrBetter_returnsFalse() {
        PokerHand subject = new PokerHand(
            List.of(
                new Card(Suit.HEART, Rank.JACK),
                new Card(Suit.CLUB, Rank.SEVEN),
                new Card(Suit.SPADE, Rank.FOUR),
                new Card(Suit.DIAMOND, Rank.TWO),
                new Card(Suit.SPADE, Rank.SIX)
            )
        );

        assertThat(subject.isJacksOrBetter()).isEqualTo(false);
    }

    @Test
    void isJacksOrBetter_threeOfAKind_returnsFalse() {
        PokerHand subject = new PokerHand(
            List.of(
                new Card(Suit.HEART, Rank.JACK),
                new Card(Suit.CLUB, Rank.JACK),
                new Card(Suit.SPADE, Rank.JACK),
                new Card(Suit.DIAMOND, Rank.FOUR),
                new Card(Suit.SPADE, Rank.SIX)
            )
        );

        assertThat(subject.isJacksOrBetter()).isEqualTo(false);
    }

    @Test
    void isJacksOrBetter_fourOfAKind_returnsFalse() {
        PokerHand subject = new PokerHand(
            List.of(
                new Card(Suit.HEART, Rank.JACK),
                new Card(Suit.CLUB, Rank.JACK),
                new Card(Suit.SPADE, Rank.JACK),
                new Card(Suit.DIAMOND, Rank.JACK),
                new Card(Suit.SPADE, Rank.SIX)
            )
        );

        assertThat(subject.isJacksOrBetter()).isEqualTo(false);
    }

    @Test
    void isJacksOrBetter_fullHouse_returnsFalse() {
        PokerHand subject = new PokerHand(
            List.of(
                new Card(Suit.HEART, Rank.JACK),
                new Card(Suit.CLUB, Rank.JACK),
                new Card(Suit.SPADE, Rank.JACK),
                new Card(Suit.DIAMOND, Rank.QUEEN),
                new Card(Suit.SPADE, Rank.QUEEN)
            )
        );

        assertThat(subject.isJacksOrBetter()).isEqualTo(false);
    }

    @Test
    void isJacksOrBetter_twoPair_returnsFalse() {
        PokerHand subject = new PokerHand(
            List.of(
                new Card(Suit.HEART, Rank.JACK),
                new Card(Suit.CLUB, Rank.JACK),
                new Card(Suit.SPADE, Rank.QUEEN),
                new Card(Suit.DIAMOND, Rank.QUEEN),
                new Card(Suit.SPADE, Rank.SIX)
            )
        );

        assertThat(subject.isJacksOrBetter()).isEqualTo(false);
    }

    @Test
    void calculateBestHand() {
        assertThat(new PokerHand(Collections.emptyList()).calculateBestHand()).isEqualTo(null);
        assertThat(new PokerHand(TestPokerHands.ROYAL_FLUSH).calculateBestHand()).isEqualTo(PokerHandEnum.ROYAL_FLUSH);
        assertThat(new PokerHand(TestPokerHands.STRAIGHT_FLUSH).calculateBestHand()).isEqualTo(PokerHandEnum.STRAIGHT_FLUSH);
        assertThat(new PokerHand(TestPokerHands.FOUR_OF_A_KIND).calculateBestHand()).isEqualTo(PokerHandEnum.FOUR_OF_A_KIND);
        assertThat(new PokerHand(TestPokerHands.FULL_HOUSE).calculateBestHand()).isEqualTo(PokerHandEnum.FULL_HOUSE);
        assertThat(new PokerHand(TestPokerHands.FLUSH).calculateBestHand()).isEqualTo(PokerHandEnum.FLUSH);
        assertThat(new PokerHand(TestPokerHands.STRAIGHT).calculateBestHand()).isEqualTo(PokerHandEnum.STRAIGHT);
        assertThat(new PokerHand(TestPokerHands.THREE_OF_A_KIND).calculateBestHand()).isEqualTo(PokerHandEnum.THREE_OF_A_KIND);
        assertThat(new PokerHand(TestPokerHands.TWO_PAIR).calculateBestHand()).isEqualTo(PokerHandEnum.TWO_PAIR);
        assertThat(new PokerHand(TestPokerHands.JACKS_OR_BETTER).calculateBestHand()).isEqualTo(PokerHandEnum.JACKS_OR_BETTER);
        assertThat(new PokerHand(List.of(
            new Card(Suit.HEART, Rank.TEN),
            new Card(Suit.CLUB, Rank.TEN),
            new Card(Suit.SPADE, Rank.SEVEN),
            new Card(Suit.DIAMOND, Rank.THREE),
            new Card(Suit.SPADE, Rank.TWO)
        )).calculateBestHand()).isEqualTo(PokerHandEnum.HIGH_CARD);

    }
}


