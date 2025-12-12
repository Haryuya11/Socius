package com.uit.sociuscoremodules.employee.service;

import com.uit.sociuscoremodules.employee.dto.EmployeeDto;
import com.uit.sociuscoremodules.employee.dto.EmployeeProfileDto;
import com.uit.sociuscoremodules.employee.dto.SearchEmployeeDto;
import com.uit.sociuscoremodules.employee.request.ChangePasswordRequest;
import com.uit.sociuscoremodules.employee.request.EmployeeCreateRequest;
import com.uit.sociuscoremodules.employee.request.SearchUserRequest;
import com.uit.sociuscoremodules.shared.request.PaginationSearchRequest;
import com.uit.sociuscoremodules.shared.response.PageResponse;
import java.util.Map;

/** Service interface for Employee-related operations. */
public interface EmployeeService {

  /**
   * Get the profile of the currently logged-in employee.
   *
   * @return EmployeeDto representing the employee profile
   */
  EmployeeProfileDto employeeProfile();

  /**
   * Create a new user profile.
   *
   * @param request the request containing user creation details
   */
  Map<String, String> createUserProfile(EmployeeCreateRequest request);

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
   * @param request the request containing password change details
   */
  void changeUserPassword(ChangePasswordRequest request);

  /**
   * Search for employees based on given criteria with pagination.
   *
   * @param request the pagination search request containing search criteria
   * @return a paginated response of EmployeeDto matching the search criteria
   */
  PageResponse<SearchEmployeeDto> search(PaginationSearchRequest<SearchUserRequest> request);

  /**
   * Find an employee by ID.
   *
   * @param id the employee ID
   * @return the corresponding EmployeeDto
   */
  EmployeeDto findById(String id);
}
