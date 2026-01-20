package com.uit.sociusmvcapp.iam.internal.persistence;

import com.uit.sociusmvcapp.iam.internal.domain.ApiPermission;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

/**
 * MyBatis Mapper interface for ApiPermission entity. Provides read-only operations to fetch API
 * permission mappings.
 */
@Mapper
public interface ApiPermissionMapper {

  /**
   * Find all active API permissions.
   *
   * @return list of all ApiPermission entities
   */
  List<ApiPermission> findAll();

  /**
   * Find API permission by HTTP method and URL pattern.
   *
   * @param httpMethod the HTTP method (GET, POST, PUT, DELETE, etc.)
   * @param urlPattern the URL pattern
   * @return the ApiPermission entity if found
   */
  ApiPermission findByMethodAndPattern(String httpMethod, String urlPattern);
}
