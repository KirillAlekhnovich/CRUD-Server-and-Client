package cz.cvut.fit.alekhkir.tjv.client.data;

import cz.cvut.fit.alekhkir.tjv.client.model.PlayerDTO;
import cz.cvut.fit.alekhkir.tjv.client.ui.PlayerView;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

import java.time.Duration;
import java.util.List;

@Component
public class PlayerClient {
    private final WebClient playerWebClient;
    private final PlayerView playerView;
    private Long currentPlayer;

    public PlayerClient(@Value("${backend_url}") String backendUrl, PlayerView playerView) {
        this.playerWebClient = WebClient.create(backendUrl + "/players");
        this.playerView = playerView;
    }

    /* Current player is the player we can read/update/delete */
    public Long getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Long currentPlayer) {
        this.currentPlayer = currentPlayer;
        if (currentPlayer != null) {
            try {
                getPlayer();
            } catch (WebClientException e) {
                this.currentPlayer = null;
                throw e;
            }
        }
    }

    public PlayerDTO createPlayer(PlayerDTO playerDTO) {
        return playerWebClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(playerDTO)
                .retrieve()
                .bodyToMono(PlayerDTO.class)
                .block(Duration.ofSeconds(5));
    }

    public PlayerDTO getPlayer() {
        if (currentPlayer == null) {
            throw new IllegalStateException("current player is not set");
        }
        return playerWebClient.get()
                .uri("/{id}", currentPlayer)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(PlayerDTO.class)
                .block();
    }

    public List<PlayerDTO> getAll() {
        return playerWebClient.get() // using get method
                .accept(MediaType.APPLICATION_JSON) // selecting media type
                .retrieve() // getting
                .bodyToFlux(PlayerDTO.class) // converting
                .collectList() // saving into list
                .block(); // stopping in the end
    }

    public PlayerDTO updatePlayer(PlayerDTO playerDTO) {
        if (currentPlayer == null) {
            throw new IllegalStateException("current player is not set");
        }
        playerDTO.setId(currentPlayer);
        return playerWebClient.put()
                .uri("/update/{id}", currentPlayer)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(playerDTO)
                .retrieve()
                .bodyToMono(PlayerDTO.class)
                .block(Duration.ofSeconds(5));
    }

    public void deletePlayer() {
        if (currentPlayer == null) {
            throw new IllegalStateException("current player is not set");
        }
        playerWebClient.delete()
                .uri("/delete/{id}", currentPlayer)
                .retrieve()
                .toBodilessEntity()
                .subscribe(
                        x -> {setCurrentPlayer(null);},
                        e -> {playerView.printErrorPlayer(e);}
                        );
    }
}
