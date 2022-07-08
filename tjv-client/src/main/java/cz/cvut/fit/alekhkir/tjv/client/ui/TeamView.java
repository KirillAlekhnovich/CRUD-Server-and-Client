package cz.cvut.fit.alekhkir.tjv.client.ui;

import cz.cvut.fit.alekhkir.tjv.client.model.TeamDTO;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.shell.ExitRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

@Component
public class TeamView {
    public void printAllTeams(List<TeamDTO> teams){
        teams.forEach(t -> System.out.println(t.getId() + ": " + t.getName()));
    }

    public void printErrorGeneric(Throwable e){
        if (e instanceof WebClientRequestException){
            System.err.println("Cannot connect to API");
            throw new ExitRequest();
        } else if (e instanceof WebClientResponseException.InternalServerError){
            System.err.println("Unknown technical server error");
        } else {
            System.err.println("Unknown error: " + e.toString());
        }
    }

    public void printErrorCreate(Throwable e){
        if (e instanceof WebClientResponseException.NotFound){
            System.err.println(AnsiOutput.toString(AnsiColor.RED, "One of players or sponsors does not exist" ,AnsiColor.DEFAULT));
        }
        else if (e instanceof WebClientResponseException.Conflict){
            System.err.println(AnsiOutput.toString(AnsiColor.RED, "Team already exists" ,AnsiColor.DEFAULT));
        }
        else if (e instanceof WebClientResponseException.NotAcceptable){
            System.err.println(AnsiOutput.toString(AnsiColor.RED, "Invalid input" ,AnsiColor.DEFAULT));
        }
        else{
            printErrorGeneric(e);
        }
    }

    public void printErrorTeam(Throwable e){
        if (e instanceof WebClientResponseException.NotFound){
            System.err.println(AnsiOutput.toString(AnsiColor.RED, "Team does not exist" ,AnsiColor.DEFAULT));
        }
        else{
            printErrorGeneric(e);
        }
    }

    public void printErrorAddPlayers(Throwable e){
        if (e instanceof  WebClientResponseException.NotFound){
            System.err.println(AnsiOutput.toString(AnsiColor.RED, "Team or player does not exist" ,AnsiColor.DEFAULT));
        }
        else if (e instanceof  WebClientResponseException.NotAcceptable){
            System.err.println(AnsiOutput.toString(AnsiColor.RED, "Invalid input" ,AnsiColor.DEFAULT));
        }
        else if (e instanceof  WebClientResponseException.Conflict){
            System.err.println(AnsiOutput.toString(AnsiColor.RED, "Player either in a team already or can't be added" ,AnsiColor.DEFAULT));
        }
        else{
            printErrorGeneric(e);
        }
    }

    public void printErrorAddSponsors(Throwable e){
        if (e instanceof  WebClientResponseException.NotFound){
            System.err.println(AnsiOutput.toString(AnsiColor.RED, "One of sponsors does not exist" ,AnsiColor.DEFAULT));
        }
        else if (e instanceof  WebClientResponseException.NotAcceptable){
            System.err.println(AnsiOutput.toString(AnsiColor.RED, "Invalid input" ,AnsiColor.DEFAULT));
        }
        else if (e instanceof  WebClientResponseException.Conflict){
            System.err.println(AnsiOutput.toString(AnsiColor.RED, "Sponsor is in a team already" ,AnsiColor.DEFAULT));
        }
        else{
            printErrorGeneric(e);
        }
    }

    public void printErrorUpdate(Throwable e){
        if (e instanceof WebClientResponseException.NotFound){
            System.err.println(AnsiOutput.toString(AnsiColor.RED, "Team or sponsor or player does not exist" ,AnsiColor.DEFAULT));
        }
        else if (e instanceof WebClientResponseException.Conflict){
            System.err.println(AnsiOutput.toString(AnsiColor.RED, "Team already exists" ,AnsiColor.DEFAULT));
        }
        else if (e instanceof WebClientResponseException.NotAcceptable){
            System.err.println(AnsiOutput.toString(AnsiColor.RED, "Invalid input" ,AnsiColor.DEFAULT));
        }
        else{
            printErrorGeneric(e);
        }
    }

    public void printTeam(TeamDTO returnedTeam) {
        System.out.println("Team:" + returnedTeam.getName());
        System.out.println("Players:" + returnedTeam.getPlayersIds());
        System.out.println("Sponsors:" + returnedTeam.getSponsorsIds());
    }
}
