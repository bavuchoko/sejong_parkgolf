package com.pjs.golf.account.repository;

import com.pjs.golf.account.entity.Account;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AccountJpaRepository extends JpaRepository<Account, Integer> {
    Optional<Account> findByUsername(String username);
    List findAllByName (String[] names);
    List findAllByIdIn(List<Integer> ids);
    @Query("SELECT a FROM Account a LEFT JOIN FETCH a.roles WHERE a.username = :username")
    Optional<Account> findByUsernameWithRoles(@Param("username") String username);
}
