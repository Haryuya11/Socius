package com.uit.sociuscoremodules.employee.persistence;

import com.uit.sociuscoremodules.employee.domain.Employee;
import com.uit.sociuscoremodules.employee.request.EmployeeCreateRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/** MyBatis Mapper interface for Employee entity. */
@Mapper
public interface EmployeeMapper {

  /**
   * Get Employee by ID.
   *
   * @param clientId the employee ID
   * @return the Employee entity
   */
  Employee findByClientId(@Param("clientId") String clientId);

  /**
   * Get Employee by User ID.
   *
   * @param userId the user ID
   * @return the Employee entity
   */
  Employee findDeletedByUserId(@Param("userId") String userId);

  /**
   * Create a new employee record.
   *
   * @param request the user creation request
   */
  void create(@Param("request") EmployeeCreateRequest request);

  /**
   * Update an existing employee record.
   *
   * @param request the user creation request
   */
  void update(@Param("request") EmployeeCreateRequest request);

  /**
   * Deactivate an employee record by client ID.
   *
   * @param clientId the client ID of the employee to deactivate
   */
  void deactivate(@Param("clientId") String clientId);

  /**
   * Find employees by user ID.
   *
   * @param userId the user ID
   * @return list of Employee entities
   */
  Employee findByUserId(@Param("userId") String userId);
}
