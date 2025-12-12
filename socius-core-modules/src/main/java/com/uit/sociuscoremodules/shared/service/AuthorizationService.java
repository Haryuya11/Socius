package com.uit.sociuscoremodules.shared.service;

import com.uit.sociuscoremodules.employee.dto.EmployeeProfileDto;

/** Service interface for user authorization. */
public interface AuthorizationService {

  /**
   * Authorize user by userId.
   *
   * @param userId the user id
   * @return the employee dto
   */
  EmployeeProfileDto authorize(String userId);
}
