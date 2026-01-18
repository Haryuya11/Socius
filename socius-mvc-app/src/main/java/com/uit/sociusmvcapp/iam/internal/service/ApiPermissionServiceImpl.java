package com.uit.sociusmvcapp.iam.internal.service;

import com.uit.sociusmvcapp.iam.ApiPermissionService;
import com.uit.sociusmvcapp.iam.internal.component.ApiPermissionCache;
import com.uit.sociusmvcapp.iam.internal.dto.ApiPermissionDto;
import com.uit.sociusmvcapp.iam.internal.repository.ApiPermissionRepository;
import jakarta.annotation.PostConstruct;
import java.util.List;
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
  private final ApiPermissionCache permissionCache;
  private final AntPathMatcher pathMatcher = new AntPathMatcher();

  /** Initialize cache on startup. */
  @PostConstruct
  public void init() {
    refreshCache();
  }

  @Override
  public List<ApiPermissionDto> findAll() {
    List<ApiPermissionDto> cached = permissionCache.get();
    if (cached == null) {
      refreshCache();
      cached = permissionCache.get();
    }
    return cached != null ? cached : List.of();
  }

  @Override
  public ApiPermissionDto matchRequest(String httpMethod, String requestUri) {
    List<ApiPermissionDto> permissions = findAll();

    for (ApiPermissionDto permission : permissions) {
      if (isMethodMatch(httpMethod, permission.getHttpMethod())
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
  public void refreshCache() {
    log.info("Refreshing API permissions cache");
    List<ApiPermissionDto> permissions = repository.findAll();
    permissionCache.put(permissions);
    log.info("Loaded {} API permissions into cache", permissions.size());
  }

  /**
   * Check if HTTP methods match (case-insensitive).
   *
   * @param requestMethod the request HTTP method
   * @param permissionMethod the permission HTTP method
   * @return true if methods match
   */
  private boolean isMethodMatch(String requestMethod, String permissionMethod) {
    if (requestMethod == null || permissionMethod == null) {
      return false;
    }
    return requestMethod.equalsIgnoreCase(permissionMethod);
  }
}
