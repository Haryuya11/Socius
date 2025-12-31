package com.uit.sociusmvcapp.shared.endpoint;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** HealthCheckController provides a simple endpoint to check the health of the application. */
@RestController
@RequestMapping
public class HealthCheckController {

  /**
   * Endpoint to check if the application is running.
   *
   * @return "pong" string indicating the application is alive
   */
  @RequestMapping("/ping")
  public String ping() {
    return "pong";
  }
}
