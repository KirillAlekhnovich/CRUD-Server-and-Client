package cz.cvut.fit.alekhkir.tjv.semestral.services;

import cz.cvut.fit.alekhkir.tjv.semestral.dto.TeamDTO;
import cz.cvut.fit.alekhkir.tjv.semestral.entities.Player;
import cz.cvut.fit.alekhkir.tjv.semestral.entities.Sponsor;
import cz.cvut.fit.alekhkir.tjv.semestral.entities.Team;
import cz.cvut.fit.alekhkir.tjv.semestral.exceptions.*;
import cz.cvut.fit.alekhkir.tjv.semestral.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TeamService {
    private final TeamRepository teamRepository;
    private final SponsorService sponsorService;
    private final PlayerService playerService;

    @Autowired
    public TeamService(TeamRepository teamRepository, SponsorService sponsorService, PlayerService playerService) {
        this.teamRepository = teamRepository;
        this.sponsorService = sponsorService;
        this.playerService = playerService;
    }

    /* Checking if all listed sponsors exist and there are no duplicates */
    private void sponsorsCheckIfValidOrThrowException(TeamDTO teamDTO, List<Sponsor> sponsors) throws InvalidInputException, SponsorNotFoundException {
        List<Long> sponsorIds = teamDTO.getSponsorsIds();
        if (listHasDuplicates(sponsorIds)) {
            throw new InvalidInputException("Input is invalid. Duplicates detected.");
        }
        if (sponsors.size() != teamDTO.getSponsorsIds().size()) {
            throw new SponsorNotFoundException("Some of the sponsors do not exist.");
        }
    }

    /* Checking if all listed players exist and there are no duplicates */
    private void playersCheckIfValidOrThrowException(TeamDTO teamDTO, List<Player> players) throws InvalidInputException, PlayerNotFoundException {
        List<Long> playersIds = teamDTO.getPlayersIds();
        if (listHasDuplicates(playersIds)) {
            throw new InvalidInputException("Input is invalid. Duplicates detected.");
        }
        if (players.size() > 5) {
            throw new InvalidInputException("There are too many players in a team.");
        }
        if (players.size() != teamDTO.getPlayersIds().size()) {
            throw new PlayerNotFoundException("Some of the players do not exist.");
        }
    }

    public TeamDTO createTeam(TeamDTO teamDTO) throws TeamAlreadyExistsException, SponsorNotFoundException, PlayerNotFoundException, InvalidInputException {
        Optional<Team> optionalName = Optional.ofNullable(teamRepository.findByName(teamDTO.getName()));
        if (optionalName.isPresent()) {
            throw new TeamAlreadyExistsException("Team " + teamDTO.getName() + " already exists.");
        }
        List<Sponsor> sponsors = new ArrayList<>();
        List<Player> players = new ArrayList<>();
        // Adding sponsors to this team
        if (teamDTO.getSponsorsIds() != null) {
            sponsors = sponsorService.findByIds(teamDTO.getSponsorsIds());
            sponsorsCheckIfValidOrThrowException(teamDTO, sponsors);
        }
        // Adding players to this team
        if (teamDTO.getPlayersIds() != null) {
            players = playerService.findByIds(teamDTO.getPlayersIds());
            playersCheckIfValidOrThrowException(teamDTO, players);
        }
        Team team = new Team(teamDTO.getName(), players, sponsors);
        return toDTO(teamRepository.save(team));
    }

    public TeamDTO getTeam(Long id) throws TeamNotFoundException {
        Optional<Team> optionalId = teamRepository.findById(id);
        if (optionalId.isEmpty()) {
            throw new TeamNotFoundException("Team with id " + id + " was not found");
        }
        Team team = optionalId.get();
        return toDTO(team);
    }

    public TeamDTO updateTeam(Long id, TeamDTO newTeamDTO) throws TeamNotFoundException, TeamAlreadyExistsException,
            SponsorNotFoundException, PlayerNotFoundException, InvalidInputException {
        Optional<Team> optionalId = findById(id);
        Optional<Team> optionalName = Optional.ofNullable(teamRepository.findByName(newTeamDTO.getName()));
        if (optionalId.isEmpty()) {
            throw new TeamNotFoundException("Team with id " + id + " does not exist.");
        }
        Team updatedTeam = optionalId.get();
        // If team with this name already exists and current team has different name
        if (optionalName.isPresent() && !(updatedTeam.getName().equals(newTeamDTO.getName()))) {
            throw new TeamAlreadyExistsException("Team with name " + newTeamDTO.getName() + " already exists.");
        }
        updatedTeam.setName(newTeamDTO.getName());
        // Updating sponsors and players
        updatingTeamSponsors(updatedTeam, newTeamDTO);
        updatingTeamPlayers(updatedTeam, newTeamDTO);
        return toDTO(teamRepository.save(updatedTeam));
    }

    public void updatingTeamSponsors(Team updatedTeam, TeamDTO newTeamDTO) throws InvalidInputException, SponsorNotFoundException {
        updatedTeam.setSponsors(null);
        if (newTeamDTO.getSponsorsIds() != null) {
            addSponsorsCheck(newTeamDTO.getSponsorsIds());
            List<Sponsor> newTeamSponsors = sponsorService.findByIds(newTeamDTO.getSponsorsIds());
            updatedTeam.setSponsors(newTeamSponsors);
        }
    }

    public void updatingTeamPlayers(Team updatedTeam, TeamDTO newTeamDTO) throws InvalidInputException, PlayerNotFoundException {
        updatedTeam.setPlayers(null);
        if (newTeamDTO.getPlayersIds() == null) {
            return;
        }
        addPlayersCheck(updatedTeam, newTeamDTO.getPlayersIds());
        if (updatedTeam.getPlayers() != null) {
            for (Player player : updatedTeam.getPlayers()) {
                player.setTeam(null);
            }
        }
        List<Player> newTeamPlayers = playerService.findByIds(newTeamDTO.getPlayersIds());
        // Deleting players from last team and setting updated team as their current
        for (Player newTeamPlayer : newTeamPlayers) {
            if (newTeamPlayer.getTeam() != null) {
                newTeamPlayer.getTeam().removePlayer(newTeamPlayer);
            }
            newTeamPlayer.setTeam(updatedTeam);
        }
        updatedTeam.setPlayers(newTeamPlayers);
    }

    public void deleteTeam(Long id) throws TeamNotFoundException {
        Optional<Team> optionalId = teamRepository.findById(id);
        if (optionalId.isEmpty())
            throw new TeamNotFoundException("Team with id " + id + " does not exist.");
        Team team = optionalId.get();
        // Deleting team from players' entity
        if (team.getPlayers() != null) {
            for (Player player : team.getPlayers()) {
                player.setTeam(null);
            }
        }
        teamRepository.deleteById(id);
    }

    public List<TeamDTO> findAll() {
        return teamRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    // Returns true if there are any duplicates in a list
    Boolean listHasDuplicates(List<Long> ids) {
        List<Long> sortedIds = new ArrayList<Long>(ids);
        sortedIds.sort(Comparator.naturalOrder());
        for (int i = 0; i < sortedIds.size() - 1; i++) {
            if (sortedIds.get(i).equals(sortedIds.get(i + 1))) {
                return true;
            }
        }
        return false;
    }

    void addPlayersCheck(Team team, List<Long> playersIds) throws InvalidInputException, PlayerNotFoundException {
        if (listHasDuplicates(playersIds)) {
            throw new InvalidInputException("Input is invalid. Duplicates detected.");
        }
        for (Long playersId : playersIds) {
            Optional<Player> optionalId = playerService.findById(playersId);
            if (optionalId.isEmpty()) {
                throw new PlayerNotFoundException("One of the players does not exist.");
            }
        }
        if (team.getPlayers() != null) {
            if (team.getPlayers().size() + playersIds.size() > 5) {
                throw new InvalidInputException("Invalid input. Number of players in a team is greater than 5.");
            }
        } else if (playersIds.size() > 5) {
            throw new InvalidInputException("Invalid input. Number of players in a team is greater than 5.");
        }
    }

    public TeamDTO addPlayers(Long teamId, List<Long> newPlayersIds) throws TeamNotFoundException, PlayerCantBeAddedException, InvalidInputException, PlayerNotFoundException, PlayerAlreadyExistsException {
        Optional<Team> optionalId = teamRepository.findById(teamId);
        if (optionalId.isEmpty()) {
            throw new TeamNotFoundException("Team with id " + teamId + " was not found.");
        }
        if (newPlayersIds.isEmpty()) {
            throw new InvalidInputException("Invalid input. There are no players.");
        }
        Team team = optionalId.get();
        addPlayersCheck(team, newPlayersIds);
        // Checking if team already has one of the players
        for (Long newPlayerId : newPlayersIds) {
            for (Player teamPlayers : team.getPlayers()) {
                if (teamPlayers.getId().equals(newPlayerId)) {
                    throw new PlayerAlreadyExistsException("One of the players is already a part of a team.");
                }
            }
        }
        // Updating teams' players list
        for (Long newPlayersId : newPlayersIds) {
            Player player = playerService.findById(newPlayersId).get();
            if (player.getTeam() != null) {
                player.getTeam().removePlayer(player);
            }
            player.setTeam(team);
            team.addPlayer(player);
        }
        return toDTO(teamRepository.save(team));
    }

    void addSponsorsCheck(List<Long> sponsorsIds) throws InvalidInputException, SponsorNotFoundException {
        if (listHasDuplicates(sponsorsIds)) {
            throw new InvalidInputException("Input is invalid. Duplicates detected.");
        }
        for (Long sponsorsId : sponsorsIds) {
            Optional<Sponsor> optionalId = sponsorService.findById(sponsorsId);
            if (optionalId.isEmpty()) {
                throw new SponsorNotFoundException("Some sponsors do not exist.");
            }
        }
    }

    public TeamDTO addSponsors(Long teamId, List<Long> newSponsorsIds) throws TeamNotFoundException, SponsorNotFoundException, SponsorAlreadyExistsException, InvalidInputException {
        Optional<Team> optionalId = teamRepository.findById(teamId);
        if (optionalId.isEmpty()) {
            throw new TeamNotFoundException("Team with id " + teamId + " was not found.");
        }
        if (newSponsorsIds.isEmpty()) {
            throw new InvalidInputException("Invalid input. There are no sponsors.");
        }
        Team team = optionalId.get();
        addSponsorsCheck(newSponsorsIds);
        // Checking if team already has one of the sponsors
        for (Long newSponsorId : newSponsorsIds) {
            for (Sponsor sponsor : team.getSponsors()) {
                if (sponsor.getId().equals(newSponsorId)) {
                    throw new SponsorAlreadyExistsException("One of the sponsors already support this team.");
                }
            }
        }
        // Updating teams' sponsors list
        for (Long newSponsorsId : newSponsorsIds) {
            Sponsor sponsor = sponsorService.findById(newSponsorsId).get();
            team.addSponsor(sponsor);
        }
        return toDTO(teamRepository.save(team));
    }

    void removePlayersCheck(Team team, List<Long> playersToRemoveIds) throws InvalidInputException, PlayerNotFoundException {
        if (playersToRemoveIds.isEmpty()) {
            throw new InvalidInputException("Invalid input. There are no players.");
        }
        if (listHasDuplicates(playersToRemoveIds)) {
            throw new InvalidInputException("Input is invalid. Duplicates detected.");
        }
        if (playersToRemoveIds.size() > 5) {
            throw new InvalidInputException("Input is invalid. There can't be more than 5 players.");
        }
        for (Long playerToRemoveId : playersToRemoveIds) {
            boolean playerIsInThisTeam = false;
            for (Player teamPlayer : team.getPlayers()) {
                // If current player from list of id's to delete matches one of the players in the team
                if (playerToRemoveId.equals(teamPlayer.getId())) {
                    playerIsInThisTeam = true;
                    break;
                }
            }
            if (!playerIsInThisTeam) {
                throw new PlayerNotFoundException("One of the players is not in a team.");
            }
        }
    }

    public TeamDTO removePlayers(Long teamId, List<Long> playersIds) throws TeamNotFoundException, PlayerNotFoundException, InvalidInputException {
        Optional<Team> optionalId = teamRepository.findById(teamId);
        if (optionalId.isEmpty()) {
            throw new TeamNotFoundException("Team with id " + teamId + " was not found.");
        }
        Team team = optionalId.get();
        if (team.getPlayers().isEmpty()) {
            throw new PlayerNotFoundException("There are no players in the team.");
        }
        removePlayersCheck(team, playersIds);
        for (Long playerId : playersIds) {
            Optional<Player> optPlayer = playerService.findById(playerId);
            if (optPlayer.isEmpty()) {
                throw new PlayerNotFoundException("Player does not exist.");
            }
            Player player = optPlayer.get();
            team.removePlayer(player);
            player.setTeam(null);
        }
        return toDTO(teamRepository.save(team));
    }

    void removeSponsorsCheck(Team team, List<Long> sponsorsToRemoveIds) throws InvalidInputException, SponsorNotFoundException {
        if (sponsorsToRemoveIds.isEmpty()) {
            throw new InvalidInputException("Invalid input. There are no sponsors.");
        }
        if (listHasDuplicates(sponsorsToRemoveIds)) {
            throw new InvalidInputException("Input is invalid. Duplicates detected.");
        }
        for (Long sponsorToRemoveId : sponsorsToRemoveIds) {
            boolean sponsorIsInTheTeam = false;
            for (Sponsor teamSponsor : team.getSponsors()) {
                // If current sponsor from list of id's to delete matches one of the sponsors in the team
                if (sponsorToRemoveId.equals(teamSponsor.getId())) {
                    sponsorIsInTheTeam = true;
                    break;
                }
            }
            if (!sponsorIsInTheTeam) {
                throw new SponsorNotFoundException("One of the sponsors do not support this team");
            }
        }
    }

    public TeamDTO removeSponsors(Long teamId, List<Long> sponsorsIds) throws TeamNotFoundException, SponsorNotFoundException, InvalidInputException {
        Optional<Team> optionalId = teamRepository.findById(teamId);
        if (optionalId.isEmpty()) {
            throw new TeamNotFoundException("Team with id " + teamId + " was not found.");
        }
        Team team = optionalId.get();
        if (team.getSponsors().isEmpty()) {
            throw new SponsorNotFoundException("There are no sponsors in the team.");
        }
        removeSponsorsCheck(team, sponsorsIds);
        for (Long sponsorId : sponsorsIds) {
            Optional<Sponsor> optSponsor = sponsorService.findById(sponsorId);
            if (optSponsor.isEmpty()) {
                throw new SponsorNotFoundException("Sponsor does not exist.");
            }
            Sponsor sponsor = optSponsor.get();
            team.removeSponsor(sponsor);
        }
        return toDTO(teamRepository.save(team));
    }

    public Optional<Team> findById(Long id) {
        return teamRepository.findById(id);
    }

    public List<Team> findByIds(List<Long> ids) {
        return teamRepository.findAllById(ids);
    }

    public Optional<TeamDTO> findDTOById(Long id) {
        Optional<Team> team = findById(id);
        if (team.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(toDTO(team.get()));
    }

    private TeamDTO toDTO(Team team) {
        List<Long> playersIds = null;
        List<Long> sponsorsIds = null;
        if (team.getPlayers() != null) {
            playersIds = team.getPlayers().stream().map(Player::getId).collect(Collectors.toList());
        }
        if (team.getSponsors() != null) {
            sponsorsIds = team.getSponsors().stream().map(Sponsor::getId).collect(Collectors.toList());
        }
        return new TeamDTO(
                team.getId(),
                team.getName(),
                playersIds,
                sponsorsIds);
    }
}