package com.uit.sociusmvcapp.endpoint;

import com.uit.sociuscoremodules.employee.dto.EmployeeDto;
import com.uit.sociuscoremodules.employee.request.ChangePasswordRequest;
import com.uit.sociuscoremodules.employee.request.EmployeeCreateRequest;
import com.uit.sociuscoremodules.employee.service.EmployeeService;
import com.uit.sociuscoremodules.shared.constants.MessageConstant;
import com.uit.sociuscoremodules.shared.response.Response;
import com.uit.sociuscoremodules.shared.service.I18nService;
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
  public ResponseEntity<Response> getEmployeeProfile() {
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
  public ResponseEntity<Response> createEmployee(@RequestBody EmployeeCreateRequest request) {
    employeeService.createUserProfile(request);
    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.CREATED.value())
            .code(MessageConstant.S_EMP_001)
            .message(i18nService.getMessage(MessageConstant.S_EMP_001))
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
  public ResponseEntity<Response> updateEmployee(
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
  public ResponseEntity<Response> deactivateEmployee(@PathVariable String clientId) {
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
  public ResponseEntity<Response> changeEmployeePassword(
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
}
