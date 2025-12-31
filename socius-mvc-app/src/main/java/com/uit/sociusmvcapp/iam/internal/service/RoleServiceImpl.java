package com.uit.sociusmvcapp.iam.internal.service;

import com.uit.sociusmvcapp.iam.RoleService;
import com.uit.sociusmvcapp.iam.dto.RoleDto;
import com.uit.sociusmvcapp.iam.internal.repository.RoleRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/** Implementation of RoleService for role-related operations. */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

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
