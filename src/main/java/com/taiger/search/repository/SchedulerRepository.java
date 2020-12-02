package com.taiger.search.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.taiger.search.domain.Scheduler;

@Repository
public interface SchedulerRepository extends JpaRepository<Scheduler, UUID> {

  /** Find Scheduler by UUID */
  Optional<Scheduler> findByUuid(UUID uuid);
}
