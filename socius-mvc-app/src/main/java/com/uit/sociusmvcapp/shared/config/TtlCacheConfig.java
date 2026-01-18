package com.uit.sociusmvcapp.shared.config;

import jakarta.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for lightweight TTL-based cache. Designed for low memory footprint (~400MB VM
 * constraint). Can be used by different modules for caching small, frequently-accessed data.
 */
@Slf4j
@Getter
@Configuration
public class TtlCacheConfig {

  /** Minimum allowed TTL in minutes. */
  private static final long MIN_TTL_MINUTES = 1L;

  /** Maximum allowed TTL in minutes (24 hours). */
  private static final long MAX_TTL_MINUTES = 1440L;

  /** Default cache TTL in minutes. */
  @Value("${app.cache.default-ttl-minutes:30}")
  private long defaultTtlMinutes;

  /** Validate TTL configuration on startup. */
  @PostConstruct
  public void validateConfig() {
    if (defaultTtlMinutes < MIN_TTL_MINUTES || defaultTtlMinutes > MAX_TTL_MINUTES) {
      log.warn(
          "Invalid cache TTL configuration: {} minutes. "
              + "Valid range is {}-{} minutes. Using default: 30 minutes.",
          defaultTtlMinutes,
          MIN_TTL_MINUTES,
          MAX_TTL_MINUTES);
      defaultTtlMinutes = 30L;
    }
    log.info("Cache TTL configured: {} minutes", defaultTtlMinutes);
  }

  /**
   * Get default cache TTL in milliseconds.
   *
   * @return TTL in milliseconds
   */
  public long getDefaultTtlMillis() {
    return TimeUnit.MINUTES.toMillis(defaultTtlMinutes);
  }
}
