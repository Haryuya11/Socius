package com.uit.sociusmvcapp.employee.internal.repository;

import com.uit.sociusmvcapp.employee.dto.EmployeeDto;
import com.uit.sociusmvcapp.employee.dto.SearchEmployeeDto;
import com.uit.sociusmvcapp.employee.dto.request.CreateEmployeeRequest;
import com.uit.sociusmvcapp.employee.dto.request.SearchUserRequest;
import com.uit.sociusmvcapp.employee.internal.converter.EmployeeConverter;
import com.uit.sociusmvcapp.employee.internal.persistence.EmployeeMapper;
import com.uit.sociusmvcapp.shared.request.SortRequest;
import java.util.List;
import java.util.Set;
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
  public void create(CreateEmployeeRequest request) {
    employeeMapper.create(employeeConverter.createRequestToEntity(request));
  }

  /**
   * Update an existing employee record.
   *
   * @param request the user creation request
   */
  public void update(CreateEmployeeRequest request) {
    employeeMapper.update(employeeConverter.createRequestToEntity(request));
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
   * Reactivate an employee record by client ID.
   *
   * @param request the user reactivation request
   */
  public void reactivate(CreateEmployeeRequest request) {
    employeeMapper.reactivate(employeeConverter.createRequestToEntity(request));
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

  /**
   * Count employees based on given criteria.
   *
   * @param criteria the search criteria
   * @return the count of employees matching the criteria
   */
  public int count(SearchUserRequest criteria) {
    return employeeMapper.count(criteria);
  }

  /**
   * Search for employees based on given criteria.
   *
   * @param criteria the search criteria
   * @param sortRequests the sorting requests
   * @param limit the maximum number of records to return
   * @param offset the starting point for records to return
   */
  public List<SearchEmployeeDto> search(
      SearchUserRequest criteria, List<SortRequest> sortRequests, int limit, int offset) {
    return employeeConverter.entitiesToSearchDtos(
        employeeMapper.search(criteria, sortRequests, limit, offset));
  }

  /**
   * Check if an employee exists by client ID.
   *
   * @param clientId the employee client ID
   * @return true if the employee exists, false otherwise
   */
  public boolean existsByClientId(String clientId) {
    return employeeMapper.existsByClientId(clientId);
  }

  /**
   * Batch find employees by client IDs.
   *
   * @param clientIds set of employee client IDs
   * @return list of EmployeeDtos matching the client IDs
   */
  public List<EmployeeDto> findByClientIds(Set<String> clientIds) {
    if (clientIds == null || clientIds.isEmpty()) {
      return List.of();
    }
    return employeeConverter.entitiesToDtos(employeeMapper.findByClientIds(clientIds));
  }
}
