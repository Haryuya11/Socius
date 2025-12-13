package com.uit.sociuscoremodules.role.service;

import com.uit.sociuscoremodules.role.dto.RoleDto;
import java.util.List;

/**
 * Service interface for Role operations. All operations are read-only. Role modifications must be
 * done via database.
 */
public interface RoleService {

  /**
   * Find a role by its code.
   *
   * @param roleCode the role code
   * @return the RoleDto
   */
  RoleDto findByRoleCode(String roleCode);

  /**
   * Find all roles.
   *
   * @return list of all RoleDtos
   */
  List<RoleDto> findAll();

  /**
   * Find roles by type.
   *
   * @param roleType the role type
   * @return list of RoleDtos matching the type
   */
  List<RoleDto> findByRoleType(String roleType);
}
