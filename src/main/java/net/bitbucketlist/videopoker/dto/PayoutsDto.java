package net.bitbucketlist.videopoker.dto;

import lombok.Value;
import net.bitbucketlist.videopoker.scoring.PokerHandEnum;

import java.util.List;

@Value
public class PayoutsDto {
    PokerHandEnum hand;
    List<Integer> payouts;
}
