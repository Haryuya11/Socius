package com.uit.sociusmvcapp.shared.config;

import java.util.concurrent.TimeUnit;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for lightweight TTL-based cache. Designed for low memory footprint (~400MB VM
 * constraint). Can be used by different modules for caching small, frequently-accessed data.
 */
@Getter
@Configuration
public class TtlCacheConfig {

  /** Default cache TTL in minutes. */
  @Value("${app.cache.default-ttl-minutes:30}")
  private long defaultTtlMinutes;

  /**
   * Get default cache TTL in milliseconds.
   *
   * @return TTL in milliseconds
   */
  public long getDefaultTtlMillis() {
    return TimeUnit.MINUTES.toMillis(defaultTtlMinutes);
  }
}
