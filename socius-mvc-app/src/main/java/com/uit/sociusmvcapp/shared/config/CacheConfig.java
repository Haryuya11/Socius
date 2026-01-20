package com.uit.sociusmvcapp.shared.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Spring Cache. Enables caching and configures a ConcurrentMapCacheManager for
 * lightweight in-memory caching.
 */
@Configuration
@EnableCaching
public class CacheConfig {

  /**
   * Creates a CacheManager using ConcurrentHashMap for lightweight caching. Suitable for single
   * instance deployments with memory constraints (~400MB VM).
   *
   * @return the CacheManager
   */
  @Bean
  public CacheManager cacheManager() {
    return new ConcurrentMapCacheManager("user-principal");
  }
}
