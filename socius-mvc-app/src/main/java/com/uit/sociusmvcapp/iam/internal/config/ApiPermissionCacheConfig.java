package com.uit.sociusmvcapp.iam.internal.config;

import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for API permission cache. Provides TTL-based cache settings for lightweight memory
 * usage.
 */
@Configuration
public class ApiPermissionCacheConfig {

  /** Cache time-to-live in minutes. Default is 30 minutes. */
  @Value("${app.cache.api-permission.ttl-minutes:30}")
  private long ttlMinutes;

  /**
   * Get cache TTL in milliseconds.
   *
   * @return TTL in milliseconds
   */
  public long getTtlMillis() {
    return TimeUnit.MINUTES.toMillis(ttlMinutes);
  }

  /**
   * Get cache TTL in minutes.
   *
   * @return TTL in minutes
   */
  public long getTtlMinutes() {
    return ttlMinutes;
  }
}
