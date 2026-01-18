package com.uit.sociusmvcapp.task;

/**
 * Gateway interface for department-related operations.
 *
 * <p>This interface allows Task module to validate departments without direct dependency on
 * Department module, following the Dependency Inversion Principle and maintaining clean module
 * boundaries.
 */
public interface DepartmentGateway {

  /**
   * Validate if a department exists by its code.
   *
   * @param departmentCode the department code to validate
   * @throws NotFoundException if department not found
   */
  void validateDepartmentExists(String departmentCode);
}
