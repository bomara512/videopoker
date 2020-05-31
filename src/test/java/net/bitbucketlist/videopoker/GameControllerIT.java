package net.bitbucketlist.videopoker;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.bitbucketlist.videopoker.dto.CardDto;
import net.bitbucketlist.videopoker.dto.GameDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Collections;
import java.util.List;
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

    @Autowired
    ObjectMapper objectMapper;

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
    void changeBet_invalidState() throws Exception {
        GameDto game = gameService.createGame();
        gameService.deal(game.getId());

        mockMvc.perform(put("/game/" + game.getId() + "/bet?currentBet=5"))
            .andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.message").value("Unable to change bet for gameId: " + game.getId()));
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
        gameService.setCurrentBet(game.getId(), 5);

        mockMvc.perform(put("/game/" + game.getId() + "/deal"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.currentHand.length()").value(5))
            .andExpect(jsonPath("$.cardsRemainingInDeck").value(47))
            .andExpect(jsonPath("$.currentBalance").value(45));
    }

    @Test
    void deal_invalidState() throws Exception {
        GameDto game = gameService.createGame();
        gameService.deal(game.getId());

        mockMvc.perform(put("/game/" + game.getId() + "/deal"))
            .andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.message").value("Unable to deal for gameId: " + game.getId()));
    }

    @Test
    void deal_gameNotFound() throws Exception {
        UUID nonExistentGameId = UUID.randomUUID();

        mockMvc.perform(put("/game/" + nonExistentGameId + "/deal"))
            .andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.message").value("Game " + nonExistentGameId + " does not exist"));
    }

    @Test
    void draw() throws Exception {
        GameDto game = gameService.createGame();
        List<CardDto> originalHand = gameService.deal(game.getId()).getCurrentHand();

        MvcResult mvcResult = mockMvc.perform(put("/game/" + game.getId() + "/draw?holds=0,2,4"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.currentHand").isArray())
            .andExpect(jsonPath("$.currentHand.length()").value(5))
            .andExpect(jsonPath("$.cardsRemainingInDeck").value(45))
            .andExpect(jsonPath("$.gameState").value(GameState.READY_TO_DEAL.name()))
            .andReturn();

        GameDto gameDto = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), GameDto.class);
        assertThat(gameDto.getCurrentHand().get(0)).isEqualTo(originalHand.get(0));
        assertThat(gameDto.getCurrentHand().get(1)).isNotEqualTo(originalHand.get(1));
        assertThat(gameDto.getCurrentHand().get(2)).isEqualTo(originalHand.get(2));
        assertThat(gameDto.getCurrentHand().get(3)).isNotEqualTo(originalHand.get(3));
        assertThat(gameDto.getCurrentHand().get(4)).isEqualTo(originalHand.get(4));
    }

    @Test
    void draw_invalidState() throws Exception {
        GameDto game = gameService.createGame();
        gameService.deal(game.getId());
        gameService.draw(game.getId(), Collections.emptyList());

        mockMvc.perform(put("/game/" + game.getId() + "/draw?holds=0,2,4"))
            .andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.message").value("Unable to draw for gameId: " + game.getId()));
    }

    @Test
    void draw_gameNotFound() throws Exception {
        UUID nonExistentGameId = UUID.randomUUID();

        mockMvc.perform(put("/game/" + nonExistentGameId + "/deal"))
            .andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.message").value("Game " + nonExistentGameId + " does not exist"));
    }
}
