package com.taiger.search.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.springframework.validation.annotation.Validated;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Validated
@NoArgsConstructor
public class SparkJobForm {

  @NotNull
  @Pattern(regexp = "^[A-Za-z0-9\\-_\\s]+$")
  private String name;

  @NotNull private String mainClass;

  @NotNull private int numberOfArgs;

  @NotNull private String description;
}
