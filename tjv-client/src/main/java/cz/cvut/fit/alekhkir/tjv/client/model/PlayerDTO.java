package cz.cvut.fit.alekhkir.tjv.client.model;

public class PlayerDTO {
    private Long id;
    private String name;
    private String surname;
    private String nickname;
    private Integer age;
    private String countryOfBirth;
    private Long teamId;

    public PlayerDTO() {}

    public PlayerDTO(Long id, String name, String surname, String nickname, Integer age, String countryOfBirth, Long teamId){
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.nickname = nickname;
        this.age = age;
        this.countryOfBirth = countryOfBirth;
        this.teamId = teamId;
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

    public String getSurname() {
        return surname;
    }

    public String getNickname() {
        return nickname;
    }

    public Integer getAge() {
        return age;
    }

    public String getCountryOfBirth() {
        return countryOfBirth;
    }

    public Long getTeamId() {
        return teamId;
    }
}
