package com.pjs.golf.warmup.repository;

import com.pjs.golf.account.entity.Account;
import com.pjs.golf.warmup.entity.WarmupGame;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface WarmupGameJpaRepository extends JpaRepository<WarmupGame, Long> {


}
