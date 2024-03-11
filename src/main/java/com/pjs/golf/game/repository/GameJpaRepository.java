package com.pjs.golf.game.repository;

import com.pjs.golf.game.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameJpaRepository extends JpaRepository<Game, Long> {
}
