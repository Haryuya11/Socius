package com.uit.sociuscoremodules.shared.service;

import com.uit.sociuscoremodules.employee.dto.EmployeeDto;

/** Service interface for user authorization. */
public interface AuthorizationService {

  /**
   * Authorize user by userId.
   *
   * @param userId the user id
   * @return the employee dto
   */
  EmployeeDto authorize(String userId);
}
