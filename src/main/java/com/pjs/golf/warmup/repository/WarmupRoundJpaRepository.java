package com.pjs.golf.warmup.repository;

import com.pjs.golf.warmup.entity.WarmupRound;
import com.pjs.golf.warmup.entity.id.WarmupRoundId;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WarmupRoundJpaRepository extends JpaRepository<WarmupRound, WarmupRoundId> {


    @Query("SELECT a FROM WarmupRound a WHERE a.id.game.id = :id")
    List selectWarmupRoundByGameId(@Param("id") Long id);
}
