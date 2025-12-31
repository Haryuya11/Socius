package com.uit.sociusmvcapp.employee;

import com.uit.sociusmvcapp.employee.dto.EmployeeDto;
import com.uit.sociusmvcapp.employee.dto.SearchEmployeeDto;
import com.uit.sociusmvcapp.employee.dto.UploadFileDto;
import com.uit.sociusmvcapp.employee.dto.request.ChangePasswordRequest;
import com.uit.sociusmvcapp.employee.dto.request.EmployeeCreateRequest;
import com.uit.sociusmvcapp.employee.dto.request.SearchUserRequest;
import com.uit.sociusmvcapp.iam.dto.UserPrincipal;
import com.uit.sociusmvcapp.shared.request.PaginationSearchRequest;
import com.uit.sociusmvcapp.shared.response.PageResponse;
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
  Map<String, String> create(EmployeeCreateRequest request);

  /**
   * Update an existing user profile.
   *
   * @param request the request containing user update details
   * @param clientId the client ID of the user to be updated
   */
  void update(EmployeeCreateRequest request, String clientId);

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
   * Find an employee by client ID.
   *
   * @param clientId the employee client ID
   * @return the corresponding EmployeeDto
   */
  EmployeeDto findByClientId(String clientId);

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
}
