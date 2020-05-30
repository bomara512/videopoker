package net.bitbucketlist.videopoker;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import net.bitbucketlist.videopoker.dto.GameDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GameController {
    private final GameService gameService;

    @PostMapping(path = "/game")
    @ResponseStatus(value = HttpStatus.CREATED)
    public GameDto createGame() {
        return gameService.createGame();
    }

    @PutMapping(path = "/game/{gameId}/bet")
    public GameDto changeBet(@PathVariable UUID gameId, @RequestParam int currentBet) {
        return gameService.setCurrentBet(gameId, currentBet);
    }

    @PutMapping(path = "/game/{gameId}/deal")
    public GameDto deal(@PathVariable UUID gameId) {
        return gameService.deal(gameId);
    }

    @ExceptionHandler({InvalidGameStateException.class})
    public ResponseEntity<GameErrorResponse> handleException(InvalidGameStateException e) {
        return new ResponseEntity<>(new GameErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @PutMapping(path = "/game/{gameId}/draw")
    public GameDto draw(@PathVariable UUID gameId, @RequestParam List<Integer> holds) {
        return gameService.draw(gameId, holds);
    }

    @Value
    private class GameErrorResponse {
        String message;
    }
}