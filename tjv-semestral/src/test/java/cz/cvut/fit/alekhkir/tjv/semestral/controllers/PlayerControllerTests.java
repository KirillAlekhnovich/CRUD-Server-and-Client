package cz.cvut.fit.alekhkir.tjv.semestral.controllers;

import cz.cvut.fit.alekhkir.tjv.semestral.dto.PlayerDTO;
import cz.cvut.fit.alekhkir.tjv.semestral.exceptions.InvalidInputException;
import cz.cvut.fit.alekhkir.tjv.semestral.exceptions.PlayerAlreadyExistsException;
import cz.cvut.fit.alekhkir.tjv.semestral.exceptions.PlayerNotFoundException;
import cz.cvut.fit.alekhkir.tjv.semestral.exceptions.TeamNotFoundException;
import cz.cvut.fit.alekhkir.tjv.semestral.services.PlayerService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Optional;

@WebMvcTest(PlayerController.class)
public class PlayerControllerTests {

    @MockBean
    PlayerService playerService;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void testCreatePlayer() throws Exception {
        /* Testing if we create player correctly */
        PlayerDTO player = new PlayerDTO(1L, "Martin", "Larsson", "Rekkles", 24, "Sweden", 1L);
        Mockito.when(playerService.createPlayer(any(PlayerDTO.class))).thenReturn(player);
        mockMvc.perform(post("/players")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Martin\",\"surname\":\"Larsson\",\"nickname\":\"Rekkles\",\"age\":24,\"countryOfBirth\":\"Sweden\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", Matchers.is("Martin")))
                .andExpect(jsonPath("$.surname", Matchers.is("Larsson")))
                .andExpect(jsonPath("$.nickname", Matchers.is("Rekkles")))
                .andExpect(jsonPath("$.age", Matchers.is(24)))
                .andExpect(jsonPath("$.countryOfBirth", Matchers.is("Sweden")));

        /* If the team has 5 players already */
        Mockito.when(playerService.createPlayer(any(PlayerDTO.class))).thenThrow(InvalidInputException.class);
        mockMvc.perform(post("/players")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Rasmus\",\"surname\":\"Winther\",\"nickname\":\"Caps\",\"age\":21,\"countryOfBirth\":\"Denmark\",\"teamId\":2}"))
                .andExpect(status().isNotAcceptable());

        /* Testing if we can create player with team that does not exist */
        Mockito.when(playerService.createPlayer(any(PlayerDTO.class))).thenThrow(TeamNotFoundException.class);
        mockMvc.perform(post("/players")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Rasmus\",\"surname\":\"Winther\",\"nickname\":\"Caps\",\"age\":21,\"countryOfBirth\":\"Denmark\",\"teamId\":2}"))
                .andExpect(status().isNotFound());

        /* Testing if we can create player twice */
        Mockito.when(playerService.createPlayer(any(PlayerDTO.class))).thenThrow(PlayerAlreadyExistsException.class);
        mockMvc.perform(post("/players")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Martin\",\"surname\":\"Larsson\",\"nickname\":\"Rekkles\",\"age\":24,\"countryOfBirth\":\"Sweden\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    public void testGetPlayer() throws Exception {
        PlayerDTO player = new PlayerDTO(1L, "Martin", "Larsson", "Rekkles", 24, "Sweden", 1L);
        Mockito.when(playerService.findDTOById(1L)).thenReturn(Optional.of(player));
        mockMvc.perform(get("/players/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", Matchers.is("Martin")))
                .andExpect(jsonPath("$.surname", Matchers.is("Larsson")))
                .andExpect(jsonPath("$.nickname", Matchers.is("Rekkles")))
                .andExpect(jsonPath("$.age", Matchers.is(24)))
                .andExpect(jsonPath("$.countryOfBirth", Matchers.is("Sweden")));
    }

    @Test
    public void testGetAll() throws Exception {
        PlayerDTO player1 = new PlayerDTO(1L, "Martin", "Larsson", "Rekkles", 24, "Sweden", 1L);
        PlayerDTO player2 = new PlayerDTO(2L, "Rasmus", "Winther", "Caps", 21, "Denmark", 1L);
        List<PlayerDTO> players = List.of(player1, player2);

        Mockito.when(playerService.findAll()).thenReturn(players);

        mockMvc.perform(get("/players"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(2)))

                .andExpect(jsonPath("$[0].name", Matchers.is("Martin")))
                .andExpect(jsonPath("$[0].surname", Matchers.is("Larsson")))
                .andExpect(jsonPath("$[0].nickname", Matchers.is("Rekkles")))
                .andExpect(jsonPath("$[0].age", Matchers.is(24)))
                .andExpect(jsonPath("$[0].countryOfBirth", Matchers.is("Sweden")))

                .andExpect(jsonPath("$[1].name", Matchers.is("Rasmus")))
                .andExpect(jsonPath("$[1].surname", Matchers.is("Winther")))
                .andExpect(jsonPath("$[1].nickname", Matchers.is("Caps")))
                .andExpect(jsonPath("$[1].age", Matchers.is(21)))
                .andExpect(jsonPath("$[1].countryOfBirth", Matchers.is("Denmark")));
    }

    @Test
    public void testUpdatePlayer() throws Exception {
        /* Test players' updating */
        PlayerDTO updated = new PlayerDTO(1L, "Martin", "Larsson", "Rekkles", 24, "Sweden", 1L);
        Mockito.when(playerService.updatePlayer(any(Long.class), any(PlayerDTO.class))).thenReturn(updated);
        mockMvc.perform(put("/players/update/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Martin\",\"surname\":\"Larsson\",\"nickname\":\"Rekkles\",\"age\":24,\"countryOfBirth\":\"Sweden\"}"))
                .andExpect(jsonPath("$.name", Matchers.is("Martin")))
                .andExpect(jsonPath("$.surname", Matchers.is("Larsson")))
                .andExpect(jsonPath("$.nickname", Matchers.is("Rekkles")))
                .andExpect(jsonPath("$.age", Matchers.is(24)))
                .andExpect(jsonPath("$.countryOfBirth", Matchers.is("Sweden")));

        /* If the team has 5 players already */
        Mockito.when(playerService.updatePlayer(any(Long.class), any(PlayerDTO.class))).thenThrow(InvalidInputException.class);
        mockMvc.perform(put("/players/update/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Rasmus\",\"surname\":\"Winther\",\"nickname\":\"Caps\",\"age\":21,\"countryOfBirth\":\"Denmark\",\"teamId\":2}"))
                .andExpect(status().isNotAcceptable());

        /* Trying to update player with nickname that already exists */
        Mockito.when(playerService.updatePlayer(any(Long.class), any(PlayerDTO.class))).thenThrow(PlayerAlreadyExistsException.class);
        mockMvc.perform(put("/players/update/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Martin\",\"surname\":\"Larsson\",\"nickname\":\"Rekkles\",\"age\":24,\"countryOfBirth\":\"Sweden\"}"))
                .andExpect(status().isConflict());

        /* Trying to update player that was not found */
        Mockito.when(playerService.updatePlayer(any(Long.class), any(PlayerDTO.class))).thenThrow(PlayerNotFoundException.class);
        mockMvc.perform(put("/players/update/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Martin\",\"surname\":\"Larsson\",\"nickname\":\"Rekkles\",\"age\":24,\"countryOfBirth\":\"Sweden\"}"))
                .andExpect(status().isNotFound());

        /* Trying to update player with team that was not found */
        Mockito.when(playerService.updatePlayer(any(Long.class), any(PlayerDTO.class))).thenThrow(TeamNotFoundException.class);
        mockMvc.perform(put("/players/update/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Martin\",\"surname\":\"Larsson\",\"nickname\":\"Rekkles\",\"age\":24,\"countryOfBirth\":\"Sweden\"},\"teamId\":2}"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeletePlayer() throws Exception {
        mockMvc.perform(delete("/players/delete/1"))
                .andExpect(status().isOk());
    }
}
