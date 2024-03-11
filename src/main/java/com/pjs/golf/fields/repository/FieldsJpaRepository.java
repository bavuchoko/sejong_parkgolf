package com.pjs.golf.fields.repository;

import com.pjs.golf.fields.entity.Fields;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FieldsJpaRepository extends JpaRepository<Fields, Integer> {
    Optional<Fields> findById(int id);
}
