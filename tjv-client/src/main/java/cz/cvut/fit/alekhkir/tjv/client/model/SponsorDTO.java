package cz.cvut.fit.alekhkir.tjv.client.model;

import java.util.List;

public class SponsorDTO {
    private Long id;
    private String name;
    private List<Long> teamsIds;

    public SponsorDTO() {}

    public SponsorDTO(Long id, String name, List<Long> teamsIds) {
        this.id = id;
        this.name = name;
        this.teamsIds = teamsIds;
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

    public List<Long> getTeamsIds() {
        return teamsIds;
    }
}
