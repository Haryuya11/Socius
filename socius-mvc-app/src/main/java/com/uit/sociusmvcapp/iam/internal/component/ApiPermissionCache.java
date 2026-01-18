package com.uit.sociusmvcapp.iam.internal.component;

import com.uit.sociusmvcapp.iam.internal.config.ApiPermissionCacheConfig;
import com.uit.sociusmvcapp.iam.internal.dto.ApiPermissionDto;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Lightweight cache for API permissions using ConcurrentHashMap with TTL. Designed for low memory
 * footprint (~400MB VM constraint).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ApiPermissionCache {

  private static final String CACHE_KEY = "api_permissions";

  private final ApiPermissionCacheConfig cacheConfig;

  /** Thread-safe cache storage. */
  private final ConcurrentHashMap<String, List<ApiPermissionDto>> cache = new ConcurrentHashMap<>();

  /** Timestamp when cache was last updated. */
  private final AtomicLong lastUpdatedAt = new AtomicLong(0);

  /**
   * Get cached permissions if cache is valid.
   *
   * @return list of cached permissions or null if cache is expired/empty
   */
  public List<ApiPermissionDto> get() {
    if (isCacheExpired()) {
      log.debug("API permission cache expired or empty");
      return null;
    }
    List<ApiPermissionDto> cached = cache.get(CACHE_KEY);
    return cached != null ? Collections.unmodifiableList(cached) : null;
  }

  /**
   * Put permissions into cache with current timestamp.
   *
   * @param permissions list of permissions to cache
   */
  public void put(List<ApiPermissionDto> permissions) {
    if (permissions == null) {
      return;
    }
    cache.put(CACHE_KEY, List.copyOf(permissions));
    lastUpdatedAt.set(System.currentTimeMillis());
    log.debug("Cached {} API permissions", permissions.size());
  }

  /** Clear the cache and reset timestamp. */
  public void clear() {
    cache.clear();
    lastUpdatedAt.set(0);
    log.debug("API permission cache cleared");
  }

  /**
   * Check if cache is valid (not expired and not empty).
   *
   * @return true if cache is valid
   */
  public boolean isValid() {
    return !isCacheExpired() && cache.containsKey(CACHE_KEY);
  }

  /**
   * Check if cache is expired based on TTL.
   *
   * @return true if cache is expired
   */
  private boolean isCacheExpired() {
    long lastUpdate = lastUpdatedAt.get();
    if (lastUpdate == 0) {
      return true;
    }
    long elapsed = System.currentTimeMillis() - lastUpdate;
    return elapsed > cacheConfig.getTtlMillis();
  }
}
