package cz.cvut.fit.alekhkir.tjv.client.data;

import cz.cvut.fit.alekhkir.tjv.client.model.TeamDTO;
import cz.cvut.fit.alekhkir.tjv.client.ui.TeamView;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

import java.time.Duration;
import java.util.List;

@Component
public class TeamClient {
    private final WebClient teamWebClient;
    private final TeamView teamView;
    private Long currentTeam;

    public TeamClient(@Value("${backend_url}") String backendUrl, TeamView teamView) {
        this.teamWebClient = WebClient.create(backendUrl + "/teams");
        this.teamView = teamView;
    }

    /* Current team is the team we can read/update/delete */
    public Long getCurrentTeam() {
        return currentTeam;
    }

    public void setCurrentTeam(Long currentTeam) {
        this.currentTeam = currentTeam;
        if (currentTeam != null) {
            try {
                getTeam();
            } catch (WebClientException e) {
                this.currentTeam = null;
                throw e;
            }
        }
    }

    public TeamDTO createTeam(TeamDTO teamDTO) {
        return teamWebClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(teamDTO)
                .retrieve()
                .bodyToMono(TeamDTO.class)
                .block(Duration.ofSeconds(5));
    }

    public TeamDTO getTeam() {
        if (currentTeam == null) {
            throw new IllegalStateException("current team is not set");
        }
        return teamWebClient.get()
                .uri("/{id}", currentTeam)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(TeamDTO.class)
                .block();
    }

    public List<TeamDTO> getAll() {
        return teamWebClient.get() // using get method
                .accept(MediaType.APPLICATION_JSON) // selecting media type
                .retrieve() // getting
                .bodyToFlux(TeamDTO.class) // converting
                .collectList() // saving into list
                .block(); // stopping in the end
    }

    public TeamDTO updateTeam(TeamDTO teamDTO) {
        if (currentTeam == null) {
            throw new IllegalStateException("current team is not set");
        }
        teamDTO.setId(currentTeam);
        return teamWebClient.put()
                .uri("/update/{id}", currentTeam)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(teamDTO)
                .retrieve()
                .bodyToMono(TeamDTO.class)
                .block(Duration.ofSeconds(5));
    }

    public void deleteTeam() {
        if (currentTeam == null) {
            throw new IllegalStateException("current team is not set");
        }
        teamWebClient.delete()
                .uri("/delete/{id}", currentTeam)
                .retrieve()
                .toBodilessEntity()
                .subscribe(
                        x -> {setCurrentTeam(null);},
                        e -> {teamView.printErrorTeam(e);}
                );
    }

    public TeamDTO addPlayers(List<Long> playersIds){
        if (currentTeam == null) {
            throw new IllegalStateException("current team not set");
        }
        return teamWebClient.post()
                .uri("/{id}/add_players", currentTeam)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(playersIds)
                .retrieve()
                .bodyToMono(TeamDTO.class)
                .block(Duration.ofSeconds(5));
    }

    public TeamDTO addSponsors(List<Long> sponsorsIds){
        if (currentTeam == null) {
            throw new IllegalStateException("current team is not set");
        }
        return teamWebClient.post()
                .uri("/{id}/add_sponsors", currentTeam)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(sponsorsIds)
                .retrieve()
                .bodyToMono(TeamDTO.class)
                .block(Duration.ofSeconds(5));
    }

    public TeamDTO removePlayers(List<Long> playersIds){
        if (currentTeam == null) {
            throw new IllegalStateException("current team is not set");
        }
        return teamWebClient.post()
                .uri("/{id}/remove_players", currentTeam)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(playersIds)
                .retrieve()
                .bodyToMono(TeamDTO.class)
                .block(Duration.ofSeconds(5));
    }

    public TeamDTO removeSponsors(List<Long> sponsorsIds){
        if (currentTeam == null) {
            throw new IllegalStateException("current team is not set");
        }
        return teamWebClient.post()
                .uri("/{id}/remove_sponsors", currentTeam)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(sponsorsIds)
                .retrieve()
                .bodyToMono(TeamDTO.class)
                .block(Duration.ofSeconds(5));
    }
}
