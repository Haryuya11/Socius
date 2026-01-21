package com.uit.sociusmvcapp;

import com.uit.sociusmvcapp.iam.ApiPermissionService;
import com.uit.sociusmvcapp.shared.service.EncryptionService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles("test")
class SociusMvcAppApplicationTests {

  @MockitoBean private ApiPermissionService apiPermissionService;

  @MockitoBean private EncryptionService encryptionService;

  @Test
  void contextLoads() {
    /* Intentionally left empty: this test verifies that the Spring application
    context starts without throwing exceptions. No assertions required. */
  }
}
