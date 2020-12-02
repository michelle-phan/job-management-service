package com.taiger.search.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Lists;
import com.taiger.search.domain.SparkJob;
import com.taiger.search.dto.SparkJobDto;
import com.taiger.search.dto.SparkJobForm;
import com.taiger.search.exception.NotFoundException;
import com.taiger.search.repository.SparkJobRepository;
import com.taiger.search.service.SparkJobService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SparkJobServiceImpl implements SparkJobService {

  @Value("${jms.upload.path:/Users/michellephan/Documents/NUS/}")
  private String uploadPath;

  private static final String NOT_FOUND_UUID = "Not found %s with uuid: %s";

  private static final String NOT_FOUND_FIELD = "Not found field %s to apply sorted";

  @Autowired private SparkJobRepository sparkJobRepository;
  // TODO: check schedulers before deleting
  //  @Autowired private SchedulerService scheduleService;

  @Override
  public List<SparkJob> findAll(String sorted, String order) {
    Sort.Direction direction =
        (order != null) ? Sort.Direction.valueOf(order.toUpperCase()) : Sort.Direction.ASC;

    // check if property exist in bean
    if (sorted != null && BeanUtils.getPropertyDescriptor(SparkJob.class, sorted) == null) {
      throw new IllegalArgumentException(String.format(NOT_FOUND_FIELD, sorted));
    }
    Sort sort = Sort.by(direction, (sorted == null) ? "id" : sorted);
    Iterable<SparkJob> sparkJobs;
    sparkJobs = sparkJobRepository.findAll(sort);

    return Lists.newArrayList(sparkJobs);
  }

  @Override
  public SparkJob findSparkJobByUuid(UUID uuid) {
    SparkJob sparkJob =
        sparkJobRepository
            .findByUuid(uuid)
            .orElseThrow(
                () -> new NotFoundException(String.format(NOT_FOUND_UUID, "sparkJob", uuid)));
    return sparkJob;
  }

  @Override
  public SparkJob addSparkJob(SparkJobForm sparkJobForm, MultipartFile file) throws IOException {
    SparkJob sparkJob = new SparkJob();
    BeanUtils.copyProperties(sparkJobForm, sparkJob);
    long currentTimeMillis = System.currentTimeMillis();
    String path = upload(file, String.valueOf(currentTimeMillis));
    sparkJob.setPath(path);
    sparkJob.setFile(file.getOriginalFilename());

    sparkJob.setCreatedDate(new Timestamp(currentTimeMillis));
    sparkJob.setCreatedBy("admin");

    sparkJob = sparkJobRepository.save(sparkJob);
    log.info("Created new sparkJob uuid: {}", sparkJob.getUuid());
    log.info("Created new sparkJob uuid: {}", file.getOriginalFilename());
    return sparkJob;
  }

  private String upload(MultipartFile file, String fileName) throws IOException {
    String fileExtension = getFileExtension(file);
    Path targetLocation =
        Paths.get(uploadPath + fileName + fileExtension).toAbsolutePath().normalize();
    Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
    return targetLocation.toString();
  }

  private String getFileExtension(MultipartFile inFile) {
    String fileExtention =
        inFile.getOriginalFilename().substring(inFile.getOriginalFilename().lastIndexOf('.'));
    return fileExtention;
  }

  @Override
  public SparkJob updateSparkJob(SparkJobDto sparkJobDto) {
    SparkJob sparkJob = findSparkJobByUuid(sparkJobDto.getUuid());
    BeanUtils.copyProperties(sparkJobDto, sparkJob, "schedulers");
    sparkJob.setModifiedDate(new Timestamp(System.currentTimeMillis()));
    sparkJob.setModifiedBy("admin");
    sparkJob = sparkJobRepository.save(sparkJob);
    log.info("Updated sparkJob uuid: {}", sparkJob.getUuid());
    return sparkJob;
  }

  @Override
  public void deleteSparkJob(UUID uuid) {
    SparkJob sparkJob = findSparkJobByUuid(uuid);
    sparkJobRepository.delete(sparkJob);
    log.info("Deleted sparkJob uuid: {}", uuid);
  }
}
