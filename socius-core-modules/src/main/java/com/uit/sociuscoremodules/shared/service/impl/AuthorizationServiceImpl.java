package com.uit.sociuscoremodules.shared.service.impl;

import com.uit.sociuscoremodules.employee.dto.EmployeeDto;
import com.uit.sociuscoremodules.employee.enums.RoleEnums;
import com.uit.sociuscoremodules.employee.repository.EmployeeRepository;
import com.uit.sociuscoremodules.shared.service.AuthorizationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/** Implementation of AuthorizationService for user authorization. */
@Service
@Slf4j
@RequiredArgsConstructor
public class AuthorizationServiceImpl implements AuthorizationService {

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

    String roleCode = employeeDto.getRoleCode();
    if (!isValidCode(roleCode)) {
      throw new IllegalArgumentException("Invalid role code: " + roleCode);
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
    return RoleEnums.ADMIN.getRoleCode().equals(roleCode)
        || RoleEnums.STAFF.getRoleCode().equals(roleCode)
        || RoleEnums.CUSTOMER.getRoleCode().equals(roleCode);
  }
}
