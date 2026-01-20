package com.uit.sociusmvcapp.employee.internal.persistence;

import com.uit.sociusmvcapp.employee.dto.request.SearchUserRequest;
import com.uit.sociusmvcapp.employee.dto.request.UpdateEmployeeRequest;
import com.uit.sociusmvcapp.employee.internal.domain.Employee;
import com.uit.sociusmvcapp.shared.request.SortRequest;
import java.util.List;
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
  Employee findByClientId(String clientId);

  /**
   * Get Employee by User ID.
   *
   * @param userId the user ID
   * @return the Employee entity
   */
  Employee findDeletedByUserId(String userId);

  /**
   * Create a new employee record.
   *
   * @param request the user creation request
   */
  void create(Employee request);

  /**
   * Update employee profile information (excluding salary).
   *
   * <p>This method intentionally excludes salary to prevent mass assignment vulnerabilities.
   *
   * @param request the update request containing profile fields
   * @param clientId the client ID of the employee to update
   */
  void updateProfile(
      @Param("request") UpdateEmployeeRequest request, @Param("clientId") String clientId);

  /**
   * Update employee salary.
   *
   * <p>Separated from profile updates for security - requires specific authorization.
   *
   * @param salary the new salary value
   * @param clientId the client ID of the employee
   */
  void updateSalary(@Param("salary") Long salary, @Param("clientId") String clientId);

  /**
   * Update employee system role.
   *
   * <p>Separated from profile updates for security - only SYS_ADMIN can change system roles.
   *
   * @param systemRole the new system role
   * @param clientId the client ID of the employee
   */
  void updateSystemRole(@Param("systemRole") String systemRole, @Param("clientId") String clientId);

  /**
   * Deactivate an employee record by client ID.
   *
   * @param clientId the client ID of the employee to deactivate
   */
  void deactivate(String clientId);

  /**
   * Reactivate an employee record by client ID.
   *
   * @param request the user reactivation request
   */
  void reactivate(Employee request);

  /**
   * Find employees by user ID.
   *
   * @param userId the user ID
   * @return list of Employee entities
   */
  Employee findByUserId(String userId);

  /**
   * Search for employees based on criteria, sorting, pagination.
   *
   * @param criteria the search criteria
   * @param sorts the sorting options
   * @param limit the maximum number of records to return
   * @param offset the starting point for records to return
   * @return list of Employee entities matching the search criteria
   */
  List<Employee> search(
      @Param("criteria") SearchUserRequest criteria,
      @Param("sorts") List<SortRequest> sorts,
      @Param("limit") int limit,
      @Param("offset") int offset);

  /**
   * Count employees based on search criteria.
   *
   * @param criteria the search criteria
   * @return the count of employees matching the criteria
   */
  int count(@Param("criteria") SearchUserRequest criteria);

  /**
   * Check if an employee exists by client ID.
   *
   * @param clientId the employee client ID
   * @return true if the employee exists, false otherwise
   */
  boolean existsByClientId(String clientId);

  /**
   * Batch find employees by client IDs.
   *
   * @param clientIds set of employee client IDs
   * @return list of Employee entities
   */
  List<Employee> findByClientIds(@Param("clientIds") List<String> clientIds);
}
