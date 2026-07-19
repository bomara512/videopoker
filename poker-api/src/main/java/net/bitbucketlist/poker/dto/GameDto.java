package net.bitbucketlist.poker.dto;

import net.bitbucketlist.poker.GameState;
import net.bitbucketlist.poker.scoring.PokerHandEnum;

import java.util.List;
import java.util.UUID;

public record GameDto(
    UUID id,
    int deckSize,
    int bet,
    int credits,
    List<CardDto> hand,
    PokerHandEnum handRank,
    GameState gameState
) {
}
