package com.pjs.golf.warmup.repository;

import com.pjs.golf.warmup.entity.WarmupScore;
import com.pjs.golf.warmup.entity.id.WarmupScoreId;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WarmupScoreJpaRepository extends JpaRepository<WarmupScore, WarmupScoreId> {


    @Query("SELECT a FROM WarmupScore a WHERE a.id.rounding.id.game.id = :id")
    List selectWarmupGameById(@Param("id") Long id);

}
