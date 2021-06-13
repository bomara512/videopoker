package net.bitbucketlist.videopoker.deck;

import org.junit.jupiter.api.Test;

import static net.bitbucketlist.videopoker.deck.Rank.*;
import static net.bitbucketlist.videopoker.deck.Suit.*;
import static org.assertj.core.api.Assertions.assertThat;

class CardTest {

    @Test
    void getDisplay_Heart() {
        assertThat(new Card(HEART, ACE).getDisplay()).isEqualTo("AH");
        assertThat(new Card(HEART, TWO).getDisplay()).isEqualTo("2H");
        assertThat(new Card(HEART, THREE).getDisplay()).isEqualTo("3H");
        assertThat(new Card(HEART, FOUR).getDisplay()).isEqualTo("4H");
        assertThat(new Card(HEART, FIVE).getDisplay()).isEqualTo("5H");
        assertThat(new Card(HEART, SIX).getDisplay()).isEqualTo("6H");
        assertThat(new Card(HEART, SEVEN).getDisplay()).isEqualTo("7H");
        assertThat(new Card(HEART, EIGHT).getDisplay()).isEqualTo("8H");
        assertThat(new Card(HEART, NINE).getDisplay()).isEqualTo("9H");
        assertThat(new Card(HEART, TEN).getDisplay()).isEqualTo("10H");
        assertThat(new Card(HEART, JACK).getDisplay()).isEqualTo("JH");
        assertThat(new Card(HEART, QUEEN).getDisplay()).isEqualTo("QH");
        assertThat(new Card(HEART, KING).getDisplay()).isEqualTo("KH");
    }

    @Test
    void getDisplay_Diamond() {
        assertThat(new Card(DIAMOND, ACE).getDisplay()).isEqualTo("AD");
        assertThat(new Card(DIAMOND, TWO).getDisplay()).isEqualTo("2D");
        assertThat(new Card(DIAMOND, THREE).getDisplay()).isEqualTo("3D");
        assertThat(new Card(DIAMOND, FOUR).getDisplay()).isEqualTo("4D");
        assertThat(new Card(DIAMOND, FIVE).getDisplay()).isEqualTo("5D");
        assertThat(new Card(DIAMOND, SIX).getDisplay()).isEqualTo("6D");
        assertThat(new Card(DIAMOND, SEVEN).getDisplay()).isEqualTo("7D");
        assertThat(new Card(DIAMOND, EIGHT).getDisplay()).isEqualTo("8D");
        assertThat(new Card(DIAMOND, NINE).getDisplay()).isEqualTo("9D");
        assertThat(new Card(DIAMOND, TEN).getDisplay()).isEqualTo("10D");
        assertThat(new Card(DIAMOND, JACK).getDisplay()).isEqualTo("JD");
        assertThat(new Card(DIAMOND, QUEEN).getDisplay()).isEqualTo("QD");
        assertThat(new Card(DIAMOND, KING).getDisplay()).isEqualTo("KD");
    }

    @Test
    void getDisplay_Club() {
        assertThat(new Card(CLUB, ACE).getDisplay()).isEqualTo("AC");
        assertThat(new Card(CLUB, TWO).getDisplay()).isEqualTo("2C");
        assertThat(new Card(CLUB, THREE).getDisplay()).isEqualTo("3C");
        assertThat(new Card(CLUB, FOUR).getDisplay()).isEqualTo("4C");
        assertThat(new Card(CLUB, FIVE).getDisplay()).isEqualTo("5C");
        assertThat(new Card(CLUB, SIX).getDisplay()).isEqualTo("6C");
        assertThat(new Card(CLUB, SEVEN).getDisplay()).isEqualTo("7C");
        assertThat(new Card(CLUB, EIGHT).getDisplay()).isEqualTo("8C");
        assertThat(new Card(CLUB, NINE).getDisplay()).isEqualTo("9C");
        assertThat(new Card(CLUB, TEN).getDisplay()).isEqualTo("10C");
        assertThat(new Card(CLUB, JACK).getDisplay()).isEqualTo("JC");
        assertThat(new Card(CLUB, QUEEN).getDisplay()).isEqualTo("QC");
        assertThat(new Card(CLUB, KING).getDisplay()).isEqualTo("KC");
    }

    @Test
    void getDisplay_Spade() {
        assertThat(new Card(SPADE, ACE).getDisplay()).isEqualTo("AS");
        assertThat(new Card(SPADE, TWO).getDisplay()).isEqualTo("2S");
        assertThat(new Card(SPADE, THREE).getDisplay()).isEqualTo("3S");
        assertThat(new Card(SPADE, FOUR).getDisplay()).isEqualTo("4S");
        assertThat(new Card(SPADE, FIVE).getDisplay()).isEqualTo("5S");
        assertThat(new Card(SPADE, SIX).getDisplay()).isEqualTo("6S");
        assertThat(new Card(SPADE, SEVEN).getDisplay()).isEqualTo("7S");
        assertThat(new Card(SPADE, EIGHT).getDisplay()).isEqualTo("8S");
        assertThat(new Card(SPADE, NINE).getDisplay()).isEqualTo("9S");
        assertThat(new Card(SPADE, TEN).getDisplay()).isEqualTo("10S");
        assertThat(new Card(SPADE, JACK).getDisplay()).isEqualTo("JS");
        assertThat(new Card(SPADE, QUEEN).getDisplay()).isEqualTo("QS");
        assertThat(new Card(SPADE, KING).getDisplay()).isEqualTo("KS");
    }
}
