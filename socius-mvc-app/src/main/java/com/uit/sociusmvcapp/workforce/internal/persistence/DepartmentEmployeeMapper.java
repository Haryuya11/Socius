package com.uit.sociusmvcapp.workforce.internal.persistence;

import com.uit.sociusmvcapp.workforce.internal.domain.DepartmentEmployee;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/** MyBatis Mapper interface for DepartmentEmployee entity. */
@Mapper
public interface DepartmentEmployeeMapper {
  /**
   * Get all Employees in a Department by department code.
   *
   * @param departmentCode the department code
   * @return list of DepartmentEmployees entities
   */
  List<DepartmentEmployee> getEmployeesByDepartmentCode(String departmentCode);

  /**
   * Count total Employees in a Department by department code.
   *
   * @param departmentCode the department code
   * @return total number of Employees
   */
  int countEmployeesByDepartmentCode(String departmentCode);

  /**
   * Add an Employee to a Department.
   *
   * @param request the employee addition request
   */
  void addEmployeeToDepartment(DepartmentEmployee request);

  /**
   * Remove an Employee from a Department.
   *
   * @param departmentCode the department code
   * @param employeeId the employee's user ID
   */
  void removeEmployeeFromDepartment(
      @Param("departmentCode") String departmentCode, @Param("employeeId") String employeeId);

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
  DepartmentEmployee findEmployeeInDepartment(
      @Param("departmentCode") String departmentCode, @Param("employeeId") String employeeId);

  /**
   * Check if a team has any employees assigned.
   *
   * @param teamCode the team code
   * @return true if the team has employees, false otherwise
   */
  boolean hasEmployees(String teamCode);

  /**
   * Delete all department-employee relationships for a given employee ID.
   *
   * @param employeeId the employee ID
   */
  void deleteByEmployeeId(String employeeId);

  /**
   * Find all department-employee relationships for a given employee ID.
   *
   * @param employeeId the employee ID
   * @return list of DepartmentEmployee entities
   */
  List<DepartmentEmployee> findByEmployeeId(String employeeId);

  /**
   * Find all department-employee relationships for a list of employee IDs.
   *
   * @param employeeIds the list of employee IDs
   * @return list of DepartmentEmployee entities
   */
  List<DepartmentEmployee> findByEmployeeIdIn(List<String> employeeIds);

  /**
   * Find employee IDs in a department from a list of employee IDs.
   *
   * @param departmentCode the department code
   * @param employeeIds the list of employee IDs
   * @return list of employee IDs present in the department
   */
  List<String> findEmployeeIdsInDepartment(
      @Param("departmentCode") String departmentCode,
      @Param("employeeIds") List<String> employeeIds);

  /**
   * Add multiple Employees to a Department in batch.
   *
   * @param entities the list of DepartmentEmployee entities to be added
   */
  void addEmployeesToDepartmentBatch(@Param("entities") List<DepartmentEmployee> entities);

  /**
   * Remove multiple Employees from a Department in batch.
   *
   * @param departmentCode the department code
   * @param employeeIds the list of employee IDs to be removed
   */
  void removeEmployeesFromDepartmentBatch(
      @Param("departmentCode") String departmentCode,
      @Param("employeeIds") List<String> employeeIds);
}
