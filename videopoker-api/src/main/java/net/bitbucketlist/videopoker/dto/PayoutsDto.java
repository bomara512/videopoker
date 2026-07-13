package net.bitbucketlist.videopoker.dto;

import net.bitbucketlist.videopoker.scoring.PokerHandEnum;

import java.util.List;

public record PayoutsDto(PokerHandEnum hand, List<Integer> payouts) {
}
