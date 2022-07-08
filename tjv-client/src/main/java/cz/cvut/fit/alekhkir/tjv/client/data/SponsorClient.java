package cz.cvut.fit.alekhkir.tjv.client.data;

import cz.cvut.fit.alekhkir.tjv.client.model.SponsorDTO;
import cz.cvut.fit.alekhkir.tjv.client.ui.SponsorView;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

import java.time.Duration;
import java.util.List;

@Component
public class SponsorClient {
    private final WebClient sponsorWebClient;
    private final SponsorView sponsorView;
    private Long currentSponsor;

    public SponsorClient(@Value("${backend_url}") String backendUrl, SponsorView sponsorView) {
        this.sponsorWebClient = WebClient.create(backendUrl + "/sponsors");
        this.sponsorView = sponsorView;
    }

    /* Current sponsor is the sponsor we can read/update/delete */
    public Long getCurrentSponsor() {
        return currentSponsor;
    }

    public void setCurrentSponsor(Long currentSponsor) {
        this.currentSponsor = currentSponsor;
        if (currentSponsor != null) {
            try {
                getSponsor();
            } catch (WebClientException e) {
                this.currentSponsor = null;
                throw e;
            }
        }
    }

    public SponsorDTO createSponsor(SponsorDTO sponsorDTO) {
        return sponsorWebClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(sponsorDTO)
                .retrieve()
                .bodyToMono(SponsorDTO.class)
                .block(Duration.ofSeconds(5));
    }

    public SponsorDTO getSponsor() {
        if (currentSponsor == null) {
            throw new IllegalStateException("current sponsor is not set");
        }
        return sponsorWebClient.get()
                .uri("/{id}", currentSponsor)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(SponsorDTO.class)
                .block();
    }

    public List<SponsorDTO> getAll() {
        return sponsorWebClient.get() // using get method
                .accept(MediaType.APPLICATION_JSON) // selecting media type
                .retrieve() // getting
                .bodyToFlux(SponsorDTO.class) // converting
                .collectList() // saving into list
                .block(); // stopping in the end
    }

    public SponsorDTO updateSponsor(SponsorDTO sponsorDTO) {
        if (currentSponsor == null) {
            throw new IllegalStateException("current sponsor is not set");
        }
        sponsorDTO.setId(currentSponsor);
        return sponsorWebClient.put()
                .uri("/update/{id}", currentSponsor)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(sponsorDTO)
                .retrieve()
                .bodyToMono(SponsorDTO.class)
                .block(Duration.ofSeconds(5));
    }

    public void deleteSponsor() {
        if (currentSponsor == null) {
            throw new IllegalStateException("current sponsor is not set");
        }
        sponsorWebClient.delete()
                .uri("/delete/{id}", currentSponsor)
                .retrieve()
                .toBodilessEntity()
                .subscribe(
                        x -> {setCurrentSponsor(null);},
                        e -> {sponsorView.printErrorSponsor(e);}
                );
    }
}
