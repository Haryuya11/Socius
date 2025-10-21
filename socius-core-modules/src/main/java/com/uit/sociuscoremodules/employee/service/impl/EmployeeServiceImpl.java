package com.uit.sociuscoremodules.employee.service.impl;

import com.uit.sociuscoremodules.employee.dto.EmployeeDto;
import com.uit.sociuscoremodules.employee.repository.EmployeeRepository;
import com.uit.sociuscoremodules.employee.service.EmployeeService;
import com.uit.sociuscoremodules.shared.security.UserContentProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** Implementation of EmployeeService for employee-related operations. */
@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

  /** Repository for accessing employee data. */
  private final EmployeeRepository employeeRepository;

  /** Provider for user content. */
  private final UserContentProvider userContentProvider;

  /**
   * Get the profile of the currently logged-in employee.
   *
   * @return EmployeeDto representing the employee profile
   */
  @Override
  public EmployeeDto employeeProfile() {
    return userContentProvider.getUserContent();
  }
}
