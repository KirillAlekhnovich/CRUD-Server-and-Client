package cz.cvut.fit.alekhkir.tjv.semestral.services;

import cz.cvut.fit.alekhkir.tjv.semestral.dto.TeamDTO;
import cz.cvut.fit.alekhkir.tjv.semestral.entities.Team;
import cz.cvut.fit.alekhkir.tjv.semestral.repository.PlayerRepository;
import cz.cvut.fit.alekhkir.tjv.semestral.repository.TeamRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TeamServiceTests {

    @InjectMocks
    TeamService teamService;

    @Mock
    TeamRepository teamRepository;

    @Mock
    PlayerRepository playerRepository;

    @Test
    public void testCreateTeam() throws Exception{
        Team team = new Team("G2 Esports", null, null);
        TeamDTO teamDTO = new TeamDTO(1L, "G2 Esports", null, null);
        Mockito.when(teamRepository.save(any(Team.class))).thenReturn(team);
        TeamDTO returnedDTO = teamService.createTeam(teamDTO);
        assertEquals(teamDTO.getName(),returnedDTO.getName());
        assertEquals(teamDTO.getPlayersIds(),returnedDTO.getPlayersIds());
        assertEquals(teamDTO.getSponsorsIds(),returnedDTO.getSponsorsIds());
        verify(teamRepository, times(1)).save(any(Team.class));
    }

    @Test
    public void testGetAll(){
        Team team1 = new Team("G2 Esports", null, null);
        Team team2 = new Team("Fnatic", null, null);
        List<Team> teams = List.of(team1, team2);
        Mockito.when(teamRepository.findAll()).thenReturn(teams);
        List<TeamDTO> returnedTeams = teamService.findAll();

        assertEquals(2, returnedTeams.size());
        verify(teamRepository, times(1)).findAll();
    }

    @Test
    public void testUpdateTeam() throws Exception{
        Team team = new Team("G2 Esports", null, null);
        team.setId(1L);
        TeamDTO teamDTO = new TeamDTO(2L, "Fnatic", null, null);

        Mockito.when(teamRepository.save(any(Team.class))).thenReturn(team);
        Mockito.when(teamRepository.findById(1L)).thenReturn(Optional.of(team));

        TeamDTO returnedDTO = teamService.updateTeam(1L, teamDTO);
        assertEquals(teamDTO.getName(),returnedDTO.getName());
        assertEquals(teamDTO.getPlayersIds(),returnedDTO.getPlayersIds());
        assertEquals(teamDTO.getSponsorsIds(),returnedDTO.getSponsorsIds());
    }

    @Test
    public void deleteTeam() throws Exception{
        Team team = new Team("G2 Esports", null, null);
        team.setId(1L);
        Mockito.when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
        teamService.deleteTeam(team.getId());
        verify(teamRepository, times(1)).deleteById(1L);
    }
}
