package com.uit.sociusmvcapp.iam.internal.service;

import com.uit.sociusmvcapp.iam.ApiPermissionService;
import com.uit.sociusmvcapp.iam.internal.component.ApiPermissionCache;
import com.uit.sociusmvcapp.iam.internal.dto.ApiPermissionDto;
import com.uit.sociusmvcapp.iam.internal.repository.ApiPermissionRepository;
import jakarta.annotation.PostConstruct;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

/**
 * Implementation of ApiPermissionService. Provides caching and pattern matching for API
 * permissions. Uses "best match" algorithm - the most specific pattern wins over more general ones.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ApiPermissionServiceImpl implements ApiPermissionService {

  private final ApiPermissionRepository repository;
  private final ApiPermissionCache permissionCache;
  private final AntPathMatcher pathMatcher = new AntPathMatcher();

  /** API context path prefix (e.g., "/api"). */
  @Value("${server.servlet.context-path:}")
  private String contextPath;

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

    // Strip context path prefix from request URI for matching
    String normalizedUri = normalizeUri(requestUri);

    // Use best-match algorithm: find the most specific pattern that matches
    // AntPathMatcher.getPatternComparator returns a comparator where more specific patterns
    // come first (have lower sort order)
    Comparator<String> patternComparator = pathMatcher.getPatternComparator(normalizedUri);

    ApiPermissionDto bestMatch =
        permissions.stream()
            .filter(
                p ->
                    isMethodMatch(httpMethod, p.getHttpMethod())
                        && pathMatcher.match(p.getUrlPattern(), normalizedUri))
            .min((p1, p2) -> patternComparator.compare(p1.getUrlPattern(), p2.getUrlPattern()))
            .orElse(null);

    if (bestMatch != null) {
      log.debug(
          "Matched request [{} {}] to permission [{}] with pattern [{}]",
          httpMethod,
          normalizedUri,
          bestMatch.getPermissionCode(),
          bestMatch.getUrlPattern());
    } else {
      log.debug("No permission mapping found for [{} {}]", httpMethod, normalizedUri);
    }

    return bestMatch;
  }

  @Override
  public void refreshCache() {
    log.info("Refreshing API permissions cache");
    List<ApiPermissionDto> permissions = repository.findAll();
    permissionCache.put(permissions);
    log.info("Loaded {} API permissions into cache", permissions.size());
  }

  /**
   * Normalize the request URI by removing the context path prefix.
   *
   * @param requestUri the full request URI
   * @return the normalized URI without context path
   */
  private String normalizeUri(String requestUri) {
    if (contextPath != null && !contextPath.isEmpty() && requestUri.startsWith(contextPath)) {
      return requestUri.substring(contextPath.length());
    }
    return requestUri;
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
