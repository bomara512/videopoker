package net.bitbucketlist.videopoker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.bitbucketlist.videopoker.scoring.PokerHandEnum;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayoutsDto {
    PokerHandEnum hand;
    List<Integer> payouts;
}
