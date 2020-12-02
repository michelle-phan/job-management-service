package com.taiger.search.dto;

import java.sql.Timestamp;
import java.util.UUID;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.taiger.search.domain.Scheduler;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Validated
@Builder
@AllArgsConstructor
@ApiModel("Scheduler")
public class SchedulerDto {

  private UUID uuid;

  @Pattern(regexp = "^[A-Za-z0-9\\-_\\s]+$")
  @NotNull private String name;

  @NotNull private String description;

  @NotNull private String args;

  @NotNull private String driverMemory;

  @NotNull private int executorCores;

  @NotNull private int retries;

  @NotNull private String email;

  @NotNull private boolean active;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  @NotNull private Timestamp startDate;

  @NotNull private String scheduleInterval;

  private SparkJobDto sparkJob;

  public SchedulerDto() {}

  public static SchedulerDto of(Scheduler scheduler) {
    return SchedulerDto.builder()
        .uuid(scheduler.getUuid())
        .name(scheduler.getName())
        .description(scheduler.getDescription())
        .args(scheduler.getArgs())
        .driverMemory(scheduler.getDriverMemory())
        .executorCores(scheduler.getExecutorCores())
        .retries(scheduler.getRetries())
        .email(scheduler.getEmail())
        .startDate(scheduler.getStartDate())
        .scheduleInterval(scheduler.getScheduleInterval())
        .sparkJob(SparkJobDto.of(scheduler.getSparkJob()))
        .active(scheduler.isActive())
        .build();
  }
}
