package cz.cvut.fit.alekhkir.tjv.semestral.controllers;

import cz.cvut.fit.alekhkir.tjv.semestral.dto.TeamDTO;
import cz.cvut.fit.alekhkir.tjv.semestral.exceptions.*;
import cz.cvut.fit.alekhkir.tjv.semestral.services.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/teams")
public class TeamController {
    private final TeamService teamService;

    @Autowired
    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @PostMapping
    public ResponseEntity<TeamDTO> createTeam(@RequestBody TeamDTO teamDTO) {
        try {
            TeamDTO createdTeam = teamService.createTeam(teamDTO);
            return ResponseEntity
                    .created(Link.of("http://localhost:8080/teams/" + createdTeam.getId()).toUri())
                    .body(createdTeam);
        } catch (InvalidInputException e) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
        } catch (TeamAlreadyExistsException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        } catch (PlayerNotFoundException | SponsorNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity getTeam(@PathVariable Long id) {
        try {
            Optional<TeamDTO> optionalTeam = teamService.findDTOById(id);
            if (optionalTeam.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok(optionalTeam);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity getAll() {
        try {
            return ResponseEntity.ok(teamService.findAll());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<TeamDTO> updateTeam(@PathVariable Long id, @RequestBody TeamDTO newTeamDTO) {
        try {
            TeamDTO updatedTeam = teamService.updateTeam(id, newTeamDTO);
            return ResponseEntity
                    .created(Link.of("http://localhost:8080/teams/" + updatedTeam.getId()).toUri())
                    .body(updatedTeam);
        } catch (InvalidInputException e) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
        } catch (TeamAlreadyExistsException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        } catch (TeamNotFoundException | PlayerNotFoundException | SponsorNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteTeam(@PathVariable Long id) {
        try {
            teamService.deleteTeam(id);
            return ResponseEntity.ok("Team with id " + id + " was deleted.");
        } catch (TeamNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @PostMapping("/{id}/add_players")
    public ResponseEntity<TeamDTO> addPlayers(@PathVariable Long id, @RequestBody List<Long> playersIds) {
        try {
            TeamDTO updatedTeam = teamService.addPlayers(id, playersIds);
            return ResponseEntity
                    .created(Link.of("http://localhost:8080/teams/" + updatedTeam.getId()).toUri())
                    .body(updatedTeam);
        } catch (InvalidInputException e) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
        } catch (PlayerCantBeAddedException | PlayerAlreadyExistsException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        } catch (TeamNotFoundException | PlayerNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/{id}/add_sponsors")
    public ResponseEntity<TeamDTO> addSponsors(@PathVariable Long id, @RequestBody List<Long> sponsorIds) {
        try {
            TeamDTO updatedTeam = teamService.addSponsors(id, sponsorIds);
            return ResponseEntity
                    .created(Link.of("http://localhost:8080/teams/" + updatedTeam.getId()).toUri())
                    .body(updatedTeam);
        } catch (TeamNotFoundException | SponsorNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } catch (InvalidInputException e) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
        } catch (SponsorAlreadyExistsException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/{id}/remove_players")
    public ResponseEntity<TeamDTO> removePlayers(@PathVariable Long id, @RequestBody List<Long> playersIds) {
        try {
            TeamDTO updatedTeam = teamService.removePlayers(id, playersIds);
            return ResponseEntity
                    .created(Link.of("http://localhost:8080/teams/" + updatedTeam.getId()).toUri())
                    .body(updatedTeam);
        } catch (PlayerNotFoundException | TeamNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } catch (InvalidInputException e) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/{id}/remove_sponsors")
    public ResponseEntity<TeamDTO> removeSponsors(@PathVariable Long id, @RequestBody List<Long> sponsorIds) {
        try {
            TeamDTO updatedTeam = teamService.removeSponsors(id, sponsorIds);
            return ResponseEntity
                    .created(Link.of("http://localhost:8080/teams/" + updatedTeam.getId()).toUri())
                    .body(updatedTeam);
        } catch (SponsorNotFoundException | TeamNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } catch (InvalidInputException e) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }
}
