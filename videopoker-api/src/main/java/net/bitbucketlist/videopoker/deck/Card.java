package net.bitbucketlist.videopoker.deck;

import lombok.NonNull;
import lombok.Value;

@Value
public class Card {

    @NonNull
    Suit suit;

    @NonNull
    Rank rank;

    public String getDisplay() {
        return getRankDisplay() + getSuitDisplay();
    }

    private String getRankDisplay() {
        switch (rank) {
            case ACE:
                return "A";
            case TWO:
                return "2";
            case THREE:
                return "3";
            case FOUR:
                return "4";
            case FIVE:
                return "5";
            case SIX:
                return "6";
            case SEVEN:
                return "7";
            case EIGHT:
                return "8";
            case NINE:
                return "9";
            case TEN:
                return "10";
            case JACK:
                return "J";
            case QUEEN:
                return "Q";
            case KING:
                return "K";
            default:
                throw new IllegalArgumentException("Unexpected value: " + getRank());
        }
    }

    private String getSuitDisplay() {
        switch (suit) {
            case HEART:
                return "H";
            case CLUB:
                return "C";
            case DIAMOND:
                return "D";
            case SPADE:
                return "S";
            default:
                throw new IllegalArgumentException("Unexpected value: " + getSuit());
        }
    }
}
