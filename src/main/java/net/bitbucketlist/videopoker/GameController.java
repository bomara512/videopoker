package net.bitbucketlist.videopoker;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import net.bitbucketlist.videopoker.dto.GameDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@CrossOrigin
public class GameController {
    GameService gameService;

    @PostMapping(path = "/game")
    @ResponseStatus(value = HttpStatus.CREATED)
    public GameDto createGame() {
        return gameService.createGame();
    }

    @GetMapping(path = "/game")
    public List<GameDto> getAllGames() {
        return gameService.getAllGames();
    }

    @PutMapping(path = "/game/{gameId}/bet")
    public GameDto changeBet(@PathVariable UUID gameId, @RequestParam int amount) {
        return gameService.setBet(gameId, amount);
    }

    @PutMapping(path = "/game/{gameId}/deal")
    public GameDto deal(@PathVariable UUID gameId) {
        return gameService.deal(gameId);
    }

    @PutMapping(path = "/game/{gameId}/draw")
    public GameDto draw(@PathVariable UUID gameId, @RequestParam List<Integer> holds) {
        return gameService.draw(gameId, holds);
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
