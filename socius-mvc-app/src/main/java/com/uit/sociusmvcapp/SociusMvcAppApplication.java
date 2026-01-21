package com.uit.sociusmvcapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/** Main application class for Socius MVC Application. */
@SpringBootApplication(scanBasePackages = {"com.uit.sociusmvcapp"})
@EnableAsync
@EnableCaching
@EnableRetry
@EnableScheduling
public class SociusMvcAppApplication {

  /**
   * Main method to run the Spring Boot application.
   *
   * @param args command-line arguments
   */
  public static void main(String[] args) {
    SpringApplication.run(SociusMvcAppApplication.class, args);
  }
}
