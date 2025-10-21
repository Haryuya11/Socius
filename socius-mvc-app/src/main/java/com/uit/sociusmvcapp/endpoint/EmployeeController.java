package com.uit.sociusmvcapp.endpoint;

import com.uit.sociuscoremodules.employee.dto.EmployeeDto;
import com.uit.sociuscoremodules.employee.service.EmployeeService;
import com.uit.sociuscoremodules.shared.response.Response;
import com.uit.sociuscoremodules.shared.service.I18nService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
            .code("SUCCESS")
            .message("Employee profile retrieved successfully")
            .data(employee)
            .build();
    return ResponseEntity.ok(response);
  }
}
