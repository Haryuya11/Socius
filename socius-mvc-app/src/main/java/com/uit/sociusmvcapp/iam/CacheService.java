package com.uit.sociusmvcapp.iam;

import com.uit.sociusmvcapp.iam.dto.CacheClearResultDto;

/**
 * Service interface for cache management operations. Provides methods to clear authorization and
 * permission caches.
 */
public interface CacheService {

  /**
   * Clears all authorization-related caches including API permissions and user principal caches.
   *
   * @return CacheClearResultDto containing the result of cache clearing operation
   */
  CacheClearResultDto clearAuthorizationCaches();
}
