package cz.cvut.fit.alekhkir.tjv.semestral.services;

import cz.cvut.fit.alekhkir.tjv.semestral.dto.PlayerDTO;
import cz.cvut.fit.alekhkir.tjv.semestral.entities.Player;
import cz.cvut.fit.alekhkir.tjv.semestral.entities.Team;
import cz.cvut.fit.alekhkir.tjv.semestral.exceptions.InvalidInputException;
import cz.cvut.fit.alekhkir.tjv.semestral.exceptions.PlayerAlreadyExistsException;
import cz.cvut.fit.alekhkir.tjv.semestral.exceptions.PlayerNotFoundException;
import cz.cvut.fit.alekhkir.tjv.semestral.exceptions.TeamNotFoundException;
import cz.cvut.fit.alekhkir.tjv.semestral.repository.PlayerRepository;
import cz.cvut.fit.alekhkir.tjv.semestral.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PlayerService {
    private final PlayerRepository playerRepository;
    private final TeamRepository teamRepository;

    @Autowired
    public PlayerService(PlayerRepository playerRepository, TeamRepository teamRepository) {
        this.playerRepository = playerRepository;
        this.teamRepository = teamRepository;
    }

    public PlayerDTO createPlayer(PlayerDTO playerDTO) throws PlayerAlreadyExistsException, TeamNotFoundException, InvalidInputException {
        // Check if player with this nickname already exists
        Optional<Player> optionalNickname = Optional.ofNullable(playerRepository.findByNickname(playerDTO.getNickname()));
        if (optionalNickname.isPresent()) {
            throw new PlayerAlreadyExistsException("Player " + playerDTO.getNickname() + " already exists.");
        }
        // Creating player
        Player player = new Player();
        player.setName(playerDTO.getName());
        player.setSurname(playerDTO.getSurname());
        player.setNickname(playerDTO.getNickname());
        player.setAge(playerDTO.getAge());
        player.setCountryOfBirth(playerDTO.getCountryOfBirth());
        // Setting players' team
        assignPlayerToATeam(player, playerDTO.getTeamId());
        return toDTO(playerRepository.save(player));
    }

    public PlayerDTO getPlayer(Long id) throws PlayerNotFoundException {
        Optional<Player> optionalId = playerRepository.findById(id);
        if (optionalId.isEmpty()) {
            throw new PlayerNotFoundException("Player was not found");
        }
        Player player = optionalId.get();
        return toDTO(player);
    }

    public PlayerDTO updatePlayer(Long id, PlayerDTO newPlayerDTO) throws PlayerNotFoundException, PlayerAlreadyExistsException, TeamNotFoundException, InvalidInputException {
        Optional<Player> optionalId = playerRepository.findById(id);
        Optional<Player> optionalNickname = Optional.ofNullable(playerRepository.findByNickname(newPlayerDTO.getNickname()));
        // Checking if player that we want to update exists
        if (optionalId.isEmpty()) {
            throw new PlayerNotFoundException("There is no player with id " + id);
        }
        Player updatedPlayer = optionalId.get();
        // If player with this nickname already exists and current player has different nickname
        if (optionalNickname.isPresent() && !(updatedPlayer.getNickname().equals(newPlayerDTO.getNickname()))) {
            throw new PlayerAlreadyExistsException("Player with nickname " + newPlayerDTO.getNickname() + " already exists.");
        }
        updatedPlayer.setName(newPlayerDTO.getName());
        updatedPlayer.setSurname(newPlayerDTO.getSurname());
        updatedPlayer.setAge(newPlayerDTO.getAge());
        updatedPlayer.setCountryOfBirth(newPlayerDTO.getCountryOfBirth());
        updatedPlayer.setNickname(newPlayerDTO.getNickname());
        // Deleting player from previous team
        if (updatedPlayer.getTeam() != null) {
            updatedPlayer.getTeam().removePlayer(updatedPlayer);
        }
        // Setting new team for a player
        assignPlayerToATeam(updatedPlayer, newPlayerDTO.getTeamId());
        return toDTO(playerRepository.save(updatedPlayer));
    }

    public void deletePlayer(Long id) throws PlayerNotFoundException {
        Optional<Player> optionalId = findById(id);
        if (optionalId.isEmpty()) {
            throw new PlayerNotFoundException("Player with id " + id + " does not exist.");
        }
        Player player = optionalId.get();
        if (player.getTeam() != null) {
            player.getTeam().removePlayer(player);
        }
        playerRepository.deleteById(id);
    }

    public void assignPlayerToATeam(Player player, Long teamId) throws TeamNotFoundException, InvalidInputException {
        player.setTeam(null);
        // If we're just removing player from a team
        if (teamId == null) {
            return;
        }
        Optional<Team> optionalTeam = teamRepository.findById(teamId);
        if (optionalTeam.isEmpty()) {
            throw new TeamNotFoundException("Team in which player should be does not exist.");
        }
        Team team = optionalTeam.get();
        if (team.getPlayers().size() == 5) {
            throw new InvalidInputException("Player can not join this team because there are already 5 players in a team.");
        }
        player.setTeam(team);
        team.addPlayer(player);
    }

    public List<Player> findByIds(List<Long> ids) {
        return playerRepository.findAllById(ids);
    }

    public Optional<Player> findById(Long id) {
        return playerRepository.findById(id);
    }

    public Optional<PlayerDTO> findDTOById(Long id) {
        Optional<Player> player = findById(id);
        if (player.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(toDTO(player.get()));
    }

    public List<PlayerDTO> findAll() {
        return playerRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    private PlayerDTO toDTO(Player player) {
        // Sending null if player does not have a team
        Long teamId = null;
        if (player.getTeam() != null) {
            teamId = player.getTeam().getId();
        }
        return new PlayerDTO(
                player.getId(),
                player.getName(),
                player.getSurname(),
                player.getNickname(),
                player.getAge(),
                player.getCountryOfBirth(),
                teamId);
    }
}
