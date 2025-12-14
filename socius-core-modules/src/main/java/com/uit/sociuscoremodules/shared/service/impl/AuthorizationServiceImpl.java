package com.uit.sociuscoremodules.shared.service.impl;

import com.uit.sociuscoremodules.employee.converter.EmployeeConverter;
import com.uit.sociuscoremodules.employee.dto.EmployeeDto;
import com.uit.sociuscoremodules.employee.dto.EmployeeProfileDto;
import com.uit.sociuscoremodules.employee.repository.EmployeeRepository;
import com.uit.sociuscoremodules.role.dto.ScopedPermissionDto;
import com.uit.sociuscoremodules.role.enums.SystemRoleEnums;
import com.uit.sociuscoremodules.role.repository.RoleRepository;
import com.uit.sociuscoremodules.shared.service.AuthorizationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/** Implementation of AuthorizationService for user authorization. */
@Service
@Slf4j
@RequiredArgsConstructor
public class AuthorizationServiceImpl extends BaseServiceImpl implements AuthorizationService {

  /** Repository for accessing employee data. */
  private final EmployeeRepository employeeRepository;

  /** Repository for accessing role data. */
  private final RoleRepository roleRepository;

  /** Converter for transforming Employee entities to DTOs. */
  private final EmployeeConverter employeeConverter;

  /**
   * Authorize user by employeeId.
   *
   * @param employeeId the user id
   * @return EmployeeDto if authorized, null otherwise
   */
  @Override
  public EmployeeProfileDto authorize(String employeeId) {
    EmployeeDto employee = employeeRepository.findByClientId(employeeId);

    if (employee == null) {
      log.warn("Employee not found for userId: {}", employeeId);
      throw notFound("Employee not found for clientId: " + employeeId);
    }

    String roleCode = employee.getSystemRole();
    if (!isValidCode(roleCode)) {
      throw badRequest("Invalid role code: " + roleCode);
    }

    EmployeeProfileDto profile = employeeConverter.dtoToProfileDto(employee);

    List<ScopedPermissionDto> permissionDtos =
        roleRepository.findPermissionsByClientIdGrouped(employeeId);

    profile.setPermissions(permissionDtos);

    return profile;
  }

  /**
   * Check if the role code is valid.
   *
   * @param roleCode the role code
   * @return true if valid, false otherwise
   */
  private boolean isValidCode(String roleCode) {
    return SystemRoleEnums.SYS_ADMIN.getRoleCode().equals(roleCode)
        || SystemRoleEnums.USER.getRoleCode().equals(roleCode);
  }
}
