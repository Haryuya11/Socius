package com.uit.sociuscoremodules.department.persistence;

import com.uit.sociuscoremodules.department.domain.Department;
import com.uit.sociuscoremodules.department.domain.DepartmentEmployees;
import com.uit.sociuscoremodules.department.request.DepartmentCreateRequest;
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
   * @param request the department to be created
   */
  void create(@Param("request") Department request);

  /**
   * Update an existing Department.
   *
   * @param request the department update request
   */
  void update(@Param("request") Department request);

  /**
   * Soft delete a Department by its department code.
   *
   * @param departmentCode the department code
   */
  void deactivate(@Param("departmentCode") String departmentCode);

  /**
   * Reactivate a previously deleted Department.
   *
   * @param request the department reactivation request
   */
  void activate(@Param("departmentCode") DepartmentCreateRequest request);

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
  void addEmployeeToDepartment(DepartmentEmployees request);

  /**
   * Remove an Employee from a Department.
   *
   * @param departmentCode the department code
   * @param employeeId the employee's user ID
   */
  void removeEmployeeFromDepartment(
      @Param("departmentCode") String departmentCode, @Param("employeeId") String employeeId);

  /**
   * Find a deleted Department by its department code.
   *
   * @param departmentCode the department code
   * @return the deleted Department entity
   */
  Department findDeletedByDepartmentCode(@Param("departmentCode") String departmentCode);

  /**
   * Change an Employee's role in a Department.
   *
   * @param departmentCode the department code
   * @param employeeId the employee's user ID
   * @param roleCode the new role code
   */
  void changeEmployeeRoleInDepartment(
      @Param("departmentCode") String departmentCode,
      @Param("employeeId") String employeeId,
      @Param("roleCode") String roleCode);

  /**
   * Find an Employee in a Department by department code and employee ID.
   *
   * @param departmentCode the department code
   * @param employeeId the employee's user ID
   * @return the DepartmentEmployees entity
   */
  DepartmentEmployees findEmployeeInDepartment(
      @Param("departmentCode") String departmentCode, @Param("employeeId") String employeeId);
}
