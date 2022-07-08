package cz.cvut.fit.alekhkir.tjv.client.ui;

import cz.cvut.fit.alekhkir.tjv.client.data.PlayerClient;
import cz.cvut.fit.alekhkir.tjv.client.model.PlayerDTO;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;
import org.springframework.web.reactive.function.client.WebClientException;

import java.util.List;

@ShellComponent
public class PlayerConsole {
    private final PlayerClient playerClient;
    private final PlayerView playerView;

    public PlayerConsole(PlayerClient playerClient, PlayerView playerView) {
        this.playerClient = playerClient;
        this.playerView = playerView;
    }

    @ShellMethod("Display list of all players")
    public void listPlayers() {
        try {
            List<PlayerDTO> players = playerClient.getAll();
            playerView.printAllPlayers(players);
        } catch (WebClientException e) {
            playerView.printErrorGeneric(e);
        }
    }

    @ShellMethod("Create new player")
    public void createPlayer(String name, String surname, String nickname, Integer age, String countryOfBirth, @ShellOption(defaultValue = "NULL") String team) {
        try {
            Long teamId = convertStringToLong(team);
            PlayerDTO newPlayer = new PlayerDTO(null, name, surname, nickname, age, countryOfBirth, teamId);
            PlayerDTO returnedPlayer = playerClient.createPlayer(newPlayer);
            playerView.printPlayer(returnedPlayer);
        } catch (WebClientException e) {
            playerView.printErrorCreate(e);
        }
    }

    @ShellMethod("Set current player")
    public void setPlayer(Long id) {
        try {
            playerClient.setCurrentPlayer(id);
        } catch (WebClientException e) {
            playerView.printErrorPlayer(e);
        }
    }

    @ShellMethod("Unset current player")
    @ShellMethodAvailability("currentPlayerAvailability")
    public void unsetPlayer() {
        playerClient.setCurrentPlayer(null);
    }

    public Availability currentPlayerAvailability() {
        return playerClient.getCurrentPlayer() == null ?
                Availability.unavailable("current player needs to be set in advance.")
                : Availability.available();
    }

    @ShellMethod("Get info about selected player")
    @ShellMethodAvailability("currentPlayerAvailability")
    public void readPlayer() {
        try {
            playerView.printPlayer(playerClient.getPlayer());
        } catch (WebClientException e) {
            playerView.printErrorPlayer(e);
        }
    }

    /* Used to check if there was a team in the input of create and update methods, returns needed long value */
    public Long convertStringToLong(String str){
        Long teamId = null;
        if (!str.equals("NULL")){
            teamId = Long.parseLong(str);
        }
        return teamId;
    }

    @ShellMethod("Update players' parameters")
    @ShellMethodAvailability("currentPlayerAvailability")
    public void updatePlayer(String name, String surname, String nickname, Integer age, String countryOfBirth, @ShellOption(defaultValue = "NULL") String team) {
        try {
            Long teamId = convertStringToLong(team);
            PlayerDTO returnedPlayer = playerClient.updatePlayer(new PlayerDTO(null, name, surname, nickname, age, countryOfBirth, teamId));
            playerView.printPlayer(returnedPlayer);
        } catch (WebClientException e) {
            playerView.printErrorUpdate(e);
        }
    }


    @ShellMethod("Delete chosen player")
    @ShellMethodAvailability("currentPlayerAvailability")
    public void deletePlayer() {
        try {
            playerClient.deletePlayer();
        } catch (WebClientException e) {
            playerView.printErrorPlayer(e);
        }
    }
}

