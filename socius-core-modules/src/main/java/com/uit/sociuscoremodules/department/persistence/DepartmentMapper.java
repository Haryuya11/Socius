package com.uit.sociuscoremodules.department.persistence;

import com.uit.sociuscoremodules.department.domain.Department;
import com.uit.sociuscoremodules.department.domain.DepartmentEmployees;
import com.uit.sociuscoremodules.department.request.DepartmentCreateRequest;
import com.uit.sociuscoremodules.department.request.EmployeeAddRequest;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/** MyBatis Mapper interface for Department entity. */
@Mapper
public interface DepartmentMapper {
  /**
   * Find a Department by its department code.
   *
   * @param departmentCode the department code
   * @return the Department entity
   */
  Department findByDepartmentCode(@Param("departmentCode") String departmentCode);

  /**
   * Create a new Department.
   *
   * @param request the department creation request
   */
  void create(@Param("request") DepartmentCreateRequest request);

  /**
   * Update an existing Department.
   *
   * @param request the department update request
   */
  void update(@Param("request") DepartmentCreateRequest request);

  /**
   * Soft delete a Department by its department code.
   *
   * @param departmentCode the department code
   */
  void deactivate(@Param("departmentCode") String departmentCode);

  /**
   * Reactivate a Department by its department code.
   *
   * @param departmentCode the department code
   */
  void activate(@Param("departmentCode") String departmentCode);

  /**
   * Get all Departments.
   *
   * @return list of Department entities
   */
  List<Department> getAllDepartments();

  /**
   * Get all Employees in a Department by department code.
   *
   * @param departmentCode the department code
   * @return list of DepartmentEmployees entities
   */
  List<DepartmentEmployees> getEmployeesByDepartmentCode(
      @Param("departmentCode") String departmentCode);

  /**
   * Count total Employees in a Department by department code.
   *
   * @param departmentCode the department code
   * @return total number of Employees
   */
  int countEmployeesByDepartmentCode(@Param("departmentCode") String departmentCode);

  /**
   * Add an Employee to a Department.
   *
   * @param request the employee addition request
   */
  void addEmployeeToDepartment(
      @Param("request") EmployeeAddRequest request, @Param("departmentCode") String departmentCode);

  /**
   * Remove an Employee from a Department.
   *
   * @param departmentCode the department code
   * @param employeeId the employee's user ID
   */
  void removeEmployeeFromDepartment(
      @Param("departmentCode") String departmentCode, @Param("employeeId") String employeeId);

  /**
   * Move an Employee to a different Department.
   *
   * @param employeeId the employee's user ID
   * @param newDepartmentCode the new department code
   */
}
