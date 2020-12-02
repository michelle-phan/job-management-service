package com.taiger.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class JobManagementServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(JobManagementServiceApplication.class, args);
  }

  @Bean
  public RestTemplate restTemplate() {
    RestTemplate rest = new RestTemplate();
    return rest;
  }
}
