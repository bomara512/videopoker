package net.bitbucketlist.videopoker;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import net.bitbucketlist.videopoker.dto.GameDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @ExceptionHandler({InvalidGameStateException.class})
    public ResponseEntity<GameErrorResponse> handleException(InvalidGameStateException e) {
        return new ResponseEntity<>(new GameErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @Value
    private class GameErrorResponse {
        String message;
    }
}
