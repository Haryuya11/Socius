package com.uit.sociusmvcapp.shared.component;

import com.uit.sociusmvcapp.shared.config.TtlCacheConfig;
import com.uit.sociusmvcapp.shared.constants.CommonConstant;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;

/**
 * Generic lightweight cache using ConcurrentHashMap with TTL. Designed for low memory footprint
 * (~400MB VM constraint). Can be extended by different modules for caching small,
 * frequently-accessed data.
 *
 * @param <T> the type of items to cache
 */
@Slf4j
public abstract class TtlCache<T> {

  private final TtlCacheConfig cacheConfig;
  private final String cacheName;

  /** Thread-safe cache storage. */
  private final ConcurrentHashMap<String, List<T>> cache = new ConcurrentHashMap<>();

  /** Timestamp when cache was last updated. */
  private final AtomicLong lastUpdatedAt = new AtomicLong(CommonConstant.UNINITIALIZED_TIMESTAMP);

  /** Lock for thread-safe cache refresh operations. */
  private final ReentrantLock refreshLock = new ReentrantLock();

  /**
   * Constructor for TtlCache.
   *
   * @param cacheConfig the cache configuration
   * @param cacheName the name of this cache for logging
   */
  protected TtlCache(TtlCacheConfig cacheConfig, String cacheName) {
    this.cacheConfig = cacheConfig;
    this.cacheName = cacheName;
  }

  /**
   * Get the cache key used for storing items.
   *
   * @return the cache key
   */
  protected abstract String getCacheKey();

  /**
   * Get cached items if cache is valid.
   *
   * @return list of cached items or null if cache is expired/empty
   */
  public List<T> get() {
    if (isCacheExpired()) {
      log.debug("{} cache expired or empty", cacheName);
      return null;
    }
    return cache.get(getCacheKey());
  }

  /**
   * Get cached items, or refresh from source if cache is invalid. This method is thread-safe and
   * ensures only one refresh operation happens at a time.
   *
   * @param dataLoader supplier function to load data when cache is invalid
   * @return list of cached items, never null (returns empty list if source returns null)
   */
  public List<T> getOrRefresh(Supplier<List<T>> dataLoader) {
    // First, try to get from cache without locking
    if (!isCacheExpired()) {
      List<T> cached = cache.get(getCacheKey());
      if (cached != null) {
        return cached;
      }
    }

    // Cache is invalid, need to refresh
    refreshLock.lock();
    try {
      // Double-check after acquiring lock (another thread may have refreshed)
      if (!isCacheExpired()) {
        List<T> cached = cache.get(getCacheKey());
        if (cached != null) {
          return cached;
        }
      }

      // Load data from source
      log.debug("Refreshing {} cache", cacheName);
      List<T> data = dataLoader.get();
      if (data != null) {
        put(data);
        return data;
      }
      return List.of();
    } finally {
      refreshLock.unlock();
    }
  }

  /**
   * Put items into cache with current timestamp.
   *
   * @param items list of items to cache
   */
  public void put(List<T> items) {
    if (items == null) {
      return;
    }
    cache.put(getCacheKey(), List.copyOf(items));
    lastUpdatedAt.set(System.currentTimeMillis());
    log.debug("Cached {} items in {} cache", items.size(), cacheName);
  }

  /** Clear the cache and reset timestamp. */
  public void clear() {
    cache.clear();
    lastUpdatedAt.set(CommonConstant.UNINITIALIZED_TIMESTAMP);
    log.debug("{} cache cleared", cacheName);
  }

  /**
   * Check if cache is valid (not expired and not empty).
   *
   * @return true if cache is valid
   */
  public boolean isValid() {
    return !isCacheExpired() && cache.containsKey(getCacheKey());
  }

  /**
   * Check if cache is expired based on TTL.
   *
   * @return true if cache is expired
   */
  private boolean isCacheExpired() {
    long lastUpdate = lastUpdatedAt.get();
    if (lastUpdate == CommonConstant.UNINITIALIZED_TIMESTAMP) {
      return true;
    }
    long elapsed = System.currentTimeMillis() - lastUpdate;
    return elapsed > cacheConfig.getDefaultTtlMillis();
  }
}
