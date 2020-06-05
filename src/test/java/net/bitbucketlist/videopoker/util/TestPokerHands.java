package net.bitbucketlist.videopoker.util;

import net.bitbucketlist.videopoker.deck.Card;
import net.bitbucketlist.videopoker.deck.Rank;
import net.bitbucketlist.videopoker.deck.Suit;

import java.util.List;

public class TestPokerHands {
    public static final List<Card> ROYAL_FLUSH = List.of(
        new Card(Suit.HEART, Rank.ACE),
        new Card(Suit.HEART, Rank.KING),
        new Card(Suit.HEART, Rank.QUEEN),
        new Card(Suit.HEART, Rank.JACK),
        new Card(Suit.HEART, Rank.TEN)
    );

    public static final List<Card> STRAIGHT_FLUSH = List.of(
        new Card(Suit.HEART, Rank.SIX),
        new Card(Suit.HEART, Rank.FIVE),
        new Card(Suit.HEART, Rank.FOUR),
        new Card(Suit.HEART, Rank.THREE),
        new Card(Suit.HEART, Rank.TWO)
    );

    public static final List<Card> FOUR_OF_A_KIND = List.of(
        new Card(Suit.HEART, Rank.SEVEN),
        new Card(Suit.CLUB, Rank.SEVEN),
        new Card(Suit.SPADE, Rank.SEVEN),
        new Card(Suit.DIAMOND, Rank.SEVEN),
        new Card(Suit.SPADE, Rank.SIX)
    );

    public static final List<Card> FULL_HOUSE = List.of(
        new Card(Suit.HEART, Rank.SEVEN),
        new Card(Suit.CLUB, Rank.SEVEN),
        new Card(Suit.SPADE, Rank.SEVEN),
        new Card(Suit.DIAMOND, Rank.SIX),
        new Card(Suit.SPADE, Rank.SIX)
    );

    public static final List<Card> FLUSH = List.of(
        new Card(Suit.HEART, Rank.NINE),
        new Card(Suit.HEART, Rank.SIX),
        new Card(Suit.HEART, Rank.FIVE),
        new Card(Suit.HEART, Rank.THREE),
        new Card(Suit.HEART, Rank.ACE)
    );

    public static final List<Card> STRAIGHT = List.of(
        new Card(Suit.CLUB, Rank.EIGHT),
        new Card(Suit.HEART, Rank.SEVEN),
        new Card(Suit.SPADE, Rank.SIX),
        new Card(Suit.HEART, Rank.FIVE),
        new Card(Suit.DIAMOND, Rank.FOUR)
    );

    public static final List<Card> THREE_OF_A_KIND = List.of(
        new Card(Suit.HEART, Rank.SEVEN),
        new Card(Suit.CLUB, Rank.SEVEN),
        new Card(Suit.SPADE, Rank.SEVEN),
        new Card(Suit.DIAMOND, Rank.FIVE),
        new Card(Suit.SPADE, Rank.SIX)
    );

    public static final List<Card> TWO_PAIR = List.of(
        new Card(Suit.HEART, Rank.SEVEN),
        new Card(Suit.CLUB, Rank.SEVEN),
        new Card(Suit.SPADE, Rank.FOUR),
        new Card(Suit.DIAMOND, Rank.FOUR),
        new Card(Suit.SPADE, Rank.SIX)
    );

    public static final List<Card> JACKS_OR_BETTER = List.of(
        new Card(Suit.HEART, Rank.JACK),
        new Card(Suit.CLUB, Rank.JACK),
        new Card(Suit.SPADE, Rank.FOUR),
        new Card(Suit.DIAMOND, Rank.FIVE),
        new Card(Suit.SPADE, Rank.SIX)
    );

    public static final List<Card> HIGH_CARD = List.of(
        new Card(Suit.HEART, Rank.JACK),
        new Card(Suit.CLUB, Rank.THREE),
        new Card(Suit.SPADE, Rank.FOUR),
        new Card(Suit.DIAMOND, Rank.FIVE),
        new Card(Suit.SPADE, Rank.SIX)
    );
}
