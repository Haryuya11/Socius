package com.uit.sociusmvcapp.endpoint;

import com.uit.sociuscoremodules.department.dto.DepartmentDto;
import com.uit.sociuscoremodules.department.dto.DepartmentEmployeesDto;
import com.uit.sociuscoremodules.department.request.DepartmentCreateRequest;
import com.uit.sociuscoremodules.department.request.EmployeeAddRequest;
import com.uit.sociuscoremodules.department.service.DepartmentService;
import com.uit.sociuscoremodules.employee.dto.EmployeeDto;
import com.uit.sociuscoremodules.shared.constants.MessageConstant;
import com.uit.sociuscoremodules.shared.response.Response;
import com.uit.sociuscoremodules.shared.service.I18nService;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** DepartmentController handles HTTP requests related to department operations. */
@RestController
@RequiredArgsConstructor
@RequestMapping("/departments")
public class DepartmentController {
  /** I18nService for internationalization messages. */
  private final I18nService i18nService;

  /** DepartmentService for department-related operations. */
  private final DepartmentService departmentService;

  /**
   * Get department information by department code.
   *
   * @return ResponseEntity containing the department information
   */
  @RequestMapping("/{departmentCode}/info")
  public ResponseEntity<Response> getDepartmentInfo(@PathVariable String departmentCode) {
    DepartmentDto department = departmentService.departmentInfo(departmentCode);

    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code(MessageConstant.S_DEP_003)
            .message(i18nService.getMessage(MessageConstant.S_DEP_003))
            .data(department)
            .build();

    return ResponseEntity.ok(response);
  }

  /**
   * Create a new department.
   *
   * @param request the request containing department creation details
   * @return ResponseEntity containing the created department code
   */
  @RequestMapping("/create")
  public ResponseEntity<Response> createDepartment(@RequestBody DepartmentCreateRequest request) {
    Map<String, String> data = departmentService.createDepartment(request);
    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code(MessageConstant.S_DEP_001)
            .message(i18nService.getMessage(MessageConstant.S_DEP_001))
            .data(data)
            .build();
    return ResponseEntity.ok(response);
  }

  /**
   * Update an existing department.
   *
   * @param request the request containing department update details
   * @param departmentCode the code of the department to update
   * @return ResponseEntity containing the updated department code
   */
  @RequestMapping("/{departmentCode}/update")
  public ResponseEntity<Response> updateDepartment(
      @RequestBody DepartmentCreateRequest request, @PathVariable String departmentCode) {
    Map<String, String> data = departmentService.updateDepartment(request, departmentCode);
    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code(MessageConstant.S_DEP_002)
            .message(i18nService.getMessage(MessageConstant.S_DEP_002))
            .data(data)
            .build();
    return ResponseEntity.ok(response);
  }

  /**
   * Deactivate a department.
   *
   * @param departmentCode the code of the department to deactivate
   * @return ResponseEntity indicating the result of the operation
   */
  @RequestMapping("/{departmentCode}/deactivate")
  public ResponseEntity<Response> deactivateDepartment(@PathVariable String departmentCode) {
    Map<String, String> data = departmentService.deactivateDepartment(departmentCode);

    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code(MessageConstant.S_DEP_004)
            .message(i18nService.getMessage(MessageConstant.S_DEP_004))
            .data(data)
            .build();
    return ResponseEntity.ok(response);
  }

  /**
   * Activate a department.
   *
   * @param departmentCode the code of the department to activate
   * @return ResponseEntity indicating the result of the operation
   */
  @RequestMapping("/{departmentCode}/activate")
  public ResponseEntity<Response> activateDepartment(@PathVariable String departmentCode) {
    Map<String, String> data = departmentService.activateDepartment(departmentCode);

    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code(MessageConstant.S_DEP_002)
            .message(i18nService.getMessage(MessageConstant.S_DEP_002))
            .data(data)
            .build();
    return ResponseEntity.ok(response);
  }

  /**
   * Get all departments.
   *
   * @return ResponseEntity containing the list of all departments
   */
  @RequestMapping("/list")
  public ResponseEntity<Response> getAllDepartments() {
    List<DepartmentDto> departments = departmentService.getAllDepartments();

    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code(MessageConstant.S_DEP_005)
            .message(i18nService.getMessage(MessageConstant.S_DEP_005))
            .data(departments)
            .build();
    return ResponseEntity.ok(response);
  }

  /**
   * Get all employees in a department by department code.
   *
   * @param departmentCode the code of the department
   * @return ResponseEntity containing the list of employees in the department
   */
  @RequestMapping("/{departmentCode}/employees")
  public ResponseEntity<Response> getEmployeesByDepartment(@PathVariable String departmentCode) {
    List<DepartmentEmployeesDto> employees =
        departmentService.getEmployeesByDepartmentCode(departmentCode);

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
   * Add an employee to a department.
   *
   * @param departmentCode the code of the department
   * @param payload the request payload containing employee addition details
   * @return ResponseEntity containing the added employee information
   */
  @RequestMapping("/{departmentCode}/add-employee")
  public ResponseEntity<Response> addEmployeeToDepartment(
      @PathVariable String departmentCode, @RequestBody EmployeeAddRequest payload) {
    EmployeeDto employee = departmentService.addEmployeeToDepartment(payload, departmentCode);

    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code(MessageConstant.S_DEP_002)
            .message(i18nService.getMessage(MessageConstant.S_DEP_002))
            .data(employee)
            .build();
    return ResponseEntity.ok(response);
  }

  /**
   * Remove an employee from a department.
   *
   * @param departmentCode the code of the department
   * @param payload the request payload containing the employee ID
   * @return ResponseEntity containing the removed employee information
   */
  @RequestMapping("/{departmentCode}/remove-employee")
  public ResponseEntity<Response> removeEmployeeFromDepartment(
      @PathVariable String departmentCode, @RequestBody Map<String, String> payload) {

    String employeeId = payload.get("employeeId");
    EmployeeDto employee =
        departmentService.removeEmployeeFromDepartment(departmentCode, employeeId);

    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code(MessageConstant.S_DEP_002)
            .message(i18nService.getMessage(MessageConstant.S_DEP_002))
            .data(employee)
            .build();
    return ResponseEntity.ok(response);
  }

  /**
   * Transfer an employee from one department to another.
   *
   * @param payload the request payload containing fromDepartmentCode and toDepartmentCode
   * @param request the request containing employee addition details
   * @return ResponseEntity indicating the result of the transfer operation
   */
  @RequestMapping("/transfer-employee")
  public ResponseEntity<Response> transferEmployee(
      @RequestBody Map<String, String> payload, EmployeeAddRequest request) {
    String fromDepartmentCode = payload.get("fromDepartmentCode");
    String toDepartmentCode = payload.get("toDepartmentCode");

    Map<String, String> result =
        departmentService.transferEmployee(fromDepartmentCode, request, toDepartmentCode);

    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code(MessageConstant.S_DEP_002)
            .message(i18nService.getMessage(MessageConstant.S_DEP_002))
            .data(result)
            .build();
    return ResponseEntity.ok(response);
  }
}
