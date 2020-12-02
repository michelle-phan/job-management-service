package com.taiger.search.dto;

import java.sql.Timestamp;
import java.util.UUID;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Validated
@NoArgsConstructor
public class SchedulerForm {

  @NotNull
  @Pattern(regexp = "^[A-Za-z0-9\\-_\\s]+$")
  private String name;

  @NotNull private String args;

  @NotNull private String driverMemory;

  @NotNull private int executorCores;

  @NotNull private int retries;

  @NotNull private String email;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  @NotNull private Timestamp startDate;

  @NotNull private String scheduleInterval;

  @NotNull private UUID sparkJob;

  @NotNull private String description;
}
