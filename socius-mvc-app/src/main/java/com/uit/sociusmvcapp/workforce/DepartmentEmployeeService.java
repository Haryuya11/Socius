package com.uit.sociusmvcapp.workforce;

import com.uit.sociusmvcapp.workforce.dto.DepartmentEmployeeBatchResultDto;
import com.uit.sociusmvcapp.workforce.dto.DepartmentEmployeeDto;
import com.uit.sociusmvcapp.workforce.dto.request.AssignEmployeeToDepartmentRequest;
import com.uit.sociusmvcapp.workforce.dto.request.TransferEmployeeRequest;
import java.util.List;
import java.util.Map;

/** Service interface for managing department-employee relationships. */
public interface DepartmentEmployeeService {
  /**
   * Get all employees in a department by department code.
   *
   * @param departmentCode the code of the department
   * @return List of EmployeeDto representing employees in the department
   */
  List<DepartmentEmployeeDto> getEmployeesByDepartmentCode(String departmentCode);

  /**
   * Add an employee to a department.
   *
   * @param request the request containing employee addition details
   */
  void addEmployeeToDepartment(AssignEmployeeToDepartmentRequest request, String departmentCode);

  /**
   * Remove an employee from a department.
   *
   * @param departmentCode the code of the department
   * @param employeeId the ID of the employee to be removed
   */
  void removeEmployeeFromDepartment(String departmentCode, String employeeId);

  /**
   * Transfer an employee from one department to another.
   *
   * @param request the request containing transfer details
   * @return Map containing transfer confirmation details
   */
  Map<String, String> transferEmployee(TransferEmployeeRequest request);

  /**
   * Add multiple employees to a department in batch.
   *
   * @param requests the list of employee addition requests
   * @param departmentCode the code of the department
   * @return DepartmentEmployeeBatchResultDto containing batch operation results
   */
  DepartmentEmployeeBatchResultDto addEmployeesToDepartment(
      List<AssignEmployeeToDepartmentRequest> requests, String departmentCode);

  /**
   * Remove multiple employees from a department in batch.
   *
   * @param employeeIds the list of employee IDs to be removed
   * @param departmentCode the code of the department
   * @return DepartmentEmployeeBatchResultDto containing batch operation results
   */
  DepartmentEmployeeBatchResultDto removeEmployeesFromDepartmentBatch(
      List<String> employeeIds, String departmentCode);

  /**
   * Change an employee's role in a department.
   *
   * @param departmentCode the code of the department
   * @return Map containing role change confirmation details
   */
  Map<String, String> changeEmployeeRoleInDepartment(
      String departmentCode, String employeeId, String roleCode);
}
