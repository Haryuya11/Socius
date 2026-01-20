package com.uit.sociusmvcapp.iam;

import com.uit.sociusmvcapp.iam.dto.CacheClearResultDto;
import com.uit.sociusmvcapp.shared.constants.MessageConstant;
import com.uit.sociusmvcapp.shared.response.Response;
import com.uit.sociusmvcapp.shared.service.I18nService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for cache management operations. Provides endpoints for system administrators to
 * manage authorization caches.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/cache")
public class CacheController {

  private final CacheService cacheService;
  private final I18nService i18nService;

  /**
   * Clears all authorization-related caches. Only accessible by system administrators.
   *
   * @return ResponseEntity containing the cache clear result
   */
  @PostMapping("/clear")
  @PreAuthorize("@permissionService.isSystemAdmin()")
  public ResponseEntity<Response> clearCache() {
    CacheClearResultDto result = cacheService.clearAuthorizationCaches();
    Response response =
        Response.builder()
            .success(true)
            .status(200)
            .code(MessageConstant.S_CACHE_001)
            .message(i18nService.getMessage(MessageConstant.S_CACHE_001))
            .data(result)
            .build();

    return ResponseEntity.ok(response);
  }
}
