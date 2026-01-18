package com.uit.sociusmvcapp.workforce;

import com.uit.sociusmvcapp.shared.constants.MessageConstant;
import com.uit.sociusmvcapp.shared.response.Response;
import com.uit.sociusmvcapp.shared.service.I18nService;
import com.uit.sociusmvcapp.workforce.dto.DepartmentEmployeeBatchResultDto;
import com.uit.sociusmvcapp.workforce.dto.DepartmentEmployeeDto;
import com.uit.sociusmvcapp.workforce.dto.request.BulkAssignEmployeeRequest;
import com.uit.sociusmvcapp.workforce.dto.request.BulkRemoveEmployeeRequest;
import com.uit.sociusmvcapp.workforce.dto.request.TransferEmployeeRequest;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

/** REST controller for managing department-employee relationships. */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/departments")
public class DepartmentEmployeeController {

  private final DepartmentEmployeeService departmentEmployeeService;
  private final I18nService i18nService;

  /**
   * Add an employee to a department.
   *
   * @param departmentCode the code of the department
   * @param request the request payload containing employee addition details
   * @return ResponseEntity containing the added employee information
   */
  @PostMapping("/{departmentCode}/employees")
  public ResponseEntity<Response> addEmployeeToDepartment(
      @PathVariable String departmentCode, @RequestBody BulkAssignEmployeeRequest request) {
    DepartmentEmployeeBatchResultDto employees =
        departmentEmployeeService.addEmployeesToDepartment(request.getEmployees(), departmentCode);

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
   * Get all employees in a department by department code.
   *
   * @param departmentCode the code of the department
   * @return ResponseEntity containing the list of employees in the department
   */
  @GetMapping("/{departmentCode}/employees")
  public ResponseEntity<Response> getEmployeesByDepartment(@PathVariable String departmentCode) {
    List<DepartmentEmployeeDto> employees =
        departmentEmployeeService.getEmployeesByDepartmentCode(departmentCode);

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
   * Remove an employee from a department.
   *
   * @param departmentCode the code of the department
   * @param employeeId the ID of the employee to remove
   * @return ResponseEntity containing the removed employee information
   */
  @DeleteMapping("/{departmentCode}/employees/{employeeId}")
  public ResponseEntity<Response> removeEmployeeFromDepartment(
      @PathVariable String departmentCode, @PathVariable String employeeId) {

    departmentEmployeeService.removeEmployeeFromDepartment(departmentCode, employeeId);
    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code(MessageConstant.S_DEP_002)
            .message(i18nService.getMessage(MessageConstant.S_DEP_002))
            .build();
    return ResponseEntity.ok(response);
  }

  /**
   * Remove multiple employees from a department.
   *
   * @param departmentCode the code of the department
   * @param request the list of employee IDs to remove
   * @return ResponseEntity containing the removed employees information
   */
  @DeleteMapping("/{departmentCode}/employees")
  public ResponseEntity<Response> removeMultipleEmployeesFromDepartment(
      @PathVariable String departmentCode, @RequestBody BulkRemoveEmployeeRequest request) {

    DepartmentEmployeeBatchResultDto employees =
        departmentEmployeeService.removeEmployeesFromDepartmentBatch(
            request.getEmployeeIds(), departmentCode);

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

    Map<String, String> result = departmentEmployeeService.transferEmployee(request);

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
        departmentEmployeeService.changeEmployeeRoleInDepartment(
            departmentCode, employeeId, roleCode);

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
