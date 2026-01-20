package com.uit.sociusmvcapp.department;

import java.util.List;

/**
 * Gateway interface for department-employee relationship operations.
 *
 * <p>This interface allows Department module to access department member information without direct
 * dependency on Workforce module, following the Dependency Inversion Principle and maintaining
 * clean module boundaries.
 */
public interface DepartmentEmployeeGateway {

  /**
   * Get all active member IDs in a department.
   *
   * <p>Returns only active (non-deleted) employee client IDs for notification purposes.
   *
   * @param departmentCode the department code
   * @return list of active employee client IDs
   */
  List<String> getActiveMemberIds(String departmentCode);
}
