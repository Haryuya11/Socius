package com.uit.sociuswebfluxapp;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** SociusWebfluxAppApplication is the main entry point for the Socius WebFlux application. */
@SpringBootApplication(scanBasePackages = {"com.uit.sociuswebfluxapp", "com.uit.sociuscoremodules"})
@EnableRabbit
public class SociusWebfluxAppApplication {

  /**
   * Main method to run the Spring Boot application.
   *
   * @param args command-line arguments
   */
  public static void main(String[] args) {
    SpringApplication.run(SociusWebfluxAppApplication.class, args);
  }
}
