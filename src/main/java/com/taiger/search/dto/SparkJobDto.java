package com.taiger.search.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.springframework.validation.annotation.Validated;

import com.taiger.search.domain.Scheduler;
import com.taiger.search.domain.SparkJob;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
// @Setter
@ToString
@Validated
@Builder
@AllArgsConstructor
@ApiModel("SparkJob")
public class SparkJobDto {

  private UUID uuid;

  @Pattern(regexp = "^[A-Za-z0-9\\-_\\s]+$")
  @NotNull private String name;

  @NotNull private String description;

  @NotNull private String file;

  @NotNull private String mainClass;

  @NotNull private int numberOfArgs;

  private List<ResourceRefDto> schedulers;

  public SparkJobDto() {}

  public static SparkJobDto of(SparkJob sparkJob) {
    return SparkJobDto.builder()
        .uuid(sparkJob.getUuid())
        .name(sparkJob.getName())
        .description(sparkJob.getDescription())
        .file(sparkJob.getFile())
        .mainClass(sparkJob.getMainClass())
        .numberOfArgs(sparkJob.getNumberOfArgs())
        .schedulers(ofScheduler(sparkJob.getSchedulers()))
        .build();
  }

  private static List<ResourceRefDto> ofScheduler(List<Scheduler> schedulers) {
    if (schedulers == null) {
      return new ArrayList<>(0);
    }
    return schedulers
        .stream()
        .map(
            scheduler ->
                ResourceRefDto.builder()
                    .uuid(scheduler.getUuid())
                    .resource(scheduler.getName())
                    .build())
        .collect(Collectors.toList());
  }
}
