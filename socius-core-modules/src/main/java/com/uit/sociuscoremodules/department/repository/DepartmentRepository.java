package com.uit.sociuscoremodules.department.repository;

import com.uit.sociuscoremodules.department.converter.DepartmentConverter;
import com.uit.sociuscoremodules.department.dto.DepartmentDto;
import com.uit.sociuscoremodules.department.dto.DepartmentEmployeesDto;
import com.uit.sociuscoremodules.department.persistence.DepartmentMapper;
import com.uit.sociuscoremodules.department.request.DepartmentCreateRequest;
import com.uit.sociuscoremodules.department.request.EmployeeAddRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/** Repository for Department entity. */
@Repository
@RequiredArgsConstructor
public class DepartmentRepository {
  /** MyBatis Mapper for Department entity. */
  private final DepartmentMapper departmentMapper;

  /** Singleton instance of DepartmentConverter. */
  private final DepartmentConverter departmentConverter;

  /**
   * Get DepartmentDto by department code.
   *
   * @param departmentCode the department code
   * @return the corresponding DepartmentDto
   */
  public DepartmentDto findByDepartmentCode(String departmentCode) {
    return departmentConverter.entityToDto(departmentMapper.findByDepartmentCode(departmentCode));
  }

  /**
   * Create a new department record.
   *
   * @param request the department creation request
   */
  public void create(DepartmentCreateRequest request) {
    departmentMapper.create(departmentConverter.createRequestToEntity(request));
  }

  /**
   * Update an existing department record.
   *
   * @param request the department update request
   */
  public void update(DepartmentCreateRequest request) {
    departmentMapper.update(departmentConverter.createRequestToEntity(request));
  }

  /**
   * Deactivate a department by its department code.
   *
   * @param departmentCode the department code
   */
  public void deactivate(String departmentCode) {
    departmentMapper.deactivate(departmentCode);
  }

  /**
   * Activate a department by its department code.
   *
   * @param request the department activation request
   */
  public void activate(DepartmentCreateRequest request) {
    departmentMapper.activate(request);
  }

  /**
   * Get all Departments.
   *
   * @return list of DepartmentDto representing all departments
   */
  public List<DepartmentDto> getAllDepartments() {
    return departmentConverter.entityListToDto(departmentMapper.getAllDepartments());
  }

  /**
   * Get all Employees in a Department by department code.
   *
   * @param departmentCode the department code
   * @return list of DepartmentEmployees representing all employees in the department
   */
  public List<DepartmentEmployeesDto> getEmployeesByDepartmentCode(String departmentCode) {
    return departmentConverter.entityListToEmployeeDto(
        departmentMapper.getEmployeesByDepartmentCode(departmentCode));
  }

  /**
   * Count total Employees in a Department by department code.
   *
   * @param departmentCode the department code
   * @return total number of Employees
   */
  public int countEmployeesInDepartment(String departmentCode) {
    return departmentMapper.countEmployeesByDepartmentCode(departmentCode);
  }

  /**
   * Add an Employee to a Department.
   *
   * @param request the request containing employee addition details
   * @param departmentCode the department code
   */
  public void addEmployeeToDepartment(
      EmployeeAddRequest request, String departmentCode, String employeeId) {
    departmentMapper.addEmployeeToDepartment(
        departmentConverter.createEmployeeRequestToEntity(request, departmentCode, employeeId));
  }

  /**
   * Remove an Employee from a Department.
   *
   * @param departmentCode the department code
   * @param employeeId the employee's user ID
   */
  public void removeEmployeeFromDepartment(String departmentCode, String employeeId) {
    departmentMapper.removeEmployeeFromDepartment(departmentCode, employeeId);
  }

  /**
   * Get deleted DepartmentDto by department code.
   *
   * @param departmentCode the department code
   * @return the corresponding deleted DepartmentDto
   */
  public DepartmentDto findDeletedByDepartmentCode(String departmentCode) {
    return departmentConverter.entityToDto(
        departmentMapper.findDeletedByDepartmentCode(departmentCode));
  }

  /**
   * Change an Employee's role in a Department.
   *
   * @param departmentCode the department code
   * @param employeeId the employee's user ID
   * @param roleCode the new role code
   */
  public void changeEmployeeRole(String departmentCode, String employeeId, String roleCode) {
    departmentMapper.changeEmployeeRoleInDepartment(departmentCode, employeeId, roleCode);
  }

  /**
   * Find an Employee in a Department by department code and employee ID.
   *
   * @param departmentCode the department code
   * @param employeeId the employee's user ID
   * @return the corresponding DepartmentEmployeesDto
   */
  public DepartmentEmployeesDto findEmployeeInDepartment(String departmentCode, String employeeId) {
    return departmentConverter.entityToDepartmentEmployeesDto(
        departmentMapper.findEmployeeInDepartment(departmentCode, employeeId));
  }
}
