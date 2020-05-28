package net.bitbucketlist.videopoker;

import lombok.RequiredArgsConstructor;
import net.bitbucketlist.videopoker.dto.GameDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GameController {
    private final GameService gameService;

    @PostMapping(path = "/game")
    @ResponseStatus(value = HttpStatus.CREATED)
    public GameDto createGame() {
        return gameService.createGame();
    }
}
