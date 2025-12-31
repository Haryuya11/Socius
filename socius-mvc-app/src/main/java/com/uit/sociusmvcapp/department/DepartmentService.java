package com.uit.sociusmvcapp.department;

import com.uit.sociusmvcapp.department.dto.DepartmentDto;
import com.uit.sociusmvcapp.department.dto.request.CreateDepartmentRequest;
import java.util.List;

/** Service interface for Department operations. */
public interface DepartmentService {
  /**
   * Get department information by department code.
   *
   * @param departmentCode the code of the department
   * @return DepartmentDto representing the department information
   */
  DepartmentDto findByDepartmentCode(String departmentCode);

  /**
   * Create a new department.
   *
   * @param request the request containing department creation details
   */
  void create(CreateDepartmentRequest request);

  /**
   * Update an existing department.
   *
   * @param request the request containing department update details
   * @param departmentCode the code of the department to be updated
   */
  void update(CreateDepartmentRequest request, String departmentCode);

  /**
   * Deactivate a department.
   *
   * @param departmentCode the code of the department to be deactivated
   */
  void deactivate(String departmentCode);

  /**
   * Get all departments.
   *
   * @return List of DepartmentDto representing all departments
   */
  List<DepartmentDto> findAll();

  /**
   * Validate if a department exists by its code.
   *
   * @param departmentCode the code of the department
   */
  void validateExists(String departmentCode);
}
