package net.bitbucketlist.videopoker.scoring;


import net.bitbucketlist.videopoker.deck.Card;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class PayoutService {
    Map<PokerHandEnum, List<Integer>> payouts = Map.of(
        PokerHandEnum.ROYAL_FLUSH, List.of(250, 500, 750, 1000, 4000),
        PokerHandEnum.STRAIGHT_FLUSH, List.of(50, 100, 150, 200, 250),
        PokerHandEnum.FOUR_OF_A_KIND, List.of(25, 50, 75, 100, 125),
        PokerHandEnum.FULL_HOUSE, List.of(9, 18, 27, 36, 45),
        PokerHandEnum.FLUSH, List.of(6, 12, 18, 24, 30),
        PokerHandEnum.STRAIGHT, List.of(4, 8, 12, 16, 20),
        PokerHandEnum.THREE_OF_A_KIND, List.of(3, 6, 9, 12, 15),
        PokerHandEnum.TWO_PAIR, List.of(2, 4, 6, 8, 10),
        PokerHandEnum.JACKS_OR_BETTER, List.of(1, 2, 3, 4, 5),
        PokerHandEnum.HIGH_CARD, List.of(0, 0, 0, 0, 0)
    );

    public int calculatePayout(List<Card> currentHand, Integer currentBet) {
        PokerHandEnum bestHand = new PokerHand(currentHand).calculateBestHand();
        return payouts.get(bestHand).get(currentBet - 1);
    }
}
