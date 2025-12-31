package com.uit.sociusmvcapp.iam.internal.repository;

import com.uit.sociusmvcapp.iam.internal.converter.RolePermissionConverter;
import com.uit.sociusmvcapp.iam.internal.dto.RolePermissionDto;
import com.uit.sociusmvcapp.iam.internal.persistence.RolePermissionMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/** Repository for RolePermission entity. */
@Repository
@RequiredArgsConstructor
public class RolePermissionRepository {
  private final RolePermissionMapper mapper;

  private final RolePermissionConverter converter;

  /**
   * Find permissions associated with given role codes.
   *
   * @param roleCodes list of role codes
   * @return list of RolePermissionDto containing permissions for each role code
   */
  public List<RolePermissionDto> findPermissionsByRoleCodes(List<String> roleCodes) {
    return converter.entitiesToDtos(mapper.findPermissionsByRoleCodes(roleCodes));
  }
}
