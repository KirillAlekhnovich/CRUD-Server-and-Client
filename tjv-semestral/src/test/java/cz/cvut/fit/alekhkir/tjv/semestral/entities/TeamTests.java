package cz.cvut.fit.alekhkir.tjv.semestral.entities;

import cz.cvut.fit.alekhkir.tjv.semestral.repository.PlayerRepository;
import cz.cvut.fit.alekhkir.tjv.semestral.repository.SponsorRepository;
import cz.cvut.fit.alekhkir.tjv.semestral.repository.TeamRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class TeamTests {
    @Autowired
    private final TeamRepository teamRepository;

    @Autowired
    private final PlayerRepository playerRepository;

    @Autowired
    private final SponsorRepository sponsorRepository;

    @Autowired
    public TeamTests(TeamRepository teamRepository, PlayerRepository playerRepository, SponsorRepository sponsorRepository) {
        this.teamRepository = teamRepository;
        this.playerRepository = playerRepository;
        this.sponsorRepository = sponsorRepository;
    }

    @Test
    void testAddPlayers() {
        Team team = new Team("G2 Esports", new ArrayList<Player>(), null);
        Player player = new Player("Martin", "Larsson", "Rekkles", 24, "Sweden", null);

        assertEquals(0, team.getPlayers().size());
        team.addPlayer(player);
        assertEquals(1, team.getPlayers().size());
    }

    @Test
    void testRemovePlayers() {
        Player player1 = new Player("Martin", "Larsson", "Rekkles", 24, "Sweden", null);
        Player player2 = new Player("Rasmus", "Winther", "Caps", 21, "Denmark", null);
        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        Team team = new Team("G2 Esports", players, null);
        assertEquals(2, team.getPlayers().size());
        team.removePlayer(player1);
        assertEquals(1, team.getPlayers().size());
    }

    @Test
    void testAddSponsors() {
        Team team = new Team("G2 Esports", null, new ArrayList<Sponsor>());
        Sponsor sponsor = new Sponsor("Logitech");

        assertEquals(0, team.getSponsors().size());
        team.addSponsor(sponsor);
        assertEquals(1, team.getSponsors().size());
    }

    @Test
    void testRemoveSponsors() {
        Sponsor sponsor1 = new Sponsor("Logitech");
        Sponsor sponsor2 = new Sponsor("Vodafone");
        List<Sponsor> sponsors = new ArrayList<>();
        sponsors.add(sponsor1);
        sponsors.add(sponsor2);
        Team team = new Team("G2 Esports", null, sponsors);
        assertEquals(2, team.getSponsors().size());
        team.removeSponsor(sponsor1);
        assertEquals(1, team.getSponsors().size());
    }
}
