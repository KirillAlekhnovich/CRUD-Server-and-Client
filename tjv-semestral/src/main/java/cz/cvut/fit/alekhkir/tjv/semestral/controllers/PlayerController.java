package cz.cvut.fit.alekhkir.tjv.semestral.controllers;

import cz.cvut.fit.alekhkir.tjv.semestral.dto.PlayerDTO;
import cz.cvut.fit.alekhkir.tjv.semestral.exceptions.InvalidInputException;
import cz.cvut.fit.alekhkir.tjv.semestral.exceptions.PlayerAlreadyExistsException;
import cz.cvut.fit.alekhkir.tjv.semestral.exceptions.PlayerNotFoundException;
import cz.cvut.fit.alekhkir.tjv.semestral.exceptions.TeamNotFoundException;
import cz.cvut.fit.alekhkir.tjv.semestral.services.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@RequestMapping("/players")
public class PlayerController {
    private final PlayerService playerService;

    @Autowired
    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @PostMapping
    public ResponseEntity<PlayerDTO> createPlayer(@RequestBody PlayerDTO playerDTO) {
        try {
            PlayerDTO createdPlayer = playerService.createPlayer(playerDTO);
            return ResponseEntity
                    .created(Link.of("http://localhost:8080/players/" + createdPlayer.getId()).toUri())
                    .body(createdPlayer);
        } catch (InvalidInputException e) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
        } catch (PlayerAlreadyExistsException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        } catch (TeamNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<PlayerDTO>> getPlayer(@PathVariable Long id) {
        try {
            Optional<PlayerDTO> optionalPlayer = playerService.findDTOById(id);
            if (optionalPlayer.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok(optionalPlayer);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping
    public ResponseEntity getAll() {
        try {
            return ResponseEntity.ok(playerService.findAll());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<PlayerDTO> updatePlayer(@PathVariable Long id, @RequestBody PlayerDTO newPlayerDTO) {
        try {
            PlayerDTO updatedPlayer = playerService.updatePlayer(id, newPlayerDTO);
            return ResponseEntity
                    .created(Link.of("http://localhost:8080/players/" + updatedPlayer.getId()).toUri())
                    .body(updatedPlayer);
        } catch (InvalidInputException e) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
        } catch (PlayerAlreadyExistsException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        } catch (PlayerNotFoundException | TeamNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity deletePlayer(@PathVariable Long id) {
        try {
            playerService.deletePlayer(id);
            return ResponseEntity.ok("Player with id " + id + " was deleted.");
        } catch (PlayerNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }
}
