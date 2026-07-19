package net.bitbucketlist.poker.dto;

import net.bitbucketlist.poker.scoring.PokerHandEnum;

import java.util.List;

public record PayoutsDto(PokerHandEnum hand, List<Integer> payouts) {
}
