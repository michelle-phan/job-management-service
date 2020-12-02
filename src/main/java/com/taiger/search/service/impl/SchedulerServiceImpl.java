package com.taiger.search.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import com.google.common.collect.Lists;
import com.taiger.search.config.Constants;
import com.taiger.search.domain.Scheduler;
import com.taiger.search.domain.SparkJob;
import com.taiger.search.dto.SchedulerDto;
import com.taiger.search.dto.SchedulerForm;
import com.taiger.search.exception.NotFoundException;
import com.taiger.search.repository.SchedulerRepository;
import com.taiger.search.service.SchedulerService;
import com.taiger.search.service.SparkJobService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SchedulerServiceImpl implements SchedulerService {

  @Value("${jms.airflow.path:/Users/michellephan/Documents/NUS/}")
  private String airflowPath;

  @Value("${jms.airflow.template:classpath:template.py}")
  private String airflowTemplate;

  @Value("${jms.airflow.file-extension:.py}")
  private String airflowFileExtension;

  @Value("${jms.spark.livy-host}")
  private String sparkLivyHost;

  @Value("${jms.spark.path.docker}")
  private String sparkPathDocker;

  @Value("${jms.upload.path:/Users/michellephan/Documents/NUS/}")
  private String uploadPath;

  private static final String NOT_FOUND_UUID = "Not found %s with uuid: %s";

  private static final String NOT_FOUND_FIELD = "Not found field %s to apply sorted";

  @Autowired ResourceLoader resourceLoader;

  @Autowired private SchedulerRepository schedulerRepository;

  @Autowired private SparkJobService sparkJobService;

  @Override
  public List<Scheduler> findAll(String sorted, String order) {
    Sort.Direction direction =
        (order != null) ? Sort.Direction.valueOf(order.toUpperCase()) : Sort.Direction.ASC;

    // check if property exist in bean
    if (sorted != null && BeanUtils.getPropertyDescriptor(Scheduler.class, sorted) == null) {
      throw new IllegalArgumentException(String.format(NOT_FOUND_FIELD, sorted));
    }
    Sort sort = Sort.by(direction, (sorted == null) ? "id" : sorted);

    Iterable<Scheduler> schedulers;
    schedulers = schedulerRepository.findAll(sort);

    return Lists.newArrayList(schedulers);
  }

  @Override
  public Scheduler findSchedulerByUuid(UUID uuid) {
    Scheduler scheduler =
        schedulerRepository
            .findByUuid(uuid)
            .orElseThrow(
                () -> new NotFoundException(String.format(NOT_FOUND_UUID, "scheduler", uuid)));
    return scheduler;
  }

  @Override
  public Scheduler addScheduler(SchedulerForm schedulerForm) {
    Scheduler scheduler = new Scheduler();
    BeanUtils.copyProperties(schedulerForm, scheduler);

    SparkJob sparkJob = sparkJobService.findSparkJobByUuid(schedulerForm.getSparkJob());
    scheduler.setSparkJob(sparkJob);

    scheduler.setCreatedDate(new Timestamp(System.currentTimeMillis()));
    scheduler.setCreatedBy("admin");
    scheduler = schedulerRepository.save(scheduler);
    createDagFile(scheduler, airflowPath, scheduler.getDag());
    log.info("Created new scheduler uuid: {}", scheduler.getUuid());
    return scheduler;
  }

  private void createDagFile(Scheduler scheduler, String path, String fileName) {
    try {
      Resource resource = resourceLoader.getResource(airflowTemplate);
      InputStream inputStream = resource.getInputStream();
      Charset charset = StandardCharsets.UTF_8;
      byte[] bdata = FileCopyUtils.copyToByteArray(inputStream);
      String content = new String(bdata, charset);
      content = content.replaceAll(Constants.DAG_NAME, fileName);
      content = content.replaceAll(Constants.LIVY_HOST_NAME, sparkLivyHost);
      content = content.replaceAll(Constants.EMAIL, scheduler.getEmail());
      String datetime = convertTimestampToDatetimePython(scheduler.getStartDate());
      content = content.replaceAll(Constants.START_DATE, datetime);
      content = content.replaceAll(Constants.RETRIES, String.valueOf(scheduler.getRetries()));
      String scheduleInterval = String.valueOf(scheduler.getScheduleInterval());
      content = content.replaceAll(Constants.SCHEDULE_INTERVAL, scheduleInterval);
      content = content.replaceAll(Constants.TASK_ID_NAME, getTaskName(scheduler));
      content = content.replaceAll(Constants.CLASS_NAME, scheduler.getSparkJob().getMainClass());
      String filename = scheduler.getSparkJob().getPath().replace(uploadPath, "");
      content = content.replaceAll(Constants.PATH, sparkPathDocker + filename);
      String args = scheduler.getArgs().replaceAll(uploadPath, sparkPathDocker);
      content = content.replaceAll(Constants.ARGS, args);
      content =
          content.replaceAll(Constants.EXECUTOR_CORE, String.valueOf(scheduler.getExecutorCores()));
      content = content.replaceAll(Constants.DRIVER_MEMORY, scheduler.getDriverMemory());
      Path newPath = Paths.get(path, fileName + airflowFileExtension).toAbsolutePath().normalize();
      Files.write(newPath, content.getBytes(charset));
    } catch (IOException e) {
      log.error("Error when creating dag file for uuid: {}", scheduler.getUuid());
    }
  }

  private String getTaskName(Scheduler scheduler) {
    String s = scheduler.getDescription().replaceAll("[^a-zA-Z0-9]", "");
    if (s.length() < 20) return s;
    return s.substring(0, 20);
  }

  private String convertTimestampToDatetimePython(Timestamp date) {
    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    calendar.setTime(date);
    return String.format(
        "%d, %d, %d, %d, %d",
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH) + 1,
        calendar.get(Calendar.DAY_OF_MONTH),
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE));
  }

  @Override
  public Scheduler updateScheduler(SchedulerDto schedulerDto) {
    Scheduler scheduler = findSchedulerByUuid(schedulerDto.getUuid());
    BeanUtils.copyProperties(schedulerDto, scheduler, "name", "sparkJob");
    scheduler.setModifiedDate(new Timestamp(System.currentTimeMillis()));
    scheduler.setModifiedBy("admin");
    scheduler = schedulerRepository.save(scheduler);
    log.info("Updated scheduler uuid: {}", scheduler.getUuid());
    createDagFile(scheduler, airflowPath, scheduler.getDag());
    return scheduler;
  }

  @Override
  public Scheduler activeScheduler(Scheduler scheduler, boolean active) {
    scheduler.setModifiedDate(new Timestamp(System.currentTimeMillis()));
    scheduler.setModifiedBy("admin");
    scheduler.setActive(active);
    scheduler = schedulerRepository.save(scheduler);
    log.info("Active scheduler uuid: {} / active: {}", scheduler.getUuid(), active);
    createDagFile(scheduler, airflowPath, scheduler.getDag());
    return scheduler;
  }

  @Override
  public void deleteScheduler(UUID uuid) {
    Scheduler scheduler = findSchedulerByUuid(uuid);
    schedulerRepository.delete(scheduler);
    log.info("Deleted scheduler uuid: {}", uuid);
    try {
      Path path =
          Paths.get(airflowPath, scheduler.getDag() + airflowFileExtension)
              .toAbsolutePath()
              .normalize();
      Files.delete(path);
    } catch (IOException e) {
      log.error("Error when deleting dag file for uuid: {}", scheduler.getUuid());
    }
  }
}
