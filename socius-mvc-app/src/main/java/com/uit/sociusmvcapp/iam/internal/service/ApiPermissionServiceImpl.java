package com.uit.sociusmvcapp.iam.internal.service;

import com.uit.sociusmvcapp.iam.ApiPermissionService;
import com.uit.sociusmvcapp.iam.internal.dto.ApiPermissionDto;
import com.uit.sociusmvcapp.iam.internal.repository.ApiPermissionRepository;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

/**
 * Implementation of ApiPermissionService. Provides caching and pattern matching for API
 * permissions.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ApiPermissionServiceImpl implements ApiPermissionService {

  private final ApiPermissionRepository repository;
  private final AntPathMatcher pathMatcher = new AntPathMatcher();

  /** Cached list of API permissions. Thread-safe for concurrent reads. */
  private final List<ApiPermissionDto> cachedPermissions = new CopyOnWriteArrayList<>();

  /** Initialize cache on startup. */
  @PostConstruct
  public void init() {
    refreshCache();
  }

  @Override
  public List<ApiPermissionDto> findAll() {
    if (cachedPermissions.isEmpty()) {
      refreshCache();
    }
    return List.copyOf(cachedPermissions);
  }

  @Override
  public ApiPermissionDto matchRequest(String httpMethod, String requestUri) {
    if (cachedPermissions.isEmpty()) {
      refreshCache();
    }

    for (ApiPermissionDto permission : cachedPermissions) {
      if (httpMethod.equalsIgnoreCase(permission.getHttpMethod())
          && pathMatcher.match(permission.getUrlPattern(), requestUri)) {
        log.debug(
            "Matched request [{} {}] to permission [{}]",
            httpMethod,
            requestUri,
            permission.getPermissionCode());
        return permission;
      }
    }

    log.debug("No permission mapping found for [{} {}]", httpMethod, requestUri);
    return null;
  }

  @Override
  public synchronized void refreshCache() {
    log.info("Refreshing API permissions cache");
    List<ApiPermissionDto> permissions = repository.findAll();
    cachedPermissions.clear();
    cachedPermissions.addAll(permissions);
    log.info("Loaded {} API permissions into cache", cachedPermissions.size());
  }
}
