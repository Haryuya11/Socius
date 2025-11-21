package com.uit.sociuscoremodules.employee.repository;

import com.uit.sociuscoremodules.employee.converter.EmployeeConverter;
import com.uit.sociuscoremodules.employee.dto.EmployeeDto;
import com.uit.sociuscoremodules.employee.persistence.EmployeeMapper;
import com.uit.sociuscoremodules.employee.request.EmployeeCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/** Repository for Employee entity. */
@Repository
@RequiredArgsConstructor
public class EmployeeRepository {
  /** MyBatis Mapper for Employee entity. */
  private final EmployeeMapper employeeMapper;

  /** Singleton instance of EmployeeConverter. */
  private final EmployeeConverter employeeConverter;

  /**
   * Get EmployeeDto by employee ID.
   *
   * @param employeeId the employee ID
   * @return the corresponding EmployeeDto
   */
  public EmployeeDto findByClientId(String employeeId) {
    return employeeConverter.entityToDto(employeeMapper.findByClientId(employeeId));
  }

  /**
   * Get EmployeeDto by user ID.
   *
   * @param userId the user ID
   * @return the corresponding EmployeeDto
   */
  public EmployeeDto findDeletedByUserId(String userId) {
    return employeeConverter.entityToDto(employeeMapper.findDeletedByUserId(userId));
  }

  /**
   * Create a new employee record.
   *
   * @param request the user creation request
   */
  public void create(EmployeeCreateRequest request) {
    employeeMapper.create(request);
  }

  /**
   * Update an existing employee record.
   *
   * @param request the user creation request
   */
  public void update(EmployeeCreateRequest request) {
    employeeMapper.update(request);
  }

  /**
   * Deactivate an employee record by client ID.
   *
   * @param clientId the client ID of the employee to deactivate
   */
  public void deactivate(String clientId) {
    employeeMapper.deactivate(clientId);
  }

  /**
   * Get EmployeeDto by user ID.
   *
   * @param userId the user ID
   * @return the corresponding EmployeeDto
   */
  public EmployeeDto findByUserId(String userId) {
    return employeeConverter.entityToDto(employeeMapper.findByUserId(userId));
  }
}
