package com.taiger.search.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.taiger.search.domain.SparkJob;

public interface SparkJobRepository extends JpaRepository<SparkJob, UUID> {

  /** Find SparkJob by UUID */
  Optional<SparkJob> findByUuid(UUID uuid);
}
