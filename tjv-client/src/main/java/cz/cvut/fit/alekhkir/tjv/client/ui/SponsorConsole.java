package cz.cvut.fit.alekhkir.tjv.client.ui;

import cz.cvut.fit.alekhkir.tjv.client.data.SponsorClient;
import cz.cvut.fit.alekhkir.tjv.client.model.SponsorDTO;
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
public class SponsorConsole {
    private final SponsorClient sponsorClient;
    private final SponsorView sponsorView;

    public SponsorConsole(SponsorClient sponsorClient, SponsorView sponsorView) {
        this.sponsorClient = sponsorClient;
        this.sponsorView = sponsorView;
    }

    @ShellMethod("Display list of all sponsors")
    public void listSponsors() {
        try {
            List<SponsorDTO> sponsors = sponsorClient.getAll();
            sponsorView.printAllSponsors(sponsors);
        } catch (WebClientException e) {
            sponsorView.printErrorGeneric(e);
        }
    }

    @ShellMethod("Create new sponsor")
    public void createSponsor(String name, @ShellOption(defaultValue = "NULL") String teams) {
        try {
            List<Long> teamsIds = convertStringToList(teams);
            SponsorDTO newSponsor = new SponsorDTO(null, name, teamsIds);
            SponsorDTO returnedSponsor = sponsorClient.createSponsor(newSponsor);
            sponsorView.printSponsor(returnedSponsor);
        } catch (WebClientException e) {
            sponsorView.printErrorCreate(e);
        }
    }

    @ShellMethod("Set current sponsor")
    public void setSponsor(Long id) {
        try {
            sponsorClient.setCurrentSponsor(id);
        } catch (WebClientException e) {
            sponsorView.printErrorSponsor(e);
        }
    }

    @ShellMethod("Unset current sponsor")
    @ShellMethodAvailability("currentSponsorAvailability")
    public void unsetSponsor() {
        sponsorClient.setCurrentSponsor(null);
    }

    public Availability currentSponsorAvailability() {
        return sponsorClient.getCurrentSponsor() == null ?
                Availability.unavailable("current sponsor needs to be set in advance.")
                : Availability.available();
    }

    @ShellMethod("Get info about selected sponsor")
    @ShellMethodAvailability("currentSponsorAvailability")
    public void readSponsor() {
        try {
            sponsorView.printSponsor(sponsorClient.getSponsor());
        } catch (WebClientException e) {
            sponsorView.printErrorSponsor(e);
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

    @ShellMethod("Update sponsors' parameters")
    @ShellMethodAvailability("currentSponsorAvailability")
    public void updateSponsor(String name, @ShellOption(defaultValue = "NULL") String teams) {
        try {
            List<Long> teamsIds = convertStringToList(teams);
            SponsorDTO returnedSponsor = sponsorClient.updateSponsor(new SponsorDTO(null, name, teamsIds));
            sponsorView.printSponsor(returnedSponsor);
        } catch (WebClientException e) {
            sponsorView.printErrorUpdate(e);
        }
    }

    @ShellMethod("Delete chosen sponsor")
    @ShellMethodAvailability("currentSponsorAvailability")
    public void deleteSponsor() {
        try {
            sponsorClient.deleteSponsor();
        } catch (WebClientException e) {
            sponsorView.printErrorSponsor(e);
        }
    }
}
