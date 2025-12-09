package com.uit.sociuscoremodules.shared.service.impl;

import com.uit.sociuscoremodules.employee.dto.EmployeeDto;
import com.uit.sociuscoremodules.employee.enums.SystemRoleEnums;
import com.uit.sociuscoremodules.employee.repository.EmployeeRepository;
import com.uit.sociuscoremodules.shared.service.AuthorizationService;
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

  /**
   * Authorize user by employeeId.
   *
   * @param employeeId the user id
   * @return EmployeeDto if authorized, null otherwise
   */
  @Override
  public EmployeeDto authorize(String employeeId) {
    EmployeeDto employeeDto = employeeRepository.findByClientId(employeeId);
    if (employeeDto == null) {
      log.warn("Employee not found for userId: {}", employeeId);
      return null;
    }

    String roleCode = employeeDto.getSystemRole();
    if (!isValidCode(roleCode)) {
      throw badRequest("Invalid role code: " + roleCode);
    }

    return employeeDto;
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
