package com.uit.sociuscoremodules.employee.service;

import com.uit.sociuscoremodules.employee.dto.EmployeeDto;

/** Service interface for Employee-related operations. */
public interface EmployeeService {

  /**
   * Get the profile of the currently logged-in employee.
   *
   * @return EmployeeDto representing the employee profile
   */
  EmployeeDto employeeProfile();
}
