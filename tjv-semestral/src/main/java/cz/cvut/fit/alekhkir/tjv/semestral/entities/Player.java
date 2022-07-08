package cz.cvut.fit.alekhkir.tjv.semestral.entities;

import com.sun.istack.NotNull;
import javax.persistence.*;

@Entity
public class Player {
    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private String surname;

    @NotNull
    @Column(unique = true)
    private String nickname;

    @NotNull
    private Integer age;

    @NotNull
    private String countryOfBirth;

    @NotNull
    @ManyToOne(targetEntity = Team.class)
    @JoinColumn(name = "id_team")
    private Team team;

    public Player(String name, String surname, String nickname, int age, String countryOfBirth, Team team) {
        this.name = name;
        this.surname = surname;
        this.nickname = nickname;
        this.age = age;
        this.countryOfBirth = countryOfBirth;
        this.team = team;
    }

    public Player() {}

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

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getCountryOfBirth() {
        return countryOfBirth;
    }

    public void setCountryOfBirth(String countryOfBirth) {
        this.countryOfBirth = countryOfBirth;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }
}
