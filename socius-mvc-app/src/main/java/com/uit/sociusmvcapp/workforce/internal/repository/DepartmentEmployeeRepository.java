package com.uit.sociusmvcapp.workforce.internal.repository;

import com.uit.sociusmvcapp.workforce.dto.DepartmentEmployeeDto;
import com.uit.sociusmvcapp.workforce.dto.request.AssignEmployeeToDepartmentRequest;
import com.uit.sociusmvcapp.workforce.internal.converter.DepartmentEmployeeConverter;
import com.uit.sociusmvcapp.workforce.internal.domain.DepartmentEmployee;
import com.uit.sociusmvcapp.workforce.internal.persistence.DepartmentEmployeeMapper;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/** Repository for DepartmentEmployee entity. */
@Repository
@RequiredArgsConstructor
public class DepartmentEmployeeRepository {

  /** MyBatis Mapper for DepartmentEmployee entity. */
  private final DepartmentEmployeeMapper mapper;

  /** Singleton instance of DepartmentEmployeeConverter. */
  private final DepartmentEmployeeConverter converter;

  /**
   * Get all Employees in a Department by department code.
   *
   * @param departmentCode the department code
   * @return list of DepartmentEmployees representing all employees in the department
   */
  public List<DepartmentEmployeeDto> getEmployeesByDepartmentCode(String departmentCode) {
    return converter.entitiesToDtos(mapper.getEmployeesByDepartmentCode(departmentCode));
  }

  /**
   * Add an Employee to a Department.
   *
   * @param request the request containing employee addition details
   * @param departmentCode the department code
   */
  public void addEmployeeToDepartment(
      AssignEmployeeToDepartmentRequest request, String departmentCode) {
    mapper.addEmployeeToDepartment(converter.toEntity(request, departmentCode));
  }

  /**
   * Remove an Employee from a Department.
   *
   * @param departmentCode the department code
   * @param employeeId the employee's user ID
   */
  public void removeEmployeeFromDepartment(String departmentCode, String employeeId) {
    mapper.removeEmployeeFromDepartment(departmentCode, employeeId);
  }

  /**
   * Change an Employee's role in a Department.
   *
   * @param departmentCode the department code
   * @param employeeId the employee's user ID
   * @param roleCode the new role code
   */
  public void changeEmployeeRole(String departmentCode, String employeeId, String roleCode) {
    mapper.changeEmployeeRoleInDepartment(departmentCode, employeeId, roleCode);
  }

  /**
   * Find an Employee in a Department by department code and employee ID.
   *
   * @param departmentCode the department code
   * @param employeeId the employee's user ID
   * @return the corresponding DepartmentEmployeesDto
   */
  public DepartmentEmployeeDto findEmployeeInDepartment(String departmentCode, String employeeId) {
    return converter.entityToDto(mapper.findEmployeeInDepartment(departmentCode, employeeId));
  }

  /**
   * Check if a department has any employees.
   *
   * @param departmentCode the department code
   * @return true if the department has employees, false otherwise
   */
  public boolean hasEmployees(String departmentCode) {
    return mapper.hasEmployees(departmentCode);
  }

  /**
   * Delete all department-employee relationships for a given employee ID.
   *
   * @param employeeId the employee's user ID
   */
  public void deleteByEmployeeId(String employeeId) {
    mapper.deleteByEmployeeId(employeeId);
  }

  /**
   * Find all department-employee relationships for a given employee ID.
   *
   * @param employeeId the employee's user ID
   * @return list of DepartmentEmployeesDto representing the relationships
   */
  public List<DepartmentEmployeeDto> findByEmployeeId(String employeeId) {
    return converter.entitiesToDtos(mapper.findByEmployeeId(employeeId));
  }

  /**
   * Find all department-employee relationships for a list of employee IDs.
   *
   * @param employeeIds the list of employee user IDs
   * @return list of DepartmentEmployeesDto representing the relationships
   */
  public List<DepartmentEmployeeDto> findByEmployeeIdIn(List<String> employeeIds) {
    return converter.entitiesToDtos(mapper.findByEmployeeIdIn(employeeIds));
  }

  /**
   * Find employee IDs in a department from a list of employee IDs.
   *
   * @param departmentCode the department code
   * @param employeeIds the list of employee user IDs
   * @return list of employee IDs that are in the department
   */
  public List<String> findEmployeeIdsInDepartment(String departmentCode, List<String> employeeIds) {
    if (employeeIds == null || employeeIds.isEmpty()) {
      return List.of();
    }
    return mapper.findEmployeeIdsInDepartment(departmentCode, employeeIds);
  }

  /**
   * Add multiple Employees to a Department in batch.
   *
   * @param requests the list of employee addition requests
   * @param departmentCode the department code
   */
  public void addEmployeesToDepartmentBatch(
      List<AssignEmployeeToDepartmentRequest> requests, String departmentCode) {
    if (requests == null || requests.isEmpty()) {
      return;
    }
    // Get list of employee IDs from requests
    List<String> requestIds =
        requests.stream().map(AssignEmployeeToDepartmentRequest::getEmployeeId).toList();

    // Find existing employee IDs in the department
    List<String> rawExistingIds = mapper.findRawEmployeeIdsInDepartment(departmentCode, requestIds);

    // Separate requests into those to insert and those to reactivate
    List<AssignEmployeeToDepartmentRequest> toInsertRequests = new ArrayList<>();
    List<AssignEmployeeToDepartmentRequest> toReactivateRequests = new ArrayList<>();

    for (AssignEmployeeToDepartmentRequest req : requests) {
      if (rawExistingIds.contains(req.getEmployeeId())) {
        toReactivateRequests.add(req);
      } else {
        toInsertRequests.add(req);
      }
    }

    // Batch insert new department-employee relationships
    if (!toInsertRequests.isEmpty()) {
      List<DepartmentEmployee> insertEntities =
          toInsertRequests.stream().map(req -> converter.toEntity(req, departmentCode)).toList();
      mapper.addEmployeesToDepartmentBatch(insertEntities);
    }

    // Batch reactivate existing department-employee relationships
    if (!toReactivateRequests.isEmpty()) {
      List<DepartmentEmployee> reactivateEntities =
          toReactivateRequests.stream()
              .map(req -> converter.toEntity(req, departmentCode))
              .toList();
      // Gọi hàm update batch mới viết trong mapper
      mapper.reactivateEmployeesInDepartmentBatch(reactivateEntities, departmentCode);
    }
  }

  /**
   * Remove multiple Employees from a Department in batch.
   *
   * @param employeeIds the list of employee user IDs
   * @param departmentCode the department code
   */
  public void removeEmployeesFromDepartmentBatch(List<String> employeeIds, String departmentCode) {
    if (employeeIds == null || employeeIds.isEmpty()) {
      return;
    }
    mapper.removeEmployeesFromDepartmentBatch(departmentCode, employeeIds);
  }
}
