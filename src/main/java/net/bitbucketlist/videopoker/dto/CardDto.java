package net.bitbucketlist.videopoker.dto;

import lombok.Value;
import net.bitbucketlist.videopoker.deck.Rank;
import net.bitbucketlist.videopoker.deck.Suit;

@Value
public class CardDto {
    Suit suit;
    Rank rank;
}
