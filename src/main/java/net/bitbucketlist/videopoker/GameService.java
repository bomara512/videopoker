package net.bitbucketlist.videopoker;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.bitbucketlist.videopoker.deck.Card;
import net.bitbucketlist.videopoker.deck.Deck;
import net.bitbucketlist.videopoker.dto.GameDto;
import net.bitbucketlist.videopoker.persistence.GameEntity;
import net.bitbucketlist.videopoker.persistence.GameRepository;
import net.bitbucketlist.videopoker.scoring.PayoutService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GameService {
    private static final int HAND_SIZE = 5;
    private static final int MINIMUM_BET = 1;
    private static final int MAXIMUM_BET = 5;

    GameRepository gameRepository;
    GameMapper gameMapper;
    PayoutService payoutService;

    public GameDto createGame() {
        Deck deck = new Deck();
        deck.shuffle();

        GameEntity game = gameRepository.save(
            new GameEntity(deck, 1, 50)
        );

        return gameMapper.mapToDto(game);
    }

    public List<GameDto> getAllGames() {
        return StreamSupport.stream(gameRepository.findAll().spliterator(), false)
            .filter(Objects::nonNull)
            .map(gameMapper::mapToDto)
            .collect(Collectors.toList());
    }

    public GameDto setCurrentBet(UUID gameId, int currentBet) {
        GameEntity gameEntity = getGameEntity(gameId);

        if (gameEntity.getGameState() != GameState.READY_TO_DEAL) {
            throw new InvalidGameStateException("Unable to change bet for gameId: " + gameId);
        }

        if (currentBet < MINIMUM_BET || currentBet > MAXIMUM_BET) {
            throw new InvalidGameStateException(
                String.format("Bet must be between %s and %s. gameId: %s", MINIMUM_BET, MAXIMUM_BET, gameId)
            );
        }

        if (gameEntity.getCurrentBalance() < currentBet) {
            throw new InvalidGameStateException(
                String.format("Not enough credits (%s requested, %s available). gameId: %s", currentBet, gameEntity.getCurrentBalance(), gameId)
            );
        }

        gameEntity.setCurrentBet(currentBet);

        gameRepository.save(gameEntity);

        return gameMapper.mapToDto(gameEntity);
    }

    public GameDto deal(UUID gameId) {
        GameEntity gameEntity = getGameEntity(gameId);

        if (gameEntity.getGameState() != GameState.READY_TO_DEAL) {
            throw new InvalidGameStateException("Unable to deal for gameId: " + gameId);
        }

        if (gameEntity.getDeck().size() <= HAND_SIZE * 2) {
            Deck newDeck = new Deck();
            newDeck.shuffle();
            gameEntity.setDeck(newDeck);
        }

        List<Card> cards = gameEntity.getDeck().deal(HAND_SIZE);

        gameEntity.setGameState(GameState.READY_TO_DRAW);
        gameEntity.setCurrentHand(cards);
        gameEntity.setCurrentBalance(gameEntity.getCurrentBalance() - gameEntity.getCurrentBet());

        gameRepository.save(gameEntity);

        return gameMapper.mapToDto(gameEntity);
    }

    public GameDto draw(UUID gameId, List<Integer> holds) {
        GameEntity gameEntity = getGameEntity(gameId);

        if (gameEntity.getGameState() != GameState.READY_TO_DRAW) {
            throw new InvalidGameStateException("Unable to draw for gameId: " + gameId);
        }

        if (gameEntity.getDeck().size() < HAND_SIZE) {
            throw new InvalidGameStateException("Unable to draw for gameId: " + gameId);
        }

        List<Card> currentHand = gameEntity.getCurrentHand();

        for (int i = 0; i < HAND_SIZE; i++) {
            if (!holds.contains(i)) {
                currentHand.set(i, gameEntity.getDeck().deal(1).get(0));
            }
        }

        int payout = payoutService.calculatePayout(gameEntity.getCurrentHand(), gameEntity.getCurrentBet());
        gameEntity.setCurrentBalance(gameEntity.getCurrentBalance() + payout);

        gameEntity.setGameState(GameState.READY_TO_DEAL);

        if (gameEntity.getDeck().size() <= HAND_SIZE) {
            Deck newDeck = new Deck();
            newDeck.shuffle();
            gameEntity.setDeck(newDeck);
        }

        gameRepository.save(gameEntity);

        return gameMapper.mapToDto(gameEntity);
    }

    private GameEntity getGameEntity(UUID gameId) {
        return gameRepository
            .findById(gameId)
            .orElseThrow(() -> new InvalidGameStateException(
                    String.format("Game %s does not exist", gameId)
                )
            );
    }
}
