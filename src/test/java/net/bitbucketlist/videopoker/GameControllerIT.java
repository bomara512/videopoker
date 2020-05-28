package net.bitbucketlist.videopoker;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = VideoPokerApplication.class)
@AutoConfigureMockMvc
class GameControllerIT {
    @Autowired
    MockMvc mockMvc;

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
}
