package net.bitbucketlist.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.bitbucketlist.videopoker.GameService;
import net.bitbucketlist.videopoker.GameState;
import net.bitbucketlist.videopoker.TestRedisConfiguration;
import net.bitbucketlist.videopoker.VideoPokerApplication;
import net.bitbucketlist.videopoker.dto.CardDto;
import net.bitbucketlist.videopoker.dto.GameDto;
import net.bitbucketlist.videopoker.persistence.GameRepository;
import org.junit.jupiter.api.BeforeEach;
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
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {VideoPokerApplication.class, TestRedisConfiguration.class})
@AutoConfigureMockMvc
class GameControllerIT {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    GameService gameService;

    @Autowired
    GameRepository gameRepository;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        gameRepository.deleteAll();
    }

    @Test
    void createGame() throws Exception {
        mockMvc.perform(post("/game"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.bet").value(1))
            .andExpect(jsonPath("$.hand").isArray())
            .andExpect(jsonPath("$.hand").isEmpty())
            .andExpect(jsonPath("$.deckSize").value(52))
            .andExpect(jsonPath("$.gameState").value(GameState.READY_TO_DEAL.name()))
            .andExpect(jsonPath("$.credits").value(50))
            .andExpect(jsonPath("$.handRank").isEmpty());
    }

    @Test
    void getAllGames() throws Exception {
        GameDto game1 = gameService.createGame();
        GameDto game2 = gameService.createGame();

        mockMvc.perform(get("/game"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[*].id", containsInAnyOrder(game1.getId().toString(), game2.getId().toString())));
    }

    @Test
    void changeBet() throws Exception {
        GameDto game = gameService.createGame();

        assertThat(game.getBet()).isEqualTo(1);

        mockMvc.perform(put("/game/" + game.getId() + "/bet?amount=5"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.bet").value(5));
    }

    @Test
    void changeBet_invalidState() throws Exception {
        GameDto game = gameService.createGame();
        gameService.deal(game.getId());

        mockMvc.perform(put("/game/" + game.getId() + "/bet?amount=5"))
            .andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.message").value("Unable to change bet for gameId: " + game.getId()));
    }

    @Test
    void changeBet_gameNotFound() throws Exception {
        UUID nonExistentGameId = UUID.randomUUID();

        mockMvc.perform(put("/game/" + nonExistentGameId + "/bet?amount=5"))
            .andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.message").value("Game " + nonExistentGameId + " does not exist"));
    }

    @Test
    void deal() throws Exception {
        GameDto game = gameService.createGame();
        gameService.setBet(game.getId(), 5);

        mockMvc.perform(put("/game/" + game.getId() + "/deal"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(game.getId().toString()))
            .andExpect(jsonPath("$.hand.length()").value(5))
            .andExpect(jsonPath("$.deckSize").value(47))
            .andExpect(jsonPath("$.gameState").value(GameState.READY_TO_DRAW.name()))
            .andExpect(jsonPath("$.credits").value(45))
            .andExpect(jsonPath("$.handRank").isNotEmpty());
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
        List<CardDto> originalHand = gameService.deal(game.getId()).getHand();

        MvcResult mvcResult = mockMvc.perform(put("/game/" + game.getId() + "/draw?holds=0,2,4"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.hand").isArray())
            .andExpect(jsonPath("$.hand.length()").value(5))
            .andExpect(jsonPath("$.deckSize").value(45))
            .andExpect(jsonPath("$.gameState").value(GameState.READY_TO_DEAL.name()))
            .andExpect(jsonPath("$.handRank").isNotEmpty())
            .andReturn();

        GameDto gameDto = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), GameDto.class);
        assertThat(gameDto.getHand().get(0)).isEqualTo(originalHand.get(0));
        assertThat(gameDto.getHand().get(1)).isNotEqualTo(originalHand.get(1));
        assertThat(gameDto.getHand().get(2)).isEqualTo(originalHand.get(2));
        assertThat(gameDto.getHand().get(3)).isNotEqualTo(originalHand.get(3));
        assertThat(gameDto.getHand().get(4)).isEqualTo(originalHand.get(4));
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
