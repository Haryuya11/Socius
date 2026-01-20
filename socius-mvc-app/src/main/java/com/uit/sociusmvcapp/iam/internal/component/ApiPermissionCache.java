package com.uit.sociusmvcapp.iam.internal.component;

import com.uit.sociusmvcapp.iam.internal.dto.ApiPermissionDto;
import com.uit.sociusmvcapp.shared.component.TtlCache;
import com.uit.sociusmvcapp.shared.config.TtlCacheConfig;
import org.springframework.stereotype.Component;

/**
 * Lightweight cache for API permissions using ConcurrentHashMap with TTL. Designed for low memory
 * footprint (~400MB VM constraint).
 */
@Component
public class ApiPermissionCache extends TtlCache<ApiPermissionDto> {

  private static final String CACHE_KEY = "api_permissions";
  private static final String CACHE_NAME = "ApiPermission";

  /**
   * Constructor for ApiPermissionCache.
   *
   * @param cacheConfig the cache configuration
   */
  public ApiPermissionCache(TtlCacheConfig cacheConfig) {
    super(cacheConfig, CACHE_NAME);
  }

  @Override
  protected String getCacheKey() {
    return CACHE_KEY;
  }
}
