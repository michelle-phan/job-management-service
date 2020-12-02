package com.taiger.search.service.impl;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import com.taiger.search.config.Constants;
import com.taiger.search.domain.Scheduler;
import com.taiger.search.service.AirflowService;
import com.taiger.search.service.SchedulerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AirflowServiceImpl implements AirflowService {

  @Autowired private SchedulerService scheduleService;

  @Autowired private final RestTemplate restTemplate;

  private static final String APPLICATION_JSON = "application/json";

  @Value("${jms.airflow.host:http://localhost:8080/}")
  private String airflowHost;

  @Value("${jms.airflow.run:/api/experimental/dags/<DAG_NAME>/dag_runs}")
  private String airflowRun;

  @Value("${jms.airflow.pause:/api/experimental/dags/<DAG_NAME>/paused/<IS_PAUSE>}")
  private String airflowPause;

  @Override
  public void trigger(UUID uuid) {
    Scheduler scheduler = scheduleService.findSchedulerByUuid(uuid);
    pause(scheduler, false);

    String url = airflowHost + airflowRun.replace(Constants.DAG_NAME, scheduler.getDag());

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.valueOf(APPLICATION_JSON));

    HttpEntity<String> httpEntity = new HttpEntity<String>("{}", headers);

    ResponseEntity<String> response =
        restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);

    if (HttpStatus.OK.equals(response.getStatusCode())) {
      log.info("Triggering success!");
    } else {
      log.error("Error");
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }
  }

  @Override
  public void pause(UUID uuid, boolean isPause) {
    Scheduler scheduler = scheduleService.findSchedulerByUuid(uuid);
    pause(scheduler, isPause);
    scheduleService.activeScheduler(scheduler, !isPause);
  }

  private void pause(Scheduler scheduler, boolean isPause) {
    String url =
        airflowHost
            + airflowPause
                .replace(Constants.DAG_NAME, scheduler.getDag())
                .replace(Constants.IS_PAUSE, Boolean.toString(isPause));

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.valueOf(APPLICATION_JSON));

    HttpEntity<String> httpEntity = new HttpEntity<String>(headers);

    ResponseEntity<String> response =
        restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);

    if (HttpStatus.OK.equals(response.getStatusCode())) {
      log.info("Activating (Unpausing) success! Current active: {}", isPause);
    } else {
      log.error("Error");
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }
  }
}
