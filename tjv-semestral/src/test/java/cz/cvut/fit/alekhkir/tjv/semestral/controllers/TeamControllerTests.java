package cz.cvut.fit.alekhkir.tjv.semestral.controllers;

import cz.cvut.fit.alekhkir.tjv.semestral.dto.TeamDTO;
import cz.cvut.fit.alekhkir.tjv.semestral.exceptions.*;
import cz.cvut.fit.alekhkir.tjv.semestral.services.TeamService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TeamController.class)
public class TeamControllerTests {
    @MockBean
    TeamService teamService;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void testCreateTeam() throws Exception {
        /* Testing if we create team correctly */
        TeamDTO team = new TeamDTO(1L, "G2 Esports", new ArrayList<>(), new ArrayList<>());
        Mockito.when(teamService.createTeam(any(TeamDTO.class))).thenReturn(team);
        mockMvc.perform(post("/teams")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"G2 Esports\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", Matchers.is(1)))
                .andExpect(jsonPath("$.name", Matchers.is("G2 Esports")))
                .andExpect(jsonPath("$.playersIds", Matchers.is(Matchers.empty())))
                .andExpect(jsonPath("$.sponsorsIds", Matchers.is(Matchers.empty())));

        /* If we enter duplicate players or sponsors */
        Mockito.when(teamService.createTeam(any(TeamDTO.class))).thenThrow(InvalidInputException.class);
        mockMvc.perform(post("/teams")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"G2 Esports\",\"playersIds\":[1, 1]}"))
                .andExpect(status().isNotAcceptable());

        /* Trying to create team that has invalid players */
        Mockito.when(teamService.createTeam(any(TeamDTO.class))).thenThrow(PlayerNotFoundException.class);
        mockMvc.perform(post("/teams")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"G2 Esports\",\"playersIds\":[1]}"))
                .andExpect(status().isNotFound());

        /* Trying to create team that has invalid sponsors */
        Mockito.when(teamService.createTeam(any(TeamDTO.class))).thenThrow(PlayerNotFoundException.class);
        mockMvc.perform(post("/teams")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"G2 Esports\",\"sponsorsIds\":[1]}"))
                .andExpect(status().isNotFound());

        /* Trying to create same team more than once */
        Mockito.when(teamService.createTeam(any(TeamDTO.class))).thenThrow(TeamAlreadyExistsException.class);
        mockMvc.perform(post("/teams")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"G2 Esports\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    public void testGetTeam() throws Exception {
        TeamDTO team = new TeamDTO(1L, "G2 Esports", new ArrayList<>(), new ArrayList<>());

        Mockito.when(teamService.findDTOById(1L)).thenReturn(Optional.of(team));
        mockMvc.perform(get("/teams/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(1)))
                .andExpect(jsonPath("$.name", Matchers.is("G2 Esports")))
                .andExpect(jsonPath("$.playersIds", Matchers.is(Matchers.empty())))
                .andExpect(jsonPath("$.sponsorsIds", Matchers.is(Matchers.empty())));
    }

    @Test
    public void testGetAll() throws Exception {
        TeamDTO team1 = new TeamDTO(1L, "G2 Esports", new ArrayList<>(), new ArrayList<>());
        TeamDTO team2 = new TeamDTO(2L, "Fnatic", Collections.singletonList(3L), new ArrayList<>());
        List<TeamDTO> teams = List.of(team1, team2);

        Mockito.when(teamService.findAll()).thenReturn(teams);

        mockMvc.perform(get("/teams"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(2)))

                .andExpect(jsonPath("$[0].id", Matchers.is(1)))
                .andExpect(jsonPath("$[0].name", Matchers.is("G2 Esports")))
                .andExpect(jsonPath("$[0].playersIds", Matchers.is(Matchers.empty())))
                .andExpect(jsonPath("$[0].sponsorsIds", Matchers.is(Matchers.empty())))

                .andExpect(jsonPath("$[1].id", Matchers.is(2)))
                .andExpect(jsonPath("$[1].name", Matchers.is("Fnatic")))
                .andExpect(jsonPath("$[1].playersIds", Matchers.is(Collections.singletonList(3))))
                .andExpect(jsonPath("$[1].sponsorsIds", Matchers.is(Matchers.empty())));
    }

    @Test
    public void testUpdateTeam() throws Exception {
        /* Test teams' updating */
        TeamDTO updated = new TeamDTO(1L, "G2 Esports", new ArrayList<>(), new ArrayList<>());
        Mockito.when(teamService.updateTeam(any(Long.class), any(TeamDTO.class))).thenReturn(updated);
        mockMvc.perform(put("/teams/update/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"G2 Esports\"}"))
                .andExpect(jsonPath("$.id", Matchers.is(1)))
                .andExpect(jsonPath("$.name", Matchers.is("G2 Esports")))
                .andExpect(jsonPath("$.playersIds", Matchers.is(Matchers.empty())))
                .andExpect(jsonPath("$.sponsorsIds", Matchers.is(Matchers.empty())));

        /* If we enter duplicate players or sponsors */
        Mockito.when(teamService.updateTeam(any(Long.class), any(TeamDTO.class))).thenThrow(InvalidInputException.class);
        mockMvc.perform(put("/teams/update/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"G2 Esports\",\"playersIds\":[1, 1]}"))
                .andExpect(status().isNotAcceptable());

        /* Trying to update team with name that already exists */
        Mockito.when(teamService.updateTeam(any(Long.class), any(TeamDTO.class))).thenThrow(TeamAlreadyExistsException.class);
        mockMvc.perform(put("/teams/update/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"G2 Esports\"}"))
                .andExpect(status().isConflict());

        /* Trying to update team that was not found */
        Mockito.when(teamService.updateTeam(any(Long.class), any(TeamDTO.class))).thenThrow(TeamNotFoundException.class);
        mockMvc.perform(put("/teams/update/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"G2 Esports\"}"))
                .andExpect(status().isNotFound());

        /* Trying to update team with players that was not found */
        Mockito.when(teamService.updateTeam(any(Long.class), any(TeamDTO.class))).thenThrow(PlayerNotFoundException.class);
        mockMvc.perform(put("/teams/update/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"G2 Esports\",\"playersIds\":[1]}"))
                .andExpect(status().isNotFound());

        /* Trying to update team with sponsors that was not found */
        Mockito.when(teamService.updateTeam(any(Long.class), any(TeamDTO.class))).thenThrow(SponsorNotFoundException.class);
        mockMvc.perform(put("/teams/update/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"G2 Esports\",\"sponsorsIds\":[1]}"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteTeam() throws Exception {
        mockMvc.perform(delete("/teams/delete/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testAddPlayers() throws Exception {
        List<Long> players = List.of(1L, 2L);
        TeamDTO expectedTeam = new TeamDTO(1L, "G2 Esports", players, new ArrayList<>());

        Mockito.when(teamService.addPlayers(1L, players)).thenReturn(expectedTeam);
        mockMvc.perform(post("/teams/1/add_players")
                .contentType(MediaType.APPLICATION_JSON)
                .content("[1,2]"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.playersIds", Matchers.hasItems(1,2)));
    }

    @Test
    public void testAddSponsors() throws Exception{
        List<Long> sponsors = List.of(1L, 2L);
        TeamDTO expectedTeam = new TeamDTO(1L, "G2 Esports", new ArrayList<>(), sponsors);
        Mockito.when(teamService.addSponsors(1L, sponsors)).thenReturn(expectedTeam);

        mockMvc.perform(post("/teams/1/add_sponsors")
                .contentType(MediaType.APPLICATION_JSON)
                .content("[1,2]"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sponsorsIds", Matchers.hasItems(1,2)));
    }

    @Test
    public void testRemovePlayers() throws Exception{
        List<Long> players = List.of(1L, 2L);
        TeamDTO expectedTeam = new TeamDTO(1L, "G2 Esports", new ArrayList<>(), new ArrayList<>());
        Mockito.when(teamService.removePlayers(1L, players)).thenReturn(expectedTeam);

        mockMvc.perform(post("/teams/1/remove_players")
                .contentType(MediaType.APPLICATION_JSON)
                .content("[1,2]"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.playersIds", Matchers.everyItem(Matchers.not(1))))
                .andExpect(jsonPath("$.playersIds", Matchers.everyItem(Matchers.not(2))));
    }

    @Test
    public void testRemoveSponsors() throws Exception{
        List<Long> sponsors = List.of(1L, 2L);
        TeamDTO expectedTeam = new TeamDTO(1L, "G2 Esports", new ArrayList<>(), new ArrayList<>());
        Mockito.when(teamService.removeSponsors(1L, sponsors)).thenReturn(expectedTeam);

        mockMvc.perform(post("/teams/1/remove_sponsors")
                .contentType(MediaType.APPLICATION_JSON)
                .content("[1,2]"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sponsorsIds", Matchers.everyItem(Matchers.not(1))))
                .andExpect(jsonPath("$.sponsorsIds", Matchers.everyItem(Matchers.not(2))));
    }
}
