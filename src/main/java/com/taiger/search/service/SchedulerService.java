package com.taiger.search.service;

import java.util.List;
import java.util.UUID;

import com.taiger.search.domain.Scheduler;
import com.taiger.search.dto.SchedulerDto;
import com.taiger.search.dto.SchedulerForm;

public interface SchedulerService {

  List<Scheduler> findAll(String sorted, String order);

  Scheduler findSchedulerByUuid(UUID uuid);

  Scheduler addScheduler(SchedulerForm schedulerForm);

  Scheduler updateScheduler(SchedulerDto schedulerDto);

  void deleteScheduler(UUID uuid);

  Scheduler activeScheduler(Scheduler scheduler, boolean active);
}
