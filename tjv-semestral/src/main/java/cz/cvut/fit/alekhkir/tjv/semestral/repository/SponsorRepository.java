package cz.cvut.fit.alekhkir.tjv.semestral.repository;

import cz.cvut.fit.alekhkir.tjv.semestral.entities.Sponsor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SponsorRepository extends JpaRepository<Sponsor, Long> {
    Sponsor findByName(String name);

    void deleteByName(String name);
}
