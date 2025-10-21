package com.uit.sociusmvcapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Main application class for Socius MVC Application. */
@SpringBootApplication(scanBasePackages = {"com.uit.sociusmvcapp", "com.uit.sociuscoremodules"})
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
