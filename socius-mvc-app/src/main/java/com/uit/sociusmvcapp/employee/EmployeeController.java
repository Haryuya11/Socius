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
import com.uit.sociusmvcapp.shared.constants.MessageConstant;
import com.uit.sociusmvcapp.shared.request.PaginationSearchRequest;
import com.uit.sociusmvcapp.shared.response.PageResponse;
import com.uit.sociusmvcapp.shared.response.Response;
import com.uit.sociusmvcapp.shared.service.I18nService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/** EmployeeController handles HTTP requests related to employee operations. */
@RestController
@RequiredArgsConstructor
@RequestMapping("/employees")
public class EmployeeController {

  /** I18nService for internationalization messages. */
  private final I18nService i18nService;

  /** EmployeeService for employee-related operations. */
  private final EmployeeService employeeService;

  /**
   * Get the profile of the authenticated employee.
   *
   * @return ResponseEntity containing the employee profile
   */
  @GetMapping("/profile")
  public ResponseEntity<Response> getProfile() {
    UserPrincipal employee = employeeService.employeeProfile();
    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code(MessageConstant.S_EMP_004)
            .message(i18nService.getMessage(MessageConstant.S_EMP_004))
            .data(employee)
            .build();
    return ResponseEntity.ok(response);
  }

  /**
   * Create a new employee.
   *
   * @param request the employee creation request
   * @return ResponseEntity indicating the result of the operation
   */
  @PostMapping
  public ResponseEntity<Response> create(@RequestBody CreateEmployeeRequest request) {
    Map<String, String> data = employeeService.create(request);
    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.CREATED.value())
            .code(MessageConstant.S_EMP_001)
            .message(i18nService.getMessage(MessageConstant.S_EMP_001))
            .data(data)
            .build();
    return ResponseEntity.ok(response);
  }

  /**
   * Update an existing employee's profile (excluding salary).
   *
   * <p>This endpoint intentionally excludes salary updates to prevent mass assignment
   * vulnerabilities. Use PUT /employees/{clientId}/salary for salary modifications with proper
   * authorization.
   *
   * @param request the employee update request (excludes salary field)
   * @param clientId the client ID of the employee to update
   * @return ResponseEntity indicating the result of the operation
   */
  @PutMapping("/{clientId}")
  public ResponseEntity<Response> update(
      @RequestBody UpdateEmployeeRequest request, @PathVariable String clientId) {
    employeeService.update(request, clientId);
    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code(MessageConstant.S_EMP_002)
            .message(i18nService.getMessage(MessageConstant.S_EMP_002))
            .build();
    return ResponseEntity.ok(response);
  }

  /**
   * Update an employee's salary.
   *
   * <p>This is a separate endpoint requiring 'system.full' permission (SYS_ADMIN only) to prevent
   * unauthorized salary modifications through the general update endpoint.
   *
   * @param request the salary update request
   * @param clientId the client ID of the employee whose salary is being updated
   * @return ResponseEntity indicating the result of the operation
   */
  @PutMapping("/{clientId}/salary")
  public ResponseEntity<Response> updateSalary(
      @RequestBody UpdateSalaryRequest request, @PathVariable String clientId) {
    employeeService.updateSalary(request, clientId);
    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code(MessageConstant.S_EMP_002)
            .message(i18nService.getMessage(MessageConstant.S_EMP_002))
            .build();
    return ResponseEntity.ok(response);
  }

  /**
   * Update an employee's system role.
   *
   * <p>This is a separate endpoint requiring 'system.full' permission (SYS_ADMIN only) as only
   * system administrators should be able to change user roles.
   *
   * @param request the system role update request
   * @param clientId the client ID of the employee whose system role is being updated
   * @return ResponseEntity indicating the result of the operation
   */
  @PutMapping("/{clientId}/system-role")
  public ResponseEntity<Response> updateSystemRole(
      @RequestBody UpdateSystemRoleRequest request, @PathVariable String clientId) {
    employeeService.updateSystemRole(request, clientId);
    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code(MessageConstant.S_EMP_002)
            .message(i18nService.getMessage(MessageConstant.S_EMP_002))
            .build();
    return ResponseEntity.ok(response);
  }

  /**
   * Deactivate an employee.
   *
   * @param clientId the client ID of the employee to deactivate
   * @return ResponseEntity indicating the result of the operation
   */
  @DeleteMapping("/{clientId}")
  public ResponseEntity<Response> deactivate(@PathVariable String clientId) {
    employeeService.deactivate(clientId);
    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code(MessageConstant.S_EMP_003)
            .message(i18nService.getMessage(MessageConstant.S_EMP_003))
            .build();
    return ResponseEntity.ok(response);
  }

  /**
   * Change the password of an employee.
   *
   * @param request the password change request
   * @return ResponseEntity indicating the result of the operation
   */
  @PutMapping("/change-password")
  public ResponseEntity<Response> changePassword(@RequestBody ChangePasswordRequest request) {
    employeeService.changeUserPassword(request);

    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code(MessageConstant.S_EMP_005)
            .message(i18nService.getMessage(MessageConstant.S_EMP_005))
            .build();
    return ResponseEntity.ok(response);
  }

  /**
   * Find an employee by their client ID.
   *
   * @param clientId the client ID of the employee to find
   * @return ResponseEntity containing the employee data
   */
  @GetMapping("/{clientId}")
  public ResponseEntity<Response> findById(@PathVariable String clientId) {
    EmployeeDto employee = employeeService.findByClientId(clientId);
    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code(MessageConstant.S_EMP_006)
            .message(i18nService.getMessage(MessageConstant.S_EMP_006))
            .data(employee)
            .build();
    return ResponseEntity.ok(response);
  }

  /**
   * Search for employees with pagination.
   *
   * @param request the pagination search request containing search criteria
   * @return ResponseEntity containing paginated employee data
   */
  @PostMapping("/search")
  public ResponseEntity<Response> search(
      @RequestBody PaginationSearchRequest<SearchUserRequest> request) {
    PageResponse<SearchEmployeeDto> employees = employeeService.search(request);
    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code(MessageConstant.S_EMP_007)
            .message(i18nService.getMessage(MessageConstant.S_EMP_007))
            .data(employees)
            .build();
    return ResponseEntity.ok(response);
  }

  /**
   * Upload an avatar for the employee.
   *
   * @param file the avatar file to upload
   * @return ResponseEntity containing the upload file data
   */
  @PostMapping("/upload-avatar")
  public ResponseEntity<Response> uploadAvatar(@RequestParam("file") MultipartFile file) {
    UploadFileDto responseDto = employeeService.uploadAvatar(file);
    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code(MessageConstant.S_EMP_008)
            .message(i18nService.getMessage(MessageConstant.S_EMP_008))
            .data(responseDto)
            .build();
    return ResponseEntity.ok(response);
  }

  /**
   * Get the full URL of an avatar by its path.
   *
   * @param path the path of the avatar
   * @return ResponseEntity containing the full URL of the avatar
   */
  @GetMapping("/avatar-url")
  public ResponseEntity<Response> getAvatarUrl(@RequestParam("path") String path) {
    String url = employeeService.getAvatarUrl(path);
    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code(MessageConstant.S_EMP_009)
            .message(i18nService.getMessage(MessageConstant.S_EMP_009))
            .data(url)
            .build();
    return ResponseEntity.ok(response);
  }
}
