package com.uit.sociuscoremodules.employee.service;

import com.uit.sociuscoremodules.employee.dto.EmployeeDto;
import com.uit.sociuscoremodules.employee.request.ChangePasswordRequest;
import com.uit.sociuscoremodules.employee.request.EmployeeCreateRequest;

/** Service interface for Employee-related operations. */
public interface EmployeeService {

  /**
   * Get the profile of the currently logged-in employee.
   *
   * @return EmployeeDto representing the employee profile
   */
  EmployeeDto employeeProfile();

  /**
   * Create a new user profile.
   *
   * @param request the request containing user creation details
   */
  void createUserProfile(EmployeeCreateRequest request);

  /**
   * Update an existing user profile.
   *
   * @param request the request containing user update details
   * @param clientId the client ID of the user to be updated
   */
  void updateUserProfile(EmployeeCreateRequest request, String clientId);

  /**
   * Deactivate a user profile.
   *
   * @param clientId the client ID of the user to be deactivated
   */
  void deactivateUserProfile(String clientId);

  /**
   * Change the password of a user.
   *
   * @param clientId the client ID of the user whose password is to be changed
   * @param request the request containing password change details
   */
  void changeUserPassword(String clientId, ChangePasswordRequest request);
}
