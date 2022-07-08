package cz.cvut.fit.alekhkir.tjv.semestral.entities;

import com.sun.istack.NotNull;

import javax.persistence.*;
import java.util.List;

@Entity
public class Sponsor {
    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @Column(unique = true)
    private String name;

    @ManyToMany
    @JoinTable(name = "team_sponsor",
            joinColumns = @JoinColumn(name = "id_sponsor"),
            inverseJoinColumns = @JoinColumn(name = "team_id"))
    private List<Team> teams;

    public Sponsor(String name) {
        this.name = name;
    }

    public Sponsor() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }
}
