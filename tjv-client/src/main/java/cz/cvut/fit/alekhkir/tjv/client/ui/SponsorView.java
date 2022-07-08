package cz.cvut.fit.alekhkir.tjv.client.ui;

import cz.cvut.fit.alekhkir.tjv.client.model.SponsorDTO;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.shell.ExitRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

@Component
public class SponsorView {
    public void printAllSponsors(List<SponsorDTO> sponsors){
        sponsors.forEach(s -> System.out.println(s.getId() + ": " + s.getName()));
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
            System.err.println(AnsiOutput.toString(AnsiColor.RED, "Sponsor already exists" ,AnsiColor.DEFAULT));
        }
        else if (e instanceof WebClientResponseException.NotAcceptable){
            System.err.println(AnsiOutput.toString(AnsiColor.RED, "Invalid input" ,AnsiColor.DEFAULT));
        }
        else{
            printErrorGeneric(e);
        }
    }

    public void printErrorSponsor(Throwable e){
        if (e instanceof WebClientResponseException.NotFound){
            System.err.println(AnsiOutput.toString(AnsiColor.RED, "Sponsor does not exist" ,AnsiColor.DEFAULT));
        }
        else{
            printErrorGeneric(e);
        }
    }

    public void printErrorUpdate(Throwable e){
        if (e instanceof WebClientResponseException.NotFound){
            System.err.println(AnsiOutput.toString(AnsiColor.RED, "Sponsor or team does not exist" ,AnsiColor.DEFAULT));
        }
        else if (e instanceof WebClientResponseException.Conflict){
            System.err.println(AnsiOutput.toString(AnsiColor.RED, "Sponsor already exists" ,AnsiColor.DEFAULT));
        }
        else if (e instanceof WebClientResponseException.NotAcceptable){
            System.err.println(AnsiOutput.toString(AnsiColor.RED, "Invalid input" ,AnsiColor.DEFAULT));
        }
        else{
            printErrorGeneric(e);
        }
    }

    public void printSponsor(SponsorDTO returnedSponsor) {
        System.out.println("Sponsor:" + returnedSponsor.getName());
        System.out.println("Sponsored teams:" + returnedSponsor.getTeamsIds());
    }
}
