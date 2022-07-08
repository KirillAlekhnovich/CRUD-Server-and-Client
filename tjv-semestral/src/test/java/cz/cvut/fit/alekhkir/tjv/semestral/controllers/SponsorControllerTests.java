package cz.cvut.fit.alekhkir.tjv.semestral.controllers;

import cz.cvut.fit.alekhkir.tjv.semestral.dto.SponsorDTO;
import cz.cvut.fit.alekhkir.tjv.semestral.exceptions.*;
import cz.cvut.fit.alekhkir.tjv.semestral.services.SponsorService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SponsorController.class)
public class SponsorControllerTests {

    @MockBean
    SponsorService sponsorService;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void testCreateSponsor() throws Exception {
        /* Testing if we create sponsor correctly */
        SponsorDTO sponsor = new SponsorDTO(1L, "Logitech", new ArrayList<>());
        Mockito.when(sponsorService.createSponsor(any(SponsorDTO.class))).thenReturn(sponsor);
        mockMvc.perform(post("/sponsors")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Logitech\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", Matchers.is(1)))
                .andExpect(jsonPath("$.name", Matchers.is("Logitech")))
                .andExpect(jsonPath("$.teamsIds", Matchers.is(Matchers.empty())));

        /* If we enter duplicate teams */
        Mockito.when(sponsorService.createSponsor(any(SponsorDTO.class))).thenThrow(InvalidInputException.class);
        mockMvc.perform(post("/sponsors")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Logitech\",\"teamsIds\":[1, 1]}"))
                .andExpect(status().isNotAcceptable());

        /* Trying to create sponsor that supports invalid teams */
        Mockito.when(sponsorService.createSponsor(any(SponsorDTO.class))).thenThrow(TeamNotFoundException.class);
        mockMvc.perform(post("/sponsors")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Logitech\",\"teamsIds\":[1]}"))
                .andExpect(status().isNotFound());

        /* Trying to create same sponsor more than once */
        Mockito.when(sponsorService.createSponsor(any(SponsorDTO.class))).thenThrow(SponsorAlreadyExistsException.class);
        mockMvc.perform(post("/sponsors")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Logitech\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    public void testGetSponsor() throws Exception {
        SponsorDTO sponsor = new SponsorDTO(1L, "Logitech", new ArrayList<>());

        Mockito.when(sponsorService.findDTOById(1L)).thenReturn(Optional.of(sponsor));
        mockMvc.perform(get("/sponsors/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(1)))
                .andExpect(jsonPath("$.name", Matchers.is("Logitech")))
                .andExpect(jsonPath("$.teamsIds", Matchers.is(Matchers.empty())));
    }

    @Test
    public void testGetAll() throws Exception {
        SponsorDTO sponsor1 = new SponsorDTO(1L, "Logitech", new ArrayList<>());
        SponsorDTO sponsor2 = new SponsorDTO(2L, "Vodafone", Collections.singletonList(3L));
        List<SponsorDTO> sponsors = List.of(sponsor1, sponsor2);

        Mockito.when(sponsorService.findAll()).thenReturn(sponsors);

        mockMvc.perform(get("/sponsors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(2)))

                .andExpect(jsonPath("$[0].id", Matchers.is(1)))
                .andExpect(jsonPath("$[0].name", Matchers.is("Logitech")))
                .andExpect(jsonPath("$[0].teamsIds", Matchers.is(Matchers.empty())))

                .andExpect(jsonPath("$[1].id", Matchers.is(2)))
                .andExpect(jsonPath("$[1].name", Matchers.is("Vodafone")))
                .andExpect(jsonPath("$[1].teamsIds", Matchers.is(Collections.singletonList(3))));
    }

    @Test
    public void testUpdateSponsor() throws Exception {
        /* Test sponsors' updating */
        SponsorDTO updated = new SponsorDTO(1L, "Logitech", new ArrayList<>());
        Mockito.when(sponsorService.updateSponsor(any(Long.class), any(SponsorDTO.class))).thenReturn(updated);
        mockMvc.perform(put("/sponsors/update/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Logitech\"}"))
                .andExpect(jsonPath("$.id", Matchers.is(1)))
                .andExpect(jsonPath("$.name", Matchers.is("Logitech")))
                .andExpect(jsonPath("$.teamsIds", Matchers.is(Matchers.empty())));

        /* If we enter duplicate teams */
        Mockito.when(sponsorService.updateSponsor(any(Long.class), any(SponsorDTO.class))).thenThrow(InvalidInputException.class);
        mockMvc.perform(put("/sponsors/update/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Logitech\",\"teamsIds\":[1, 1]}"))
                .andExpect(status().isNotAcceptable());

        /* Trying to update sponsor with name that already exists */
        Mockito.when(sponsorService.updateSponsor(any(Long.class), any(SponsorDTO.class))).thenThrow(SponsorAlreadyExistsException.class);
        mockMvc.perform(put("/sponsors/update/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Logitech\"}"))
                .andExpect(status().isConflict());

        /* Trying to update sponsor that was not found */
        Mockito.when(sponsorService.updateSponsor(any(Long.class), any(SponsorDTO.class))).thenThrow(SponsorNotFoundException.class);
        mockMvc.perform(put("/sponsors/update/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Logitech\"}"))
                .andExpect(status().isNotFound());

        /* Trying to update sponsor with team that was not found */
        Mockito.when(sponsorService.updateSponsor(any(Long.class), any(SponsorDTO.class))).thenThrow(TeamNotFoundException.class);
        mockMvc.perform(put("/sponsors/update/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Logitech\",\"teamsIds\":[1]}"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteSponsor() throws Exception {
        mockMvc.perform(delete("/sponsors/delete/1"))
                .andExpect(status().isOk());
    }
}
