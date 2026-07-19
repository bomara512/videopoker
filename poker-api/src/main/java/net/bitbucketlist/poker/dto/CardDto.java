package net.bitbucketlist.poker.dto;

import net.bitbucketlist.poker.deck.Rank;
import net.bitbucketlist.poker.deck.Suit;

public record CardDto(Suit suit, Rank rank) {
}
