package com.uit.sociuscoremodules.employee.repository;

import com.uit.sociuscoremodules.employee.constants.RoleConstants;
import com.uit.sociuscoremodules.employee.converter.EmployeeConverter;
import com.uit.sociuscoremodules.employee.domain.Employee;
import com.uit.sociuscoremodules.employee.dto.EmployeeDto;
import com.uit.sociuscoremodules.employee.dto.EmployeeProfileDto;
import com.uit.sociuscoremodules.employee.dto.PermissionDto;
import com.uit.sociuscoremodules.employee.dto.PermissionQueryDto;
import com.uit.sociuscoremodules.employee.dto.ScopedPermissionDto;
import com.uit.sociuscoremodules.employee.dto.SearchEmployeeDto;
import com.uit.sociuscoremodules.employee.persistence.EmployeeMapper;
import com.uit.sociuscoremodules.employee.request.EmployeeCreateRequest;
import com.uit.sociuscoremodules.employee.request.SearchUserRequest;
import com.uit.sociuscoremodules.shared.request.SortRequest;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
   * Get EmployeeProfileDto by employee ID.
   *
   * @param employeeId the employee ID
   * @return the corresponding EmployeeProfileDto
   */
  public EmployeeProfileDto getProfileByClientId(String employeeId) {
    Employee employee = employeeMapper.findByClientId(employeeId);

    if (employee == null) {
      return null;
    }

    EmployeeProfileDto profile = employeeConverter.entityToProfileDto(employee);

    List<PermissionQueryDto> permissionDtos =
        employeeMapper.findPermissionsByClientIdGrouped(employeeId);

    List<ScopedPermissionDto> scopedPermissions = groupPermissionsByScope(permissionDtos);

    profile.setPermissions(scopedPermissions);

    return profile;
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
    employeeMapper.create(employeeConverter.createRequestToEntity(request));
  }

  /**
   * Update an existing employee record.
   *
   * @param request the user creation request
   */
  public void update(EmployeeCreateRequest request) {
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
  public void reactivate(EmployeeCreateRequest request) {
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
   * Group permissions by their scope.
   *
   * @param permissionDtos the list of PermissionQueryDto
   * @return the list of ScopedPermissionDto grouped by scope
   */
  private List<ScopedPermissionDto> groupPermissionsByScope(
      List<PermissionQueryDto> permissionDtos) {

    Map<String, ScopedPermissionDto> scopeMap = new LinkedHashMap<>();

    for (PermissionQueryDto dto : permissionDtos) {
      String scopeKey = buildScopeKey(dto.getRoleType(), dto.getRoleCode(), dto.getScopeCode());
      String displayScope = buildScope(dto.getRoleType(), dto.getScopeCode());

      ScopedPermissionDto scoped =
          scopeMap.computeIfAbsent(
              scopeKey,
              k ->
                  ScopedPermissionDto.builder()
                      .scope(displayScope)
                      .scopeCode(dto.getScopeCode())
                      .roleName(dto.getRoleName())
                      .permissions(new ArrayList<>())
                      .build());

      PermissionDto permission =
          PermissionDto.builder()
              .permissionCode(dto.getPermissionCode())
              .permissionName(dto.getPermissionName())
              .resource(dto.getResource())
              .action(dto.getAction())
              .description(dto.getDescription())
              .build();

      scoped.getPermissions().add(permission);
    }

    return new ArrayList<>(scopeMap.values());
  }

  /**
   * Build a unique scope key for grouping permissions.
   *
   * @param roleType the role type
   * @param roleCode the role code
   * @param scopeCode the scope code
   * @return the constructed scope key
   */
  private String buildScopeKey(String roleType, String roleCode, String scopeCode) {
    return roleType
        + RoleConstants.SCOPE_KEY_SEPARATOR
        + (scopeCode != null ? scopeCode : roleCode);
  }

  /**
   * Build the scope string based on role type and scope code.
   *
   * @param roleType the role type
   * @param scopeCode the scope code
   * @return the constructed scope string
   */
  private String buildScope(String roleType, String scopeCode) {
    if (RoleConstants.ROLE_TYPE_SYSTEM.equals(roleType)) {
      return RoleConstants.ROLE_TYPE_SYSTEM;
    }
    if (scopeCode != null) {
      return roleType + RoleConstants.SCOPE_KEY_SEPARATOR + scopeCode;
    }
    return roleType;
  }
}
