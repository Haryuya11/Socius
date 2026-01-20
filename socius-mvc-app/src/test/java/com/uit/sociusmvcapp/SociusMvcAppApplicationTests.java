package com.uit.sociusmvcapp;

import com.uit.sociusmvcapp.iam.ApiPermissionService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles("test")
class SociusMvcAppApplicationTests {

  @MockitoBean private ApiPermissionService apiPermissionService;

  @Test
  void contextLoads() {
    /* Intentionally left empty: this test verifies that the Spring application
    context starts without throwing exceptions. No assertions required. */
  }
}
