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
    departmentMapper.create(request);
  }

  /**
   * Update an existing department record.
   *
   * @param request the department update request
   */
  public void update(DepartmentCreateRequest request) {
    departmentMapper.update(request);
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
   * @param departmentCode the department code
   */
  public void activate(String departmentCode) {
    departmentMapper.activate(departmentCode);
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
  public void addEmployeeToDepartment(EmployeeAddRequest request, String departmentCode) {
    departmentMapper.addEmployeeToDepartment(request, departmentCode);
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
}
