package com.uit.sociusmvcapp.endpoint;

import com.uit.sociuscoremodules.employee.dto.EmployeeDto;
import com.uit.sociuscoremodules.employee.dto.SearchEmployeeDto;
import com.uit.sociuscoremodules.employee.request.ChangePasswordRequest;
import com.uit.sociuscoremodules.employee.request.EmployeeCreateRequest;
import com.uit.sociuscoremodules.employee.request.SearchUserRequest;
import com.uit.sociuscoremodules.employee.service.EmployeeService;
import com.uit.sociuscoremodules.shared.constants.MessageConstant;
import com.uit.sociuscoremodules.shared.request.PaginationSearchRequest;
import com.uit.sociuscoremodules.shared.response.PageResponse;
import com.uit.sociuscoremodules.shared.response.Response;
import com.uit.sociuscoremodules.shared.service.I18nService;
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
import org.springframework.web.bind.annotation.RestController;

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
    EmployeeDto employee = employeeService.employeeProfile();
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
  public ResponseEntity<Response> create(@RequestBody EmployeeCreateRequest request) {
    Map<String, String> data = employeeService.createUserProfile(request);
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
   * Update an existing employee.
   *
   * @param request the employee update request
   * @param clientId the client ID of the employee to update
   * @return ResponseEntity indicating the result of the operation
   */
  @PutMapping("/{clientId}")
  public ResponseEntity<Response> update(
      @RequestBody EmployeeCreateRequest request, @PathVariable String clientId) {
    employeeService.updateUserProfile(request, clientId);
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
    employeeService.deactivateUserProfile(clientId);
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
   * @param clientId the client ID of the employee whose password is to be changed
   * @return ResponseEntity indicating the result of the operation
   */
  @PutMapping("/change-password/{clientId}")
  public ResponseEntity<Response> changePassword(
      @RequestBody ChangePasswordRequest request, @PathVariable String clientId) {
    employeeService.changeUserPassword(clientId, request);

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
    EmployeeDto employee = employeeService.findById(clientId);
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
  @GetMapping
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
}
