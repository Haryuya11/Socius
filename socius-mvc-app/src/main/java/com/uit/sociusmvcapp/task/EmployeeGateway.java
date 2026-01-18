package com.uit.sociusmvcapp.task;

import java.util.Map;
import java.util.Set;

/**
 * Gateway interface for employee-related operations.
 *
 * <p>This interface allows Task module to validate employees without direct dependency on Employee
 * module, following the Dependency Inversion Principle and maintaining clean module boundaries.
 */
public interface EmployeeGateway {

  /**
   * Validate if an employee exists by their client ID.
   *
   * @param clientId the employee client ID to validate
   * @throws NotFoundException if employee not found
   */
  void validateEmployeeExists(String clientId);

  /**
   * Get employee full name by client ID.
   *
   * @param clientId the employee client ID
   * @return full name (firstName + lastName) or null if not found
   */
  String getEmployeeName(String clientId);

  /**
   * Batch get employee names by client IDs.
   *
   * @param clientIds set of employee client IDs
   * @return map of clientId to full name
   */
  Map<String, String> getEmployeeNames(Set<String> clientIds);
}
