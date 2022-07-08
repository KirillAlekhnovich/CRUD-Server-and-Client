package cz.cvut.fit.alekhkir.tjv.semestral.services;

import cz.cvut.fit.alekhkir.tjv.semestral.dto.SponsorDTO;
import cz.cvut.fit.alekhkir.tjv.semestral.entities.Sponsor;
import cz.cvut.fit.alekhkir.tjv.semestral.repository.SponsorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SponsorServiceTests {

    @InjectMocks
    SponsorService sponsorService;

    @Mock
    SponsorRepository sponsorRepository;

    @Test
    public void testCreateSponsor() throws Exception{
        Sponsor sponsor = new Sponsor("Logitech");
        SponsorDTO sponsorDTO = new SponsorDTO(1L,"Logitech", null);
        Mockito.when(sponsorRepository.save(any(Sponsor.class))).thenReturn(sponsor);
        SponsorDTO returnedDTO = sponsorService.createSponsor(sponsorDTO);
        assertEquals(sponsorDTO.getName(),returnedDTO.getName());
        assertEquals(sponsorDTO.getTeamsIds(),returnedDTO.getTeamsIds());
        verify(sponsorRepository, times(1)).save(any(Sponsor.class));
    }

    @Test
    public void testGetAll(){
        Sponsor sponsor1 = new Sponsor("Logitech");
        Sponsor sponsor2 = new Sponsor("Vodafone");
        List<Sponsor> sponsors = List.of(sponsor1, sponsor2);
        Mockito.when(sponsorRepository.findAll()).thenReturn(sponsors);
        List<SponsorDTO> returnedSponsors = sponsorService.findAll();

        assertEquals(2, returnedSponsors.size());
        verify(sponsorRepository, times(1)).findAll();
    }

    @Test
    public void testUpdateSponsor() throws Exception{
        Sponsor sponsor = new Sponsor("Logitech");
        sponsor.setId(1L);
        SponsorDTO sponsorDTO = new SponsorDTO(2L, "Vodafone", null);

        Mockito.when(sponsorRepository.save(any(Sponsor.class))).thenReturn(sponsor);
        Mockito.when(sponsorRepository.findById(1L)).thenReturn(Optional.of(sponsor));

        SponsorDTO returnedDTO = sponsorService.updateSponsor(1L, sponsorDTO);
        assertEquals(sponsorDTO.getName(),returnedDTO.getName());
        assertEquals(sponsorDTO.getTeamsIds(),returnedDTO.getTeamsIds());
    }

    @Test
    public void deleteSponsor() throws Exception{
        Sponsor sponsor = new Sponsor("Logitech");
        sponsor.setId(1L);
        Mockito.when(sponsorRepository.findById(1L)).thenReturn(Optional.of(sponsor));
        sponsorService.deleteSponsor(sponsor.getId());
        verify(sponsorRepository, times(1)).deleteById(1L);
    }
}
