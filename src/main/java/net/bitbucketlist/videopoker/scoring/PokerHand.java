package net.bitbucketlist.videopoker.scoring;

import lombok.Value;
import net.bitbucketlist.videopoker.deck.Card;
import net.bitbucketlist.videopoker.deck.Rank;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Value
public class PokerHand {
    static List<Rank> ROYAL_RANKS = List.of(Rank.ACE, Rank.KING, Rank.QUEEN, Rank.JACK);

    List<Card> hand;

    public boolean isRoyalFlush() {
        return containsRank(Rank.TEN) && isRoyal() && isSameSuit();
    }

    public boolean isStraightFlush() {
        if (isRoyalFlush()) {
            return false;
        }

        return isRun() && isSameSuit();
    }

    public boolean isFourOfAKind() {
        return containsSet(4);
    }

    public boolean isFullHouse() {
        return containsSet(2) && containsSet(3);
    }

    public boolean isFlush() {
        if (isRoyalFlush()) {
            return false;
        }

        if (isRun()) {
            return false;
        }

        return isSameSuit();
    }

    public boolean isStraight() {
        if (isStraightFlush()) {
            return false;
        }

        return isRun();
    }

    public boolean isThreeOfAKind() {
        if (isFullHouse()) {
            return false;
        }

        return containsSet(3);
    }

    public boolean isTwoPair() {
        return getPairsCount(hand) == 2;
    }

    public boolean isJacksOrBetter() {
        if (isFullHouse()) {
            return false;
        }

        if (isTwoPair()) {
            return false;
        }

        List<Card> jacksOrBetter = hand
            .stream()
            .filter(card -> ROYAL_RANKS.contains(card.getRank()))
            .collect(Collectors.toList());

        return getPairsCount(jacksOrBetter) == 1;
    }

    public PokerHandEnum calculateBestHand() {
        if (this.hand.isEmpty()) {
            return null;
        }

        if (this.isRoyalFlush()) {
            return PokerHandEnum.ROYAL_FLUSH;
        } else if (this.isStraightFlush()) {
            return PokerHandEnum.STRAIGHT_FLUSH;
        } else if (this.isFourOfAKind()) {
            return PokerHandEnum.FOUR_OF_A_KIND;
        } else if (this.isFullHouse()) {
            return PokerHandEnum.FULL_HOUSE;
        } else if (this.isFlush()) {
            return PokerHandEnum.FLUSH;
        } else if (this.isStraight()) {
            return PokerHandEnum.STRAIGHT;
        } else if (this.isThreeOfAKind()) {
            return PokerHandEnum.THREE_OF_A_KIND;
        } else if (this.isTwoPair()) {
            return PokerHandEnum.TWO_PAIR;
        } else if (this.isJacksOrBetter()) {
            return PokerHandEnum.JACKS_OR_BETTER;
        } else {
            return PokerHandEnum.HIGH_CARD;
        }
    }

    private boolean isSameSuit() {
        return hand
            .stream()
            .map(Card::getSuit)
            .distinct()
            .count() == 1;
    }

    private boolean isRoyal() {
        return getRanks().containsAll(ROYAL_RANKS);
    }

    private boolean isRun() {
        List<Integer> ordinals = getRanks()
            .stream()
            .map(Rank::ordinal)
            .distinct()
            .sorted()
            .collect(Collectors.toList());

        boolean isNoDuplicateRanks = ordinals.size() == hand.size();
        boolean isFiveRanksApart = ordinals.get(0) + 4 == ordinals.get(ordinals.size() - 1);

        return isNoDuplicateRanks && isFiveRanksApart;
    }

    private boolean containsSet(int count) {
        return getCountByRank(hand).values()
            .stream()
            .anyMatch(countByRank -> countByRank == count);
    }

    private boolean containsRank(Rank rank) {
        return hand.stream().anyMatch(card -> card.getRank() == rank);
    }

    private long getPairsCount(List<Card> hand) {
        return getCountByRank(hand).values()
            .stream()
            .filter(count -> count == 2)
            .count();
    }

    private Map<Rank, Long> getCountByRank(List<Card> hand) {
        return hand.stream().map(Card::getRank)
            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }

    private List<Rank> getRanks() {
        return hand
            .stream()
            .map(Card::getRank)
            .collect(Collectors.toList());
    }
}
