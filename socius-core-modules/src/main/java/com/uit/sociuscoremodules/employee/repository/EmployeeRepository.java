package com.uit.sociuscoremodules.employee.repository;

import com.uit.sociuscoremodules.employee.converter.EmployeeConverter;
import com.uit.sociuscoremodules.employee.domain.Employee;
import com.uit.sociuscoremodules.employee.dto.EmployeeDto;
import com.uit.sociuscoremodules.employee.persistence.EmployeeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/** Repository for Employee entity. */
@Repository
@RequiredArgsConstructor
public class EmployeeRepository {
  /** MyBatis Mapper for Employee entity. */
  private final EmployeeMapper employeeMapper;

  /** Singleton instance of EmployeeConverter. */
  private static final EmployeeConverter employeeConverter = EmployeeConverter.INSTANCE;

  /**
   * Get EmployeeDto by employee ID.
   *
   * @param employeeId the employee ID
   * @return the corresponding EmployeeDto
   */
  public EmployeeDto getEmployeeById(String employeeId) {
    Employee employee = employeeMapper.getEmployeeById(employeeId);
    return employeeConverter.entityToDto(employee);
  }
}
