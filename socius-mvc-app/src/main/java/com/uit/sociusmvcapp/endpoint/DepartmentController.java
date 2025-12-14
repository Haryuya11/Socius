package com.uit.sociusmvcapp.endpoint;

import com.uit.sociuscoremodules.department.dto.DepartmentDto;
import com.uit.sociuscoremodules.department.dto.DepartmentEmployeeBatchResultDto;
import com.uit.sociuscoremodules.department.dto.DepartmentEmployeesDto;
import com.uit.sociuscoremodules.department.request.DepartmentCreateRequest;
import com.uit.sociuscoremodules.department.request.EmployeeAddManyRequest;
import com.uit.sociuscoremodules.department.request.EmployeeAddRequest;
import com.uit.sociuscoremodules.department.request.TransferEmployeeRequest;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
  @GetMapping("/{departmentCode}")
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
  @PostMapping("/")
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
  @PutMapping("/{departmentCode}")
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
  @DeleteMapping("/{departmentCode}")
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
   * Get all departments.
   *
   * @return ResponseEntity containing the list of all departments
   */
  @GetMapping("/list")
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
  @GetMapping("/{departmentCode}/employees")
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
   * @param request the request payload containing employee addition details
   * @return ResponseEntity containing the added employee information
   */
  @PostMapping("/{departmentCode}/employees/{employeeId}")
  public ResponseEntity<Response> addEmployeeToDepartment(
      @PathVariable String departmentCode,
      @RequestBody EmployeeAddRequest request,
      @PathVariable String employeeId) {
    EmployeeDto employee =
        departmentService.addEmployeeToDepartment(request, departmentCode, employeeId);

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
   * Add multiple employees to a department.
   *
   * @param departmentCode the code of the department
   * @param requests the list of request payloads containing employee addition details
   * @return ResponseEntity containing the added employees information
   */
  @PostMapping("/{departmentCode}/employees")
  public ResponseEntity<Response> addMultipleEmployeesToDepartment(
      @PathVariable String departmentCode, @RequestBody List<EmployeeAddManyRequest> requests) {
    DepartmentEmployeeBatchResultDto employees =
        departmentService.addEmployeesToDepartmentBatch(requests, departmentCode);

    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code(MessageConstant.S_DEP_002)
            .message(i18nService.getMessage(MessageConstant.S_DEP_002))
            .data(employees)
            .build();
    return ResponseEntity.ok(response);
  }

  /**
   * Remove an employee from a department.
   *
   * @param departmentCode the code of the department
   * @param employeeId the ID of the employee to remove
   * @return ResponseEntity containing the removed employee information
   */
  @DeleteMapping("/{departmentCode}/employees/{employeeId}")
  public ResponseEntity<Response> removeEmployeeFromDepartment(
      @PathVariable String departmentCode, @PathVariable String employeeId) {

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
   * Remove multiple employees from a department.
   *
   * @param departmentCode the code of the department
   * @param employeeIds the list of employee IDs to remove
   * @return ResponseEntity containing the removed employees information
   */
  @DeleteMapping("/{departmentCode}/employees")
  public ResponseEntity<Response> removeMultipleEmployeesFromDepartment(
      @PathVariable String departmentCode, @RequestBody List<String> employeeIds) {

    DepartmentEmployeeBatchResultDto employees =
        departmentService.removeEmployeesFromDepartmentBatch(employeeIds, departmentCode);

    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code(MessageConstant.S_DEP_002)
            .message(i18nService.getMessage(MessageConstant.S_DEP_002))
            .data(employees)
            .build();
    return ResponseEntity.ok(response);
  }

  /**
   * Transfer an employee between departments.
   *
   * @param request the request containing transfer details
   * @return ResponseEntity indicating the result of the transfer operation
   */
  @PostMapping("/transfer")
  public ResponseEntity<Response> transferEmployee(@RequestBody TransferEmployeeRequest request) {

    Map<String, String> result = departmentService.transferEmployee(request);

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

  /**
   * Change an employee's role within a department.
   *
   * @param departmentCode the code of the department
   * @param employeeId the ID of the employee whose role is to be changed
   * @param roleCode the new role code to assign to the employee
   * @return ResponseEntity indicating the result of the role change operation
   */
  @PutMapping("/{departmentCode}/employees/{employeeId}/role/{roleCode}")
  public ResponseEntity<Response> changeEmployeeRole(
      @PathVariable String departmentCode,
      @PathVariable String employeeId,
      @PathVariable String roleCode) {
    Map<String, String> result =
        departmentService.changeEmployeeRoleInDepartment(departmentCode, employeeId, roleCode);

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
