package net.bitbucketlist.videopoker;

import net.bitbucketlist.videopoker.dto.GameDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = VideoPokerApplication.class)
@AutoConfigureMockMvc
class GameControllerIT {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    GameService gameService;

    @Test
    void createGame() throws Exception {
        mockMvc.perform(post("/game"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.currentBet").value(1))
            .andExpect(jsonPath("$.currentHand").isArray())
            .andExpect(jsonPath("$.currentHand").isEmpty())
            .andExpect(jsonPath("$.cardsRemainingInDeck").value(52));
    }

    @Test
    void changeBet() throws Exception {
        GameDto game = gameService.createGame();

        assertThat(game.getCurrentBet()).isEqualTo(1);

        mockMvc.perform(put("/game/" + game.getId() + "/bet?currentBet=5"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.currentBet").value(5));
    }

    @Test
    void changeBet_gameNotFound() throws Exception {
        UUID nonExistentGameId = UUID.randomUUID();

        mockMvc.perform(put("/game/" + nonExistentGameId + "/bet?currentBet=5"))
            .andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.message").value("Game " + nonExistentGameId + " does not exist"));
    }

    @Test
    void deal() throws Exception {
        GameDto game = gameService.createGame();

        mockMvc.perform(put("/game/" + game.getId() + "/deal"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.currentHand.length()").value(5))
            .andExpect(jsonPath("$.cardsRemainingInDeck").value(47));
    }
}
