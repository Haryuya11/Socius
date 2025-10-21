package com.uit.sociuswebfluxapp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class SociusWebfluxAppApplicationTests {

  @Test
  void contextLoads() {
    /* Intentionally left empty: this test verifies that the Spring application
    context starts without throwing exceptions. No assertions required. */
  }
}
