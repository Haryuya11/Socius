package com.uit.sociuscoremodules.employee.persistence;

import com.uit.sociuscoremodules.employee.domain.Employee;
import com.uit.sociuscoremodules.employee.dto.PermissionQueryDto;
import com.uit.sociuscoremodules.employee.request.SearchUserRequest;
import com.uit.sociuscoremodules.shared.request.SortRequest;
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
  void create(@Param("request") Employee request);

  /**
   * Update an existing employee record.
   *
   * @param request the user creation request
   */
  void update(@Param("request") Employee request);

  /**
   * Deactivate an employee record by client ID.
   *
   * @param clientId the client ID of the employee to deactivate
   */
  void deactivate(@Param("clientId") String clientId);

  /**
   * Reactivate an employee record by client ID.
   *
   * @param request the user reactivation request
   */
  void reactivate(@Param("request") Employee request);

  /**
   * Find employees by user ID.
   *
   * @param userId the user ID
   * @return list of Employee entities
   */
  Employee findByUserId(@Param("userId") String userId);

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
   * Find permissions grouped by scope (system/department/team) for a client.
   *
   * @param clientId the client ID
   * @return list of maps containing role and permission information
   */
  List<PermissionQueryDto> findPermissionsByClientIdGrouped(@Param("clientId") String clientId);
}
