package cz.cvut.fit.alekhkir.tjv.semestral.repository;

import cz.cvut.fit.alekhkir.tjv.semestral.entities.Player;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepository extends JpaRepository<Player, Long> {
    Player findByNickname(String nickname);

    void deleteByNickname(String nickname);
}
