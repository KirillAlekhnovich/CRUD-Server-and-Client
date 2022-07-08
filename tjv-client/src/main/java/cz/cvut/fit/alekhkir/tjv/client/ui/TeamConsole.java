package cz.cvut.fit.alekhkir.tjv.client.ui;

import cz.cvut.fit.alekhkir.tjv.client.data.TeamClient;
import cz.cvut.fit.alekhkir.tjv.client.model.TeamDTO;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;
import org.springframework.web.reactive.function.client.WebClientException;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ShellComponent
public class TeamConsole {
    private final TeamClient teamClient;
    private final TeamView teamView;

    public TeamConsole(TeamClient teamClient, TeamView teamView) {
        this.teamClient = teamClient;
        this.teamView = teamView;
    }

    @ShellMethod("Display list of all teams")
    public void listTeams() {
        try {
            List<TeamDTO> teams = teamClient.getAll();
            teamView.printAllTeams(teams);
        } catch (WebClientException e) {
            teamView.printErrorGeneric(e);
        }
    }

    @ShellMethod("Create new team")
    public void createTeam(String name, @ShellOption(defaultValue = "NULL") String players, @ShellOption(defaultValue = "NULL") String sponsors) {
        try {
            List<Long> playersIds = convertStringToList(players);
            List<Long> sponsorsIds = convertStringToList(sponsors);
            TeamDTO newTeam = new TeamDTO(null, name, playersIds, sponsorsIds);
            TeamDTO returnedTeam = teamClient.createTeam(newTeam);
            teamView.printTeam(returnedTeam);
        } catch (WebClientException e) {
            teamView.printErrorCreate(e);
        }
    }

    @ShellMethod("Set current team")
    public void setTeam(Long id) {
        try {
            teamClient.setCurrentTeam(id);
        } catch (WebClientException e) {
            teamView.printErrorTeam(e);
        }
    }

    @ShellMethod("Unset current team")
    @ShellMethodAvailability("currentTeamAvailability")
    public void unsetTeam() {
        teamClient.setCurrentTeam(null);
    }

    public Availability currentTeamAvailability() {
        return teamClient.getCurrentTeam() == null ?
                Availability.unavailable("current team needs to be set in advance.")
                : Availability.available();
    }

    @ShellMethod("Get info about selected team")
    @ShellMethodAvailability("currentTeamAvailability")
    public void readTeam() {
        try {
            teamView.printTeam(teamClient.getTeam());
        } catch (WebClientException e) {
            teamView.printErrorTeam(e);
        }
    }

    public List<Long> convertStringToList(String str){
        List<Long> list = null;
        if (!str.equals("NULL")){
            list = Stream.of(str.split(","))
                    .map(String::trim)
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
        }
        return list;
    }

    @ShellMethod("Update teams' parameters")
    @ShellMethodAvailability("currentTeamAvailability")
    public void updateTeam(String name, @ShellOption(defaultValue = "NULL") String players, @ShellOption(defaultValue = "NULL") String sponsors) {
        try {
            List<Long> playersIds = convertStringToList(players);
            List<Long> sponsorsIds = convertStringToList(sponsors);
            TeamDTO returnedTeam = teamClient.updateTeam(new TeamDTO(null, name, playersIds, sponsorsIds));
            teamView.printTeam(returnedTeam);
        } catch (WebClientException e) {
            teamView.printErrorUpdate(e);
        }
    }

    @ShellMethod("Delete chosen team")
    @ShellMethodAvailability("currentTeamAvailability")
    public void deleteTeam() {
        try {
            teamClient.deleteTeam();
        } catch (WebClientException e) {
            teamView.printErrorTeam(e);
        }
    }

    @ShellMethod("Add new players to team")
    @ShellMethodAvailability("currentTeamAvailability")
    public void addPlayers(List<Long> playersIds){
        try {
            TeamDTO returnedTeam = teamClient.addPlayers(playersIds);
            teamView.printTeam(returnedTeam);
        } catch (WebClientException e) {
            teamView.printErrorAddPlayers(e);
        }
    }

    @ShellMethod("Add new sponsors to team")
    @ShellMethodAvailability("currentTeamAvailability")
    public void addSponsors(List<Long> sponsorsIds){
        try {
            TeamDTO returnedTeam = teamClient.addSponsors(sponsorsIds);
            teamView.printTeam(returnedTeam);
        } catch (WebClientException e) {
            teamView.printErrorAddSponsors(e);
        }
    }

    @ShellMethod("Remove players from a team")
    @ShellMethodAvailability("currentTeamAvailability")
    public void removePlayers(List<Long> playersIds){
        try {
            TeamDTO returnedTeam = teamClient.removePlayers(playersIds);
            teamView.printTeam(returnedTeam);
        } catch (WebClientException e) {
            teamView.printErrorAddPlayers(e);
        }
    }

    @ShellMethod("Remove sponsors from a team")
    @ShellMethodAvailability("currentTeamAvailability")
    public void removeSponsors(List<Long> sponsorsIds){
        try {
            TeamDTO returnedTeam = teamClient.removeSponsors(sponsorsIds);
            teamView.printTeam(returnedTeam);
        } catch (WebClientException e) {
            teamView.printErrorAddSponsors(e);
        }
    }
}
