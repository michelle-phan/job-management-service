package com.taiger.search.controller;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.taiger.search.domain.SparkJob;
import com.taiger.search.dto.ModelApiResponse;
import com.taiger.search.dto.SparkJobDto;
import com.taiger.search.dto.SparkJobForm;
import com.taiger.search.service.SparkJobService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import springfox.documentation.annotations.ApiIgnore;

@Slf4j
@RestController
@AllArgsConstructor
@Api(
    value = "SparkJobs",
    tags = {"SparkJobs"})
public class SparkJobController {

  @Autowired private SparkJobService sparkJobService;

  @ApiOperation(
      value = "get sparkJob",
      nickname = "getSparkJobsByUuid",
      notes = "Read a sparkJob by uuid",
      response = SparkJobDto.class,
      //      authorizations = {@Authorization(value = "JWT")},
      tags = {"sparkjobs"})
  @ApiResponses(
      value = {
        @ApiResponse(code = 200, message = "OK", response = SparkJobDto.class),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 404, message = "Not Found")
      })
  @GetMapping(
      value = "/sparkjobs/{uuid}",
      produces = {"application/json"})
  public ResponseEntity<SparkJobDto> getSparkJobByUuid(
      @ApiParam(value = "sparkJob-uuid", required = true) @PathVariable("uuid") UUID uuid) {
    log.info("[ GET ] -> /sparkjobs/{}", uuid);
    SparkJob sparkJob = sparkJobService.findSparkJobByUuid(uuid);
    return ResponseEntity.ok(SparkJobDto.of(sparkJob));
  }

  @ApiOperation(
      value = "List sparkjobs",
      nickname = "getSparkJobs",
      notes = "List all sparkjobs",
      response = SparkJobDto.class,
      responseContainer = "List",
      //      authorizations = {@Authorization(value = "JWT")},
      tags = {"sparkjobs"})
  @ApiResponses(
      value = {
        @ApiResponse(
            code = 200,
            message = "OK",
            response = SparkJobDto.class,
            responseContainer = "List"),
        @ApiResponse(code = 401, message = "Unauthorized")
      })
  @GetMapping(
      value = "/sparkjobs",
      produces = {"application/json"})
  public ResponseEntity<List<SparkJobDto>> getSparkJobs(
      @ApiParam(value = "field code to sort by")
          @Valid
          @RequestParam(value = "sorted", required = false)
          String sorted,
      @ApiParam(value = "Sort order", allowableValues = "asc, desc")
          @Valid
          @RequestParam(value = "sort", required = false)
          String order) {
    log.info("[ GET ] -> /sparkjobs?sorted={}&order{}", sorted, order);
    List<SparkJob> sparkjobs = sparkJobService.findAll(sorted, order);
    return ResponseEntity.ok(sparkjobs.stream().map(SparkJobDto::of).collect(Collectors.toList()));
  }

  @ApiOperation(
      value = "Creates a new sparkJob.",
      nickname = "addSparkJob",
      notes = "Creates a new sparkJob.",
      response = SparkJobDto.class,
      //      authorizations = {@Authorization(value = "JWT")},
      tags = {"sparkjobs"})
  @ApiResponses(
      value = {
        @ApiResponse(code = 201, message = "Created", response = SparkJobDto.class),
        @ApiResponse(code = 400, message = "Bad request"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Not Found")
      })
  @ApiImplicitParams({
    @ApiImplicitParam(paramType = "query", name = "code", required = true),
    @ApiImplicitParam(paramType = "query", name = "description", required = true)
  })
  @PostMapping(
      value = "/sparkjobs",
      produces = {"application/json"})
  ResponseEntity<SparkJobDto> addSparkJob(
      @ApiIgnore @Valid SparkJobForm body, @RequestParam("file") MultipartFile file)
      throws IOException {
    log.info("[ POST ] -> /sparkjobs Body: {}", body);
    SparkJob sparkJob = sparkJobService.addSparkJob(body, file);
    return ResponseEntity.status(HttpStatus.CREATED).body(SparkJobDto.of(sparkJob));
  }

  @ApiOperation(
      value = "Delete a sparkJob.",
      nickname = "deleteSparkJob",
      notes = "Delete a sparkJob",
      response = ModelApiResponse.class,
      //      authorizations = {@Authorization(value = "JWT")},
      tags = {"sparkjobs"})
  @ApiResponses(
      value = {
        @ApiResponse(code = 204, message = "No Content"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "SparkJob not found")
      })
  @DeleteMapping(
      value = "/sparkjobs/{uuid}",
      produces = {"application/json"})
  ResponseEntity<ModelApiResponse> deleteSparkJob(
      @ApiParam(value = "SparkJob uuid to delete", required = true) @PathVariable("uuid")
          UUID uuid) {
    log.info("[ DELETE ] -> /sparkjobs/{}", uuid);
    sparkJobService.deleteSparkJob(uuid);
    return ResponseEntity.status(HttpStatus.NO_CONTENT)
        .body(
            ModelApiResponse.builder()
                .code(HttpStatus.NO_CONTENT.value())
                .type("SparkJob deleted")
                .build());
  }

  @ApiOperation(
      value = "Update a sparkJob.",
      nickname = "updateSparkJob",
      notes = "Update a sparkJob.",
      response = SparkJobDto.class,
      //      authorizations = {@Authorization(value = "JWT")},
      tags = {"sparkjobs"})
  @ApiResponses(
      value = {
        @ApiResponse(code = 200, message = "Successful operation", response = SparkJobDto.class),
        @ApiResponse(code = 201, message = "Created"),
        @ApiResponse(code = 400, message = "Invalid data supplied"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "SparkJob not found")
      })
  @PutMapping(
      value = "/sparkjobs/{uuid}",
      produces = {"application/json"},
      consumes = {"application/json"})
  ResponseEntity<SparkJobDto> updateSparkJob(
      @ApiParam(value = "Code of the sparkJob to be updated", required = true) @PathVariable("uuid")
          UUID uuid,
      @ApiParam(value = "") @Valid @RequestBody SparkJobDto body) {
    log.info("[ PUT ] -> /sparkjobs/{} Body: {}", uuid, body);
    SparkJob sparkJob = sparkJobService.updateSparkJob(body);
    return ResponseEntity.ok(SparkJobDto.of(sparkJob));
  }
}
