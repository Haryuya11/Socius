package com.uit.sociusmvcapp.iam.internal.repository;

import com.uit.sociusmvcapp.iam.internal.converter.ApiPermissionConverter;
import com.uit.sociusmvcapp.iam.internal.dto.ApiPermissionDto;
import com.uit.sociusmvcapp.iam.internal.persistence.ApiPermissionMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/** Repository for ApiPermission entity. */
@Repository
@RequiredArgsConstructor
public class ApiPermissionRepository {

  private final ApiPermissionMapper mapper;
  private final ApiPermissionConverter converter;

  /**
   * Find all active API permissions.
   *
   * @return list of all ApiPermissionDto
   */
  public List<ApiPermissionDto> findAll() {
    return converter.entitiesToDtos(mapper.findAll());
  }

  /**
   * Find API permission by HTTP method and URL pattern.
   *
   * @param httpMethod the HTTP method
   * @param urlPattern the URL pattern
   * @return the ApiPermissionDto if found, null otherwise
   */
  public ApiPermissionDto findByMethodAndPattern(String httpMethod, String urlPattern) {
    return converter.entityToDto(mapper.findByMethodAndPattern(httpMethod, urlPattern));
  }
}
