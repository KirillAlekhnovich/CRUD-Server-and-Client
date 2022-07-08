package cz.cvut.fit.alekhkir.tjv.semestral.services;

import cz.cvut.fit.alekhkir.tjv.semestral.dto.PlayerDTO;
import cz.cvut.fit.alekhkir.tjv.semestral.entities.Player;
import cz.cvut.fit.alekhkir.tjv.semestral.entities.Sponsor;
import cz.cvut.fit.alekhkir.tjv.semestral.entities.Team;
import cz.cvut.fit.alekhkir.tjv.semestral.repository.PlayerRepository;
import cz.cvut.fit.alekhkir.tjv.semestral.repository.TeamRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PlayerServiceTests {

    @InjectMocks
    PlayerService playerService;

    @Mock
    PlayerRepository playerRepository;

    @Mock
    TeamRepository teamRepository;

    @Test
    public void testCreatePlayer() throws Exception {
        Team expectedTeam = new Team("G2 Esports", new ArrayList<Player>(), new ArrayList<Sponsor>());
        expectedTeam.setId(1L);
        Player player = new Player("Martin", "Larsson", "Rekkles", 24, "Sweden", expectedTeam);
        PlayerDTO playerDTO = new PlayerDTO(2L, "Martin", "Larsson", "Rekkles", 24, "Sweden", 1L);
        Mockito.when(playerRepository.save(any(Player.class))).thenReturn(player);
        Mockito.when(teamRepository.findById(1L)).thenReturn(Optional.of(expectedTeam));

        PlayerDTO returnedDTO = playerService.createPlayer(playerDTO);
        assertEquals(playerDTO.getName(), returnedDTO.getName());
        assertEquals(playerDTO.getSurname(), returnedDTO.getSurname());
        assertEquals(playerDTO.getNickname(), returnedDTO.getNickname());
        assertEquals(playerDTO.getAge(), returnedDTO.getAge());
        assertEquals(playerDTO.getCountryOfBirth(), returnedDTO.getCountryOfBirth());
        assertEquals(playerDTO.getTeamId(), returnedDTO.getTeamId());

        verify(playerRepository, times(1)).save(any(Player.class));
    }

    @Test
    public void testGetAll(){
        Player player1 = new Player("Martin", "Larsson", "Rekkles", 24, "Sweden", null);
        Player player2 = new Player("Rasmus", "Winther", "Caps", 21, "Denmark", null);
        List<Player> players = List.of(player1, player2);
        Mockito.when(playerRepository.findAll()).thenReturn(players);
        List<PlayerDTO> returnedPlayers = playerService.findAll();

        assertEquals(2, returnedPlayers.size());
        verify(playerRepository, times(1)).findAll();
    }

    @Test
    public void testUpdatePlayer() throws Exception{
        Player expectedPlayer = new Player("Martin", "Larsson", "Rekkles", 24, "Sweden", null);
        expectedPlayer.setId(1L);
        PlayerDTO playerDTO = new PlayerDTO(2L, "Rasmus", "Winther", "Caps", 21, "Denmark", null);
        Mockito.when(playerRepository.save(any(Player.class))).thenReturn(expectedPlayer);
        Mockito.when(playerRepository.findById(1L)).thenReturn(Optional.of(expectedPlayer));

        PlayerDTO returnedDTO = playerService.updatePlayer(1L, playerDTO);
        assertEquals(playerDTO.getName(), returnedDTO.getName());
        assertEquals(playerDTO.getSurname(), returnedDTO.getSurname());
        assertEquals(playerDTO.getNickname(), returnedDTO.getNickname());
        assertEquals(playerDTO.getAge(), returnedDTO.getAge());
        assertEquals(playerDTO.getCountryOfBirth(), returnedDTO.getCountryOfBirth());
        assertEquals(playerDTO.getTeamId(), returnedDTO.getTeamId());
    }

    @Test
    public void testDeletePlayer() throws Exception {
        Player player = new Player("Martin", "Larsson", "Rekkles", 24, "Sweden", null);
        player.setId(1L);
        Mockito.when(playerRepository.findById(1L)).thenReturn(Optional.of(player));
        playerService.deletePlayer(player.getId());
        verify(playerRepository, times(1)).deleteById(1L);
    }
}
