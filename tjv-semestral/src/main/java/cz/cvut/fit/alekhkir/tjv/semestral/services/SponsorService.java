package cz.cvut.fit.alekhkir.tjv.semestral.services;

import cz.cvut.fit.alekhkir.tjv.semestral.dto.SponsorDTO;
import cz.cvut.fit.alekhkir.tjv.semestral.entities.Sponsor;
import cz.cvut.fit.alekhkir.tjv.semestral.entities.Team;
import cz.cvut.fit.alekhkir.tjv.semestral.exceptions.InvalidInputException;
import cz.cvut.fit.alekhkir.tjv.semestral.exceptions.SponsorAlreadyExistsException;
import cz.cvut.fit.alekhkir.tjv.semestral.exceptions.SponsorNotFoundException;
import cz.cvut.fit.alekhkir.tjv.semestral.exceptions.TeamNotFoundException;
import cz.cvut.fit.alekhkir.tjv.semestral.repository.SponsorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SponsorService {
    private final SponsorRepository sponsorRepository;
    private final TeamService teamService;

    @Autowired
    public SponsorService(SponsorRepository sponsorRepository, @Lazy TeamService teamService) {
        // We're preventing cycle with @Lazy, bean will be fully created only when it's first needed.
        this.sponsorRepository = sponsorRepository;
        this.teamService = teamService;
    }

    private void checkIfTeamsAreValidOrThrowException(SponsorDTO sponsorDTO, List<Team> teams) throws InvalidInputException, TeamNotFoundException {
        List<Long> teamsIds = sponsorDTO.getTeamsIds();
        if (teamService.listHasDuplicates(teamsIds)) {
            throw new InvalidInputException("Input is invalid. Duplicates detected.");
        }
        if (teams.size() != sponsorDTO.getTeamsIds().size()) {
            throw new TeamNotFoundException("Some of the teams do not exist.");
        }
    }

    public SponsorDTO createSponsor(SponsorDTO sponsorDTO) throws SponsorAlreadyExistsException, TeamNotFoundException, InvalidInputException {
        Optional<Sponsor> optionalName = Optional.ofNullable(sponsorRepository.findByName(sponsorDTO.getName()));
        if (optionalName.isPresent()) {
            throw new SponsorAlreadyExistsException("Sponsor with name " + sponsorDTO.getName() + " already exists.");
        }
        Sponsor sponsor = new Sponsor();
        sponsor.setName(sponsorDTO.getName());
        List<Team> teams = new ArrayList<>();
        // Assigning teams to this sponsor
        if (sponsorDTO.getTeamsIds() != null) {
            teams = teamService.findByIds(sponsorDTO.getTeamsIds());
            checkIfTeamsAreValidOrThrowException(sponsorDTO, teams);
        }
        sponsor.setTeams(teams);
        return toDTO(sponsorRepository.save(sponsor));
    }

    public SponsorDTO updateSponsor(Long id, SponsorDTO sponsorDTO) throws SponsorNotFoundException, SponsorAlreadyExistsException, InvalidInputException, TeamNotFoundException {
        Optional<Sponsor> optionalId = sponsorRepository.findById(id);
        Optional<Sponsor> optionalName = Optional.ofNullable(sponsorRepository.findByName(sponsorDTO.getName()));
        if (optionalId.isEmpty()) {
            throw new SponsorNotFoundException("Sponsor with id " + id + " was not found.");
        }
        Sponsor updatedSponsor = optionalId.get();
        // If sponsor with this name already exists and current sponsor has different name
        if (optionalName.isPresent() && !(updatedSponsor.getName().equals(sponsorDTO.getName()))) {
            throw new SponsorAlreadyExistsException("Sponsor with name " + sponsorDTO.getName() + " already exists.");
        }
        updatedSponsor.setName(sponsorDTO.getName());
        updatedSponsor.setTeams(null);
        // Assigning teams to this sponsor
        if (sponsorDTO.getTeamsIds() != null) {
            List<Team> newSponsoredTeams = teamService.findByIds(sponsorDTO.getTeamsIds());
            checkIfTeamsAreValidOrThrowException(sponsorDTO, newSponsoredTeams);
            updatedSponsor.setTeams(newSponsoredTeams);
        }
        return toDTO(sponsorRepository.save(updatedSponsor));
    }

    public SponsorDTO getSponsor(Long id) throws SponsorNotFoundException {
        Optional<Sponsor> optionalId = sponsorRepository.findById(id);
        if (optionalId.isEmpty()) {
            throw new SponsorNotFoundException("Sponsor with id " + id + " was not found");
        }
        Sponsor sponsor = optionalId.get();
        return toDTO(sponsor);
    }

    public void deleteSponsor(Long id) throws SponsorNotFoundException {
        Optional<Sponsor> optionalId = sponsorRepository.findById(id);
        if (optionalId.isEmpty())
            throw new SponsorNotFoundException("Sponsor with id " + id + " does not exist.");
        Sponsor sponsor = optionalId.get();
        // Removing this sponsor from all teams before deleting
        if (sponsor.getTeams() != null) {
            for (Team team : sponsor.getTeams()) {
                team.removeSponsor(sponsor);
            }
        }
        sponsorRepository.deleteById(id);
    }

    public List<Sponsor> findByIds(List<Long> ids) {
        return sponsorRepository.findAllById(ids);
    }

    public Optional<Sponsor> findById(Long id) {
        return sponsorRepository.findById(id);
    }

    public Optional<SponsorDTO> findDTOById(Long id) {
        Optional<Sponsor> sponsor = findById(id);
        if (sponsor.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(toDTO(sponsor.get()));
    }

    public List<SponsorDTO> findAll() {
        return sponsorRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    private SponsorDTO toDTO(Sponsor sponsor) {
        List<Long> teamsIds = null;
        if (sponsor.getTeams() != null) {
            teamsIds = sponsor.getTeams().stream().map(Team::getId).collect(Collectors.toList());
        }
        return new SponsorDTO(
                sponsor.getId(),
                sponsor.getName(),
                teamsIds);
    }
}
