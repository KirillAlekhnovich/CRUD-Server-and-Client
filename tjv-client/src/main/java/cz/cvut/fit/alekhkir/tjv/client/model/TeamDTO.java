package cz.cvut.fit.alekhkir.tjv.client.model;

import java.util.List;

public class TeamDTO {
    private Long id;
    private String name;
    private List<Long> playersIds;
    private List<Long> sponsorsIds;

    public TeamDTO() {}

    public TeamDTO(Long id, String name, List<Long> playersIds, List<Long> sponsorsIds) {
        this.id = id;
        this.name = name;
        this.playersIds = playersIds;
        this.sponsorsIds = sponsorsIds;
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

    public List<Long> getPlayersIds() {
        return playersIds;
    }

    public List<Long> getSponsorsIds() {
        return sponsorsIds;
    }
}
