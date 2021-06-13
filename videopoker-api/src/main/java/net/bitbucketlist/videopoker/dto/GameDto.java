package net.bitbucketlist.videopoker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.bitbucketlist.videopoker.GameState;
import net.bitbucketlist.videopoker.scoring.PokerHandEnum;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameDto {
    UUID id;
    int deckSize;
    int bet;
    int credits;
    List<CardDto> hand;
    PokerHandEnum handRank;
    GameState gameState;
}
