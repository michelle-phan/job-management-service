package com.taiger.search.service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.taiger.search.domain.SparkJob;
import com.taiger.search.dto.SparkJobDto;
import com.taiger.search.dto.SparkJobForm;

public interface SparkJobService {

  List<SparkJob> findAll(String sorted, String order);

  SparkJob findSparkJobByUuid(UUID uuid);

  SparkJob addSparkJob(SparkJobForm sparkJobForm, MultipartFile file) throws IOException;

  SparkJob updateSparkJob(SparkJobDto sparkJobDto);

  void deleteSparkJob(UUID uuid);
}
