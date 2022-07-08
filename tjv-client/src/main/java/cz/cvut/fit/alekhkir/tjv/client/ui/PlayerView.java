package cz.cvut.fit.alekhkir.tjv.client.ui;

import cz.cvut.fit.alekhkir.tjv.client.model.PlayerDTO;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.shell.ExitRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

@Component
public class PlayerView {
    public void printAllPlayers(List<PlayerDTO> players){
        players.forEach(p -> System.out.println(p.getId() + ": " + p.getName() + " " + p.getSurname() + " (" + p.getNickname() + ")"));
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
            System.err.println(AnsiOutput.toString(AnsiColor.RED, "Team was not found" ,AnsiColor.DEFAULT));
        }
        else if (e instanceof WebClientResponseException.Conflict){
            System.err.println(AnsiOutput.toString(AnsiColor.RED, "Player already exists" ,AnsiColor.DEFAULT));
        }
        else if (e instanceof WebClientResponseException.NotAcceptable){
            System.err.println(AnsiOutput.toString(AnsiColor.RED, "Input is invalid" ,AnsiColor.DEFAULT));
        }
        else{
            printErrorGeneric(e);
        }
    }

    public void printErrorPlayer(Throwable e){
        if (e instanceof WebClientResponseException.NotFound){
            System.err.println(AnsiOutput.toString(AnsiColor.RED, "Player does not exist" ,AnsiColor.DEFAULT));
        }
        else{
            printErrorGeneric(e);
        }
    }

    public void printErrorUpdate(Throwable e){
        if (e instanceof WebClientResponseException.NotFound){
            System.err.println(AnsiOutput.toString(AnsiColor.RED, "Player or team does not exist" ,AnsiColor.DEFAULT));
        }
        else if (e instanceof WebClientResponseException.Conflict){
            System.err.println(AnsiOutput.toString(AnsiColor.RED, "Player already exists" ,AnsiColor.DEFAULT));
        }
        else if (e instanceof WebClientResponseException.NotAcceptable){
            System.err.println(AnsiOutput.toString(AnsiColor.RED, "Input is invalid" ,AnsiColor.DEFAULT));
        }
        else{
            printErrorGeneric(e);
        }
    }

    public void printPlayer(PlayerDTO returnedPlayer) {
        System.out.println(returnedPlayer.getName() + " " + returnedPlayer.getSurname());
        System.out.println("Nickname:" + returnedPlayer.getNickname());
        System.out.println("Age:" + returnedPlayer.getAge() + ",country:" + returnedPlayer.getCountryOfBirth());
        if (returnedPlayer.getTeamId() != null){
            System.out.println("TeamID:" + returnedPlayer.getTeamId());
        }
    }
}
