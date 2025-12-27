package com.uit.sociusmvcapp.iam.internal.repository;

import com.uit.sociusmvcapp.iam.dto.RoleDto;
import com.uit.sociusmvcapp.iam.internal.converter.RoleConverter;
import com.uit.sociusmvcapp.iam.internal.persistence.RoleMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/** Repository for Role entity. */
@Repository
@RequiredArgsConstructor
public class RoleRepository {

  /** MyBatis Mapper for Role entity. */
  private final RoleMapper mapper;

  /** Singleton instance of RoleConverter. */
  private final RoleConverter converter;

  /**
   * Find a role by its code.
   *
   * @param roleCode the role code
   * @return the RoleDto
   */
  public RoleDto findByRoleCode(String roleCode) {
    return converter.entityToDto(mapper.findByRoleCode(roleCode));
  }

  /**
   * Find all roles.
   *
   * @return list of all RoleDtos
   */
  public List<RoleDto> findAll() {
    return converter.entitiesToDtos(mapper.findAll());
  }

  /**
   * Find roles by type.
   *
   * @param roleType the role type
   * @return list of RoleDtos matching the type
   */
  public List<RoleDto> findByRoleType(String roleType) {
    return converter.entitiesToDtos(mapper.findByRoleType(roleType));
  }
}
