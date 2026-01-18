package com.uit.sociusmvcapp.employee.internal.adapter;

import com.uit.sociusmvcapp.employee.EmployeeService;
import com.uit.sociusmvcapp.employee.dto.EmployeeDto;
import com.uit.sociusmvcapp.employee.internal.repository.EmployeeRepository;
import com.uit.sociusmvcapp.task.EmployeeGateway;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Adapter implementing EmployeeGateway for Task module.
 *
 * <p>This adapter provides employee-related operations to the Task module without creating direct
 * dependencies, following the Dependency Inversion Principle and maintaining clean module
 * boundaries.
 */
@Component
@RequiredArgsConstructor
public class EmployeeGatewayAdapter implements EmployeeGateway {

  private final EmployeeService employeeService;
  private final EmployeeRepository employeeRepository;

  /**
   * Validate if an employee exists by their client ID.
   *
   * @param clientId the employee client ID to validate
   * @throws NotFoundException if employee not found
   */
  @Override
  public void validateEmployeeExists(String clientId) {
    employeeService.validateExists(clientId);
  }

  /**
   * Get employee full name by client ID.
   *
   * @param clientId the employee client ID
   * @return full name (firstName + lastName) or null if not found
   */
  @Override
  public String getEmployeeName(String clientId) {
    EmployeeDto employee = employeeRepository.findByClientId(clientId);
    if (employee == null) {
      return null;
    }
    return employee.getFirstName() + " " + employee.getLastName();
  }

  /**
   * Batch get employee names by client IDs.
   *
   * @param clientIds set of employee client IDs
   * @return map of clientId to full name
   */
  @Override
  public Map<String, String> getEmployeeNames(Set<String> clientIds) {
    if (clientIds == null || clientIds.isEmpty()) {
      return Map.of();
    }

    return employeeRepository.findByClientIds(clientIds).stream()
        .collect(
            Collectors.toMap(
                EmployeeDto::getClientId, emp -> emp.getFirstName() + " " + emp.getLastName()));
  }
}
