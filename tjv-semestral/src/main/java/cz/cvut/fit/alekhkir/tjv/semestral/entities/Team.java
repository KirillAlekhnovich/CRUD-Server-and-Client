package cz.cvut.fit.alekhkir.tjv.semestral.entities;

import com.sun.istack.NotNull;

import javax.persistence.*;
import java.util.Collections;
import java.util.List;

@Entity
public class Team {
    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @Column(unique = true)
    private String name;

    @OneToMany(targetEntity = Player.class)
    @JoinColumn(name = "id_team")
    private List<Player> players;

    @ManyToMany
    @JoinTable(name = "team_sponsor",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "id_sponsor"))
    private List<Sponsor> sponsors;

    public Team(String name, List<Player> players, List<Sponsor> sponsors) {
        this.name = name;
        this.players = players;
        this.sponsors = sponsors;
    }

    public Team() {
        this.players = Collections.emptyList();
        this.sponsors = Collections.emptyList();
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public List<Sponsor> getSponsors() {
        return sponsors;
    }

    public void setSponsors(List<Sponsor> sponsors) {
        this.sponsors = sponsors;
    }

    public void addPlayer(Player player) {
        this.players.add(player);
    }

    public void addSponsor(Sponsor sponsor) {
        this.sponsors.add(sponsor);
    }

    public void removePlayer(Player player) {
        this.players.remove(player);
    }

    public void removeSponsor(Sponsor sponsor) {
        this.sponsors.remove(sponsor);
    }
}
