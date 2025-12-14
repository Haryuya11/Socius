package com.uit.sociuscoremodules.role.persistence;

import com.uit.sociuscoremodules.role.domain.Role;
import com.uit.sociuscoremodules.role.dto.PermissionQueryDto;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * MyBatis Mapper interface for Role entity. Provides read-only operations. Create/Update/Delete
 * must be done via database.
 */
@Mapper
public interface RoleMapper {

  /**
   * Find a role by its code.
   *
   * @param roleCode the role code
   * @return the Role entity
   */
  Role findByRoleCode(@Param("roleCode") String roleCode);

  /**
   * Find all roles.
   *
   * @return list of all Role entities
   */
  List<Role> findAll();

  /**
   * Find roles by type.
   *
   * @param roleType the role type (SYSTEM, DEPARTMENT, TEAM)
   * @return list of Role entities matching the type
   */
  List<Role> findByRoleType(@Param("roleType") String roleType);

  /**
   * Retrieves raw permission data for a client.
   *
   * @param clientId the client ID
   * @return list of PermissionQueryDto containing raw permission data
   */
  List<PermissionQueryDto> findPermissionsByClientIdGrouped(@Param("clientId") String clientId);
}
