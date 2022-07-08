package cz.cvut.fit.alekhkir.tjv.semestral.controllers;

import cz.cvut.fit.alekhkir.tjv.semestral.dto.SponsorDTO;
import cz.cvut.fit.alekhkir.tjv.semestral.exceptions.*;
import cz.cvut.fit.alekhkir.tjv.semestral.services.SponsorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@RequestMapping("/sponsors")
public class SponsorController {
    private final SponsorService sponsorService;

    @Autowired
    public SponsorController(SponsorService sponsorService) {
        this.sponsorService = sponsorService;
    }

    @PostMapping
    public ResponseEntity<SponsorDTO> createSponsor(@RequestBody SponsorDTO sponsorDTO) {
        try {
            SponsorDTO createdSponsor = sponsorService.createSponsor(sponsorDTO);
            return ResponseEntity
                    .created(Link.of("http://localhost:8080/sponsors/" + createdSponsor.getId()).toUri())
                    .body(createdSponsor);
        } catch (InvalidInputException e) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
        } catch (SponsorAlreadyExistsException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        } catch (TeamNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity getSponsor(@PathVariable Long id) {
        try {
            Optional<SponsorDTO> optionalSponsor = sponsorService.findDTOById(id);
            if (optionalSponsor.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok(optionalSponsor);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity getAll() {
        try {
            return ResponseEntity.ok(sponsorService.findAll());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<SponsorDTO> updateSponsor(@PathVariable Long id, @RequestBody SponsorDTO newSponsorDTO) {
        try {
            SponsorDTO updatedSponsor = sponsorService.updateSponsor(id, newSponsorDTO);
            return ResponseEntity
                    .created(Link.of("http://localhost:8080/sponsors/" + updatedSponsor.getId()).toUri())
                    .body(updatedSponsor);
        } catch (InvalidInputException e) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
        } catch (SponsorAlreadyExistsException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        } catch (SponsorNotFoundException | TeamNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteSponsor(@PathVariable Long id) {
        try {
            sponsorService.deleteSponsor(id);
            return ResponseEntity.ok("Sponsor with id " + id + " was deleted.");
        } catch (SponsorNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }
}