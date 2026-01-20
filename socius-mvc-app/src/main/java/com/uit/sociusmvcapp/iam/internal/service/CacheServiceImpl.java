package com.uit.sociusmvcapp.iam.internal.service;

import com.uit.sociusmvcapp.iam.CacheService;
import com.uit.sociusmvcapp.iam.dto.CacheClearResultDto;
import com.uit.sociusmvcapp.iam.internal.component.ApiPermissionCache;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

/**
 * Implementation of CacheService for managing authorization-related caches. Clears both the custom
 * TTL-based API permission cache and Spring-managed user principal cache.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CacheServiceImpl implements CacheService {

  private static final String API_PERMISSION_CACHE = "api-permissions";
  private static final String USER_PRINCIPAL_CACHE = "user-principal";

  private final ApiPermissionCache apiPermissionCache;
  private final CacheManager cacheManager;

  @Override
  public CacheClearResultDto clearAuthorizationCaches() {
    List<String> clearedCaches = new ArrayList<>();

    // Clear API permission cache (TTL-based custom cache)
    apiPermissionCache.clear();
    clearedCaches.add(API_PERMISSION_CACHE);
    log.info("Cleared API permission cache");

    // Clear user principal cache (Spring Cache)
    var userPrincipalCache = cacheManager.getCache(USER_PRINCIPAL_CACHE);
    if (userPrincipalCache != null) {
      userPrincipalCache.clear();
      clearedCaches.add(USER_PRINCIPAL_CACHE);
      log.info("Cleared user principal cache");
    }

    log.info("Authorization caches cleared successfully: {}", clearedCaches);

    return CacheClearResultDto.builder()
        .clearedCaches(clearedCaches)
        .clearedAt(LocalDateTime.now())
        .build();
  }
}
