package com.taiger.search.controller;

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

import com.taiger.search.domain.Scheduler;
import com.taiger.search.dto.ModelApiResponse;
import com.taiger.search.dto.SchedulerDto;
import com.taiger.search.dto.SchedulerForm;
import com.taiger.search.service.AirflowService;
import com.taiger.search.service.SchedulerService;

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
    value = "Schedulers",
    tags = {"Schedulers"})
public class SchedulerController {

  @Autowired private SchedulerService schedulerService;

  @Autowired private AirflowService airflowService;

  @ApiOperation(
      value = "get scheduler",
      nickname = "getSchedulersByUuid",
      notes = "Read a scheduler by uuid",
      response = SchedulerDto.class,
      //      authorizations = {@Authorization(value = "JWT")},
      tags = {"schedulers"})
  @ApiResponses(
      value = {
        @ApiResponse(code = 200, message = "OK", response = SchedulerDto.class),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 404, message = "Not Found")
      })
  @GetMapping(
      value = "/schedulers/{uuid}",
      produces = {"application/json"})
  public ResponseEntity<SchedulerDto> getSchedulerByUuid(
      @ApiParam(value = "scheduler-uuid", required = true) @PathVariable("uuid") UUID uuid) {
    log.info("[ GET ] -> /schedulers/{}", uuid);
    Scheduler scheduler = schedulerService.findSchedulerByUuid(uuid);
    return ResponseEntity.ok(SchedulerDto.of(scheduler));
  }

  @ApiOperation(
      value = "List schedulers",
      nickname = "getSchedulers",
      notes = "List all schedulers",
      response = SchedulerDto.class,
      responseContainer = "List",
      //      authorizations = {@Authorization(value = "JWT")},
      tags = {"schedulers"})
  @ApiResponses(
      value = {
        @ApiResponse(
            code = 200,
            message = "OK",
            response = SchedulerDto.class,
            responseContainer = "List"),
        @ApiResponse(code = 401, message = "Unauthorized")
      })
  @GetMapping(
      value = "/schedulers",
      produces = {"application/json"})
  public ResponseEntity<List<SchedulerDto>> getSchedulers(
      @ApiParam(value = "field code to sort by")
          @Valid
          @RequestParam(value = "sorted", required = false)
          String sorted,
      @ApiParam(value = "Sort order", allowableValues = "asc, desc")
          @Valid
          @RequestParam(value = "sort", required = false)
          String order) {
    log.info("[ GET ] -> /schedulers?sorted={}&order{}", sorted, order);
    List<Scheduler> schedulers = schedulerService.findAll(sorted, order);
    return ResponseEntity.ok(
        schedulers.stream().map(SchedulerDto::of).collect(Collectors.toList()));
  }

  @ApiOperation(
      value = "Creates a new scheduler.",
      nickname = "addScheduler",
      notes = "Creates a new scheduler.",
      response = SchedulerDto.class,
      //      authorizations = {@Authorization(value = "JWT")},
      tags = {"schedulers"})
  @ApiResponses(
      value = {
        @ApiResponse(code = 201, message = "Created", response = SchedulerDto.class),
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
      value = "/schedulers",
      produces = {"application/json"})
  ResponseEntity<SchedulerDto> addScheduler(@ApiIgnore @Valid SchedulerForm body) {
    log.info("[ POST ] -> /schedulers Body: {}", body);
    Scheduler scheduler = schedulerService.addScheduler(body);
    return ResponseEntity.status(HttpStatus.CREATED).body(SchedulerDto.of(scheduler));
  }

  @ApiOperation(
      value = "Delete a scheduler.",
      nickname = "deleteScheduler",
      notes = "Delete a scheduler",
      response = ModelApiResponse.class,
      //      authorizations = {@Authorization(value = "JWT")},
      tags = {"schedulers"})
  @ApiResponses(
      value = {
        @ApiResponse(code = 204, message = "No Content"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Scheduler not found")
      })
  @DeleteMapping(
      value = "/schedulers/{uuid}",
      produces = {"application/json"})
  ResponseEntity<ModelApiResponse> deleteScheduler(
      @ApiParam(value = "Scheduler uuid to delete", required = true) @PathVariable("uuid")
          UUID uuid) {
    log.info("[ DELETE ] -> /schedulers/{}", uuid);
    schedulerService.deleteScheduler(uuid);
    return ResponseEntity.status(HttpStatus.NO_CONTENT)
        .body(
            ModelApiResponse.builder()
                .code(HttpStatus.NO_CONTENT.value())
                .type("Scheduler deleted")
                .build());
  }

  @ApiOperation(
      value = "Update a scheduler.",
      nickname = "updateScheduler",
      notes = "Update a scheduler.",
      response = SchedulerDto.class,
      //      authorizations = {@Authorization(value = "JWT")},
      tags = {"schedulers"})
  @ApiResponses(
      value = {
        @ApiResponse(code = 200, message = "Successful operation", response = SchedulerDto.class),
        @ApiResponse(code = 201, message = "Created"),
        @ApiResponse(code = 400, message = "Invalid data supplied"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Scheduler not found")
      })
  @PutMapping(
      value = "/schedulers/{uuid}",
      produces = {"application/json"},
      consumes = {"application/json"})
  ResponseEntity<SchedulerDto> updateScheduler(
      @ApiParam(value = "Code of the scheduler to be updated", required = true)
          @PathVariable("uuid")
          UUID uuid,
      @ApiParam(value = "") @Valid @RequestBody SchedulerDto body) {
    log.info("[ PUT ] -> /schedulers/{} Body: {}", uuid, body);
    Scheduler scheduler = schedulerService.updateScheduler(body);
    return ResponseEntity.ok(SchedulerDto.of(scheduler));
  }

  @ApiOperation(
      value = "get scheduler",
      nickname = "getSchedulersByUuid",
      notes = "Read a scheduler by uuid",
      response = SchedulerDto.class,
      //      authorizations = {@Authorization(value = "JWT")},
      tags = {"schedulers"})
  @ApiResponses(
      value = {
        @ApiResponse(code = 200, message = "OK", response = SchedulerDto.class),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 404, message = "Not Found")
      })
  @GetMapping(
      value = "/schedulers/{uuid}/trigger",
      produces = {"application/json"})
  public ResponseEntity<ModelApiResponse> trigger(
      @ApiParam(value = "scheduler-uuid", required = true) @PathVariable("uuid") UUID uuid) {
    log.info("[ TRIGGER ] -> /schedulers/trigger/{}", uuid);
    airflowService.trigger(uuid);
    return ResponseEntity.status(HttpStatus.NO_CONTENT)
        .body(
            ModelApiResponse.builder()
                .code(HttpStatus.NO_CONTENT.value())
                .type("Scheduler triggered")
                .build());
  }

  @ApiOperation(
      value = "get scheduler",
      nickname = "getSchedulersByUuid",
      notes = "Read a scheduler by uuid",
      response = SchedulerDto.class,
      //      authorizations = {@Authorization(value = "JWT")},
      tags = {"schedulers"})
  @ApiResponses(
      value = {
        @ApiResponse(code = 200, message = "OK", response = SchedulerDto.class),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 404, message = "Not Found")
      })
  @GetMapping(
      value = "/schedulers/{uuid}/active",
      produces = {"application/json"})
  public ResponseEntity<ModelApiResponse> active(
      @ApiParam(value = "scheduler-uuid", required = true) @PathVariable("uuid") UUID uuid,
      boolean active) {
    log.info("[ TRIGGER ] -> /schedulers/active/{}", uuid);
    airflowService.pause(uuid, !active);
    return ResponseEntity.status(HttpStatus.NO_CONTENT)
        .body(
            ModelApiResponse.builder()
                .code(HttpStatus.NO_CONTENT.value())
                .type("Scheduler triggered")
                .build());
  }
}
