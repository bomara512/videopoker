package net.bitbucketlist.videopoker.scoring;


import net.bitbucketlist.videopoker.deck.Card;
import net.bitbucketlist.videopoker.dto.PayoutsDto;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static net.bitbucketlist.videopoker.scoring.PokerHandEnum.*;

@Service
public class PayoutService {
    private static final Map<PokerHandEnum, List<Integer>> payouts;
    private static final List<PayoutsDto> payoutSchedule;

    static {
        payouts = new LinkedHashMap<>();
        payouts.put(ROYAL_FLUSH, List.of(250, 500, 750, 1000, 4000));
        payouts.put(STRAIGHT_FLUSH, List.of(50, 100, 150, 200, 250));
        payouts.put(FOUR_OF_A_KIND, List.of(25, 50, 75, 100, 125));
        payouts.put(FULL_HOUSE, List.of(9, 18, 27, 36, 45));
        payouts.put(FLUSH, List.of(6, 12, 18, 24, 30));
        payouts.put(STRAIGHT, List.of(4, 8, 12, 16, 20));
        payouts.put(THREE_OF_A_KIND, List.of(3, 6, 9, 12, 15));
        payouts.put(TWO_PAIR, List.of(2, 4, 6, 8, 10));
        payouts.put(JACKS_OR_BETTER, List.of(1, 2, 3, 4, 5));
        payouts.put(HIGH_CARD, List.of(0, 0, 0, 0, 0));

        payoutSchedule = payouts.entrySet().stream()
            .filter(e -> e.getKey() != HIGH_CARD)
            .map(e -> new PayoutsDto(e.getKey(), e.getValue()))
            .collect(Collectors.toList());
    }

    public int calculatePayout(List<Card> hand, Integer bet) {
        PokerHandEnum handRank = new PokerHand(hand).calculateBestHand();
        return payouts.get(handRank).get(bet - 1);
    }

    public List<PayoutsDto> getPayoutSchedule() {
        return payoutSchedule;
    }
}
