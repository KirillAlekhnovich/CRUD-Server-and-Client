package cz.cvut.fit.alekhkir.tjv.semestral.repository;

import cz.cvut.fit.alekhkir.tjv.semestral.entities.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
    Team findByName(String name);

    void deleteByName(String name);
}
