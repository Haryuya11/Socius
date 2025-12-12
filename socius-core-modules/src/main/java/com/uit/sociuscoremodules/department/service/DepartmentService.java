package com.uit.sociuscoremodules.department.service;

import com.uit.sociuscoremodules.department.dto.DepartmentDto;
import com.uit.sociuscoremodules.department.dto.DepartmentEmployeesDto;
import com.uit.sociuscoremodules.department.request.DepartmentCreateRequest;
import com.uit.sociuscoremodules.department.request.EmployeeAddRequest;
import com.uit.sociuscoremodules.employee.dto.EmployeeDto;
import java.util.List;
import java.util.Map;

/** Service interface for Department operations. */
public interface DepartmentService {
  /**
   * Get department information by department ID.
   *
   * @param departmentId the ID of the department
   * @return DepartmentDto representing the department information
   */
  DepartmentDto departmentInfo(String departmentId);

  /**
   * Create a new department.
   *
   * @param request the request containing department creation details
   * @return Map containing the created department code
   */
  Map<String, String> createDepartment(DepartmentCreateRequest request);

  /**
   * Update an existing department.
   *
   * @param request the request containing department update details
   * @param departmentCode the code of the department to be updated
   * @return Map containing the updated department code
   */
  Map<String, String> updateDepartment(DepartmentCreateRequest request, String departmentCode);

  /**
   * Deactivate a department.
   *
   * @param departmentCode the code of the department to be deactivated
   * @return Map containing the deactivated department code
   */
  Map<String, String> deactivateDepartment(String departmentCode);

  /**
   * Activate a department.
   *
   * @param departmentCode the code of the department to be activated
   * @return Map containing the activated department code
   */
  Map<String, String> activateDepartment(String departmentCode);

  /**
   * Get all departments.
   *
   * @return List of DepartmentDto representing all departments
   */
  List<DepartmentDto> getAllDepartments();

  /**
   * Get all employees in a department by department code.
   *
   * @param departmentCode the code of the department
   * @return List of EmployeeDto representing employees in the department
   */
  List<DepartmentEmployeesDto> getEmployeesByDepartmentCode(String departmentCode);

  /**
   * Add an employee to a department.
   *
   * @param request the request containing employee addition details
   * @return EmployeeDto representing the added employee
   */
  EmployeeDto addEmployeeToDepartment(EmployeeAddRequest request, String departmentCode);

  /**
   * Remove an employee from a department.
   *
   * @param departmentCode the code of the department
   * @param employeeId the ID of the employee to be removed
   * @return EmployeeDto representing the removed employee
   */
  EmployeeDto removeEmployeeFromDepartment(String departmentCode, String employeeId);

  /**
   * Transfer an employee from one department to another.
   *
   * @param fromDepartmentCode the code of the department to transfer from
   * @param request the request containing employee addition details
   * @param toDepartmentCode the code of the department to transfer to
   * @return Map containing transfer details
   */
  Map<String, String> transferEmployee(
      String fromDepartmentCode, EmployeeAddRequest request, String toDepartmentCode);
}
