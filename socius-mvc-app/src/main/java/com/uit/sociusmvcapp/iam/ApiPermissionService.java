package com.uit.sociusmvcapp.iam;

import com.uit.sociusmvcapp.iam.internal.dto.ApiPermissionDto;
import java.util.List;

/**
 * Service interface for API permission operations. Provides methods to retrieve and match API
 * permissions for RBAC authorization.
 */
public interface ApiPermissionService {

  /**
   * Find all API permissions.
   *
   * @return list of all ApiPermissionDto
   */
  List<ApiPermissionDto> findAll();

  /**
   * Match an incoming request to an API permission.
   *
   * @param httpMethod the HTTP method of the request
   * @param requestUri the request URI
   * @return the matching ApiPermissionDto if found, null otherwise
   */
  ApiPermissionDto matchRequest(String httpMethod, String requestUri);

  /** Refresh the cached API permissions from the database. */
  void refreshCache();
}
