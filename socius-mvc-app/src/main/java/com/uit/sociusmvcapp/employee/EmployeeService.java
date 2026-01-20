package com.uit.sociusmvcapp.employee;

import com.uit.sociusmvcapp.azure.blob.UploadFileDto;
import com.uit.sociusmvcapp.azure.graph.ChangePasswordRequest;
import com.uit.sociusmvcapp.employee.dto.EmployeeDto;
import com.uit.sociusmvcapp.employee.dto.SearchEmployeeDto;
import com.uit.sociusmvcapp.employee.dto.request.CreateEmployeeRequest;
import com.uit.sociusmvcapp.employee.dto.request.SearchUserRequest;
import com.uit.sociusmvcapp.employee.dto.request.UpdateEmployeeRequest;
import com.uit.sociusmvcapp.employee.dto.request.UpdateSalaryRequest;
import com.uit.sociusmvcapp.employee.dto.request.UpdateSystemRoleRequest;
import com.uit.sociusmvcapp.iam.dto.UserPrincipal;
import com.uit.sociusmvcapp.shared.request.PaginationSearchRequest;
import com.uit.sociusmvcapp.shared.response.PageResponse;
import java.util.List;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

/** Service interface for Employee-related operations. */
public interface EmployeeService {

  /**
   * Get the profile of the currently logged-in employee.
   *
   * @return EmployeeDto representing the employee profile
   */
  UserPrincipal employeeProfile();

  /**
   * Create a new user profile.
   *
   * @param request the request containing user creation details
   */
  Map<String, String> create(CreateEmployeeRequest request);

  /**
   * Update an existing user profile (excluding salary).
   *
   * <p>This method intentionally excludes salary updates to prevent mass assignment
   * vulnerabilities. Use updateSalary() for salary modifications with proper authorization.
   *
   * @param request the request containing user update details (excludes salary)
   * @param clientId the client ID of the user to be updated
   */
  void update(UpdateEmployeeRequest request, String clientId);

  /**
   * Update an employee's salary.
   *
   * <p>This is a separate endpoint requiring 'system.full' permission (SYS_ADMIN only) to prevent
   * unauthorized salary modifications through the general update endpoint.
   *
   * @param request the request containing the new salary value
   * @param clientId the client ID of the employee whose salary is being updated
   */
  void updateSalary(UpdateSalaryRequest request, String clientId);

  /**
   * Update an employee's system role.
   *
   * <p>This is a separate endpoint requiring 'system.full' permission (SYS_ADMIN only) as only
   * system administrators should be able to change user roles.
   *
   * @param request the request containing the new system role
   * @param clientId the client ID of the employee whose system role is being updated
   */
  void updateSystemRole(UpdateSystemRoleRequest request, String clientId);

  /**
   * Deactivate a user profile.
   *
   * @param clientId the client ID of the user to be deactivated
   */
  void deactivate(String clientId);

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
   * Find an employee by client ID. The salary field will be masked (set to -1) if the current user
   * does not have permission to view it.
   *
   * <p>Salary visibility rules:
   *
   * <ul>
   *   <li>User viewing their own profile: salary is visible
   *   <li>User with 'employee.view.salary' permission: salary is visible
   *   <li>Otherwise: salary is masked as -1
   * </ul>
   *
   * @param clientId the employee client ID
   * @return the corresponding EmployeeDto with salary masked if unauthorized
   */
  EmployeeDto findByClientId(String clientId);

  /**
   * Find multiple employees by their client IDs in a single batch query.
   *
   * @param clientIds list of employee client IDs
   * @return list of EmployeeDto matching the provided client IDs
   */
  List<EmployeeDto> findByClientIds(List<String> clientIds);

  /**
   * Assert that an employee exists by client ID.
   *
   * @param clientId the employee client ID
   */
  void validateExists(String clientId);

  /**
   * Upload an avatar file for the employee.
   *
   * @param file the avatar file to be uploaded
   * @return UploadFileDto containing details of the uploaded file
   */
  UploadFileDto uploadAvatar(MultipartFile file);

  /**
   * Get the full URL of an avatar given its path.
   *
   * @param path the path of the avatar
   * @return the full URL of the avatar
   */
  String getAvatarUrl(String path);
}
