package net.bitbucketlist.videopoker.dto;

import net.bitbucketlist.videopoker.deck.Rank;
import net.bitbucketlist.videopoker.deck.Suit;

public record CardDto(Suit suit, Rank rank) {
}
