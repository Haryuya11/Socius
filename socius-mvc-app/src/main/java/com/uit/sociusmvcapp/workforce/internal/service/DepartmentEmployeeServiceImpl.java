package com.uit.sociusmvcapp.workforce.internal.service;

import com.uit.sociusmvcapp.department.DepartmentService;
import com.uit.sociusmvcapp.employee.EmployeeService;
import com.uit.sociusmvcapp.shared.constants.MessageConstant;
import com.uit.sociusmvcapp.shared.service.ExceptionFactory;
import com.uit.sociusmvcapp.shared.service.I18nService;
import com.uit.sociusmvcapp.workforce.DepartmentEmployeeService;
import com.uit.sociusmvcapp.workforce.dto.BatchErrorDto;
import com.uit.sociusmvcapp.workforce.dto.DepartmentEmployeeBatchResultDto;
import com.uit.sociusmvcapp.workforce.dto.DepartmentEmployeeDto;
import com.uit.sociusmvcapp.workforce.dto.request.AssignEmployeeToDepartmentRequest;
import com.uit.sociusmvcapp.workforce.dto.request.TransferEmployeeRequest;
import com.uit.sociusmvcapp.workforce.internal.constants.DepartmentEmployeeConstant;
import com.uit.sociusmvcapp.workforce.internal.converter.DepartmentEmployeeConverter;
import com.uit.sociusmvcapp.workforce.internal.repository.DepartmentEmployeeRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/** Implementation of DepartmentEmployeeService for managing department-employee relationships. */
@Service
@Slf4j
@RequiredArgsConstructor
public class DepartmentEmployeeServiceImpl implements DepartmentEmployeeService {

  private final DepartmentService departmentService;

  private final EmployeeService employeeService;

  private final DepartmentEmployeeRepository departmentEmployeeRepository;

  private final DepartmentEmployeeConverter departmentEmployeeConverter;

  private final I18nService i18nService;

  /**
   * Get all employees in a department by department code.
   *
   * @param departmentCode the code of the department
   * @return List of DepartmentEmployeesDto representing employees in the department
   */
  @Override
  public List<DepartmentEmployeeDto> getEmployeesByDepartmentCode(String departmentCode) {
    departmentService.validateExists(departmentCode);
    return departmentEmployeeRepository.getEmployeesByDepartmentCode(departmentCode);
  }

  /**
   * Add an employee to a department.
   *
   * @param request the request containing employee addition details
   * @param departmentCode the code of the department
   */
  @Override
  public void addEmployeeToDepartment(
      AssignEmployeeToDepartmentRequest request, String departmentCode) {
    departmentService.validateExists(departmentCode);

    employeeService.validateExists(request.getEmployeeId());
    departmentEmployeeRepository.addEmployeeToDepartment(request, departmentCode);
  }

  /**
   * Remove an employee from a department.
   *
   * @param departmentCode the code of the department
   * @param employeeId the client ID of the employee to remove
   */
  @Override
  public void removeEmployeeFromDepartment(String departmentCode, String employeeId) {
    departmentService.validateExists(departmentCode);
    employeeService.validateExists(employeeId);
    departmentEmployeeRepository.removeEmployeeFromDepartment(departmentCode, employeeId);
  }

  /**
   * Transfer an employee from one department to another.
   *
   * @param request the request containing transfer details
   * @return Map containing transfer details
   */
  @Override
  public Map<String, String> transferEmployee(TransferEmployeeRequest request) {
    departmentService.validateExists(request.getFromDepartmentCode());
    departmentService.validateExists(request.getToDepartmentCode());

    DepartmentEmployeeDto existingEmployeeInFromDepartment =
        departmentEmployeeRepository.findEmployeeInDepartment(
            request.getFromDepartmentCode(), request.getEmployeeId());
    if (existingEmployeeInFromDepartment == null) {
      throw ExceptionFactory.notFound(MessageConstant.E_DEP_010);
    }

    DepartmentEmployeeDto existingEmployeeInToDepartment =
        departmentEmployeeRepository.findEmployeeInDepartment(
            request.getToDepartmentCode(), request.getEmployeeId());
    if (existingEmployeeInToDepartment != null) {
      throw ExceptionFactory.badRequest(MessageConstant.E_DEP_011);
    }

    AssignEmployeeToDepartmentRequest addRequest =
        departmentEmployeeConverter.toAssignEmployeeToDepartmentRequest(request);
    departmentEmployeeRepository.removeEmployeeFromDepartment(
        request.getFromDepartmentCode(), request.getEmployeeId());
    departmentEmployeeRepository.addEmployeeToDepartment(addRequest, request.getToDepartmentCode());
    return Map.of(
        DepartmentEmployeeConstant.EMPLOYEE_ID,
        request.getEmployeeId(),
        DepartmentEmployeeConstant.FROM_DEPARTMENT_CODE,
        request.getFromDepartmentCode(),
        DepartmentEmployeeConstant.TO_DEPARTMENT_CODE,
        request.getToDepartmentCode());
  }

  /**
   * Add multiple employees to a department in batch.
   *
   * @param requests the list of requests containing employee addition details
   * @param departmentCode the code of the department
   * @return DepartmentEmployeeBatchResultDto containing batch operation results
   */
  @Override
  public DepartmentEmployeeBatchResultDto addEmployeesToDepartment(
      List<AssignEmployeeToDepartmentRequest> requests, String departmentCode) {
    departmentService.validateExists(departmentCode);

    List<String> successful = new ArrayList<>();
    List<BatchErrorDto> failed = new ArrayList<>();

    for (AssignEmployeeToDepartmentRequest request : requests) {
      try {
        employeeService.validateExists(request.getEmployeeId());

        DepartmentEmployeeDto existingEmployeeInDepartment =
            departmentEmployeeRepository.findEmployeeInDepartment(
                departmentCode, request.getEmployeeId());
        if (existingEmployeeInDepartment != null) {
          throw ExceptionFactory.badRequest(MessageConstant.E_DEP_011);
        }

        departmentEmployeeRepository.addEmployeeToDepartment(request, departmentCode);
        successful.add(request.getEmployeeId());
      } catch (Exception e) {
        String errorMessage = i18nService.getMessage(e.getMessage());
        failed.add(buildBatchError(request.getEmployeeId(), errorMessage));
      }
    }
    return new DepartmentEmployeeBatchResultDto(successful, failed);
  }

  /**
   * Remove multiple employees from a department in batch.
   *
   * @param employeeIds the list of employee client IDs to be removed
   * @param departmentCode the code of the department
   * @return DepartmentEmployeeBatchResultDto containing batch operation results
   */
  @Override
  public DepartmentEmployeeBatchResultDto removeEmployeesFromDepartmentBatch(
      List<String> employeeIds, String departmentCode) {
    departmentService.validateExists(departmentCode);

    List<String> successful = new ArrayList<>();
    List<BatchErrorDto> failed = new ArrayList<>();

    for (String employeeId : employeeIds) {
      try {
        employeeService.validateExists(employeeId);

        DepartmentEmployeeDto existingEmployeeInDepartment =
            departmentEmployeeRepository.findEmployeeInDepartment(departmentCode, employeeId);
        if (existingEmployeeInDepartment == null) {
          throw ExceptionFactory.notFound(MessageConstant.E_DEP_010);
        }

        departmentEmployeeRepository.removeEmployeeFromDepartment(departmentCode, employeeId);
        successful.add(employeeId);
      } catch (Exception e) {
        String errorMessage = i18nService.getMessage(e.getMessage());
        failed.add(buildBatchError(employeeId, errorMessage));
      }
    }
    return DepartmentEmployeeBatchResultDto.builder().successful(successful).failed(failed).build();
  }

  /**
   * Change an employee's role in a department.
   *
   * @param departmentCode the code of the department
   * @param employeeId the client ID of the employee whose role is to be changed
   * @return Map containing role change confirmation details
   */
  @Override
  public Map<String, String> changeEmployeeRoleInDepartment(
      String departmentCode, String employeeId, String roleCode) {
    departmentService.validateExists(departmentCode);
    employeeService.validateExists(employeeId);
    departmentEmployeeRepository.changeEmployeeRole(departmentCode, employeeId, roleCode);
    return Map.of(
        DepartmentEmployeeConstant.EMPLOYEE_ID,
        employeeId,
        DepartmentEmployeeConstant.DEPARTMENT_CODE,
        departmentCode,
        DepartmentEmployeeConstant.ROLE_CODE,
        roleCode);
  }

  /** Helper method to build a BatchErrorDto. */
  private BatchErrorDto buildBatchError(String employeeId, String errorMessage) {
    return BatchErrorDto.builder()
        .clientId(employeeId)
        .errorCode(MessageConstant.S_TEAM_EMP_001)
        .errorMessage(errorMessage)
        .build();
  }
}
