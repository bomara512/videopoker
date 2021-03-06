package net.bitbucketlist.videopoker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.bitbucketlist.videopoker.deck.Rank;
import net.bitbucketlist.videopoker.deck.Suit;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardDto {
    Suit suit;
    Rank rank;
}
