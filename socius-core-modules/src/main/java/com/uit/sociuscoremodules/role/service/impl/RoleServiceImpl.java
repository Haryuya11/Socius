package com.uit.sociuscoremodules.role.service.impl;

import com.uit.sociuscoremodules.role.dto.RoleDto;
import com.uit.sociuscoremodules.role.repository.RoleRepository;
import com.uit.sociuscoremodules.role.service.RoleService;
import com.uit.sociuscoremodules.shared.service.impl.BaseServiceImpl;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/** Implementation of RoleService for role-related operations. */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl extends BaseServiceImpl implements RoleService {

  /** Repository for accessing role data. */
  private final RoleRepository roleRepository;

  /**
   * Find a role by its code.
   *
   * @param roleCode the role code
   * @return the RoleDto
   */
  @Override
  public RoleDto findByRoleCode(String roleCode) {
    return roleRepository.findByRoleCode(roleCode);
  }

  /**
   * Find all roles.
   *
   * @return list of all RoleDtos
   */
  @Override
  public List<RoleDto> findAll() {
    return roleRepository.findAll();
  }

  /**
   * Find roles by type.
   *
   * @param roleType the role type
   * @return list of RoleDtos matching the type
   */
  @Override
  public List<RoleDto> findByRoleType(String roleType) {
    return roleRepository.findByRoleType(roleType);
  }
}
