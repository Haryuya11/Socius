package com.uit.sociusmvcapp.workforce.internal.service;

import com.uit.sociusmvcapp.department.DepartmentService;
import com.uit.sociusmvcapp.employee.EmployeeService;
import com.uit.sociusmvcapp.shared.constants.MessageConstant;
import com.uit.sociusmvcapp.shared.service.ExceptionFactory;
import com.uit.sociusmvcapp.shared.service.I18nService;
import com.uit.sociusmvcapp.workforce.DepartmentEmployeeService;
import com.uit.sociusmvcapp.workforce.dto.BatchErrorDto;
import com.uit.sociusmvcapp.workforce.dto.BatchProcessResultDto;
import com.uit.sociusmvcapp.workforce.dto.DepartmentEmployeeBatchResultDto;
import com.uit.sociusmvcapp.workforce.dto.DepartmentEmployeeDto;
import com.uit.sociusmvcapp.workforce.dto.request.AssignEmployeeToDepartmentRequest;
import com.uit.sociusmvcapp.workforce.dto.request.TransferEmployeeRequest;
import com.uit.sociusmvcapp.workforce.internal.constants.DepartmentEmployeeConstant;
import com.uit.sociusmvcapp.workforce.internal.converter.DepartmentEmployeeConverter;
import com.uit.sociusmvcapp.workforce.internal.repository.DepartmentEmployeeRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    String fromDept = request.getFromDepartmentCode();
    String toDept = request.getToDepartmentCode();
    String empId = request.getEmployeeId();

    // 1. Validation
    departmentService.validateExists(fromDept);
    departmentService.validateExists(toDept);

    // 2. Domain Logic Check
    validateTransferEligibility(fromDept, toDept, empId);

    // 3. Execution
    AssignEmployeeToDepartmentRequest addRequest =
        departmentEmployeeConverter.toAssignEmployeeToDepartmentRequest(request);

    departmentEmployeeRepository.removeEmployeeFromDepartment(fromDept, empId);
    departmentEmployeeRepository.addEmployeeToDepartment(addRequest, toDept);

    return Map.of(
        DepartmentEmployeeConstant.EMPLOYEE_ID, empId,
        DepartmentEmployeeConstant.FROM_DEPARTMENT_CODE, fromDept,
        DepartmentEmployeeConstant.TO_DEPARTMENT_CODE, toDept);
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

    if (requests == null || requests.isEmpty()) {
      return DepartmentEmployeeBatchResultDto.builder()
          .successful(Collections.emptyList())
          .failed(Collections.emptyList())
          .build();
    }

    // 1. Prepare Data
    List<String> requestEmployeeIds =
        requests.stream().map(AssignEmployeeToDepartmentRequest::getEmployeeId).toList();

    Set<String> existingIds =
        new HashSet<>(
            departmentEmployeeRepository.findEmployeeIdsInDepartment(
                departmentCode, requestEmployeeIds));

    // 2. Process Requests
    BatchProcessResultDto<AssignEmployeeToDepartmentRequest> result =
        processAddBatchRequests(requests, existingIds);

    // 3. Execute Valid Requests
    if (!result.getToInsert().isEmpty()) {
      departmentEmployeeRepository.addEmployeesToDepartmentBatch(
          result.getToInsert(), departmentCode);
    }

    // Lưu ý: Dùng .getSuccessfulIds() và .getErrors()
    return DepartmentEmployeeBatchResultDto.builder()
        .successful(result.getSuccessfulIds())
        .failed(result.getErrors())
        .build();
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

    if (employeeIds == null || employeeIds.isEmpty()) {
      return DepartmentEmployeeBatchResultDto.builder()
          .successful(Collections.emptyList())
          .failed(Collections.emptyList())
          .build();
    }

    // 1. Prepare Data
    Set<String> existingIds =
        new HashSet<>(
            departmentEmployeeRepository.findEmployeeIdsInDepartment(departmentCode, employeeIds));

    // 2. Process Requests (Sử dụng hàm Helper mới)
    BatchProcessResultDto<String> result = processRemoveBatchRequests(employeeIds, existingIds);

    // 3. Execute Valid Requests
    if (!result.getToInsert().isEmpty()) {
      departmentEmployeeRepository.removeEmployeesFromDepartmentBatch(
          result.getToInsert(), departmentCode);
    }

    return DepartmentEmployeeBatchResultDto.builder()
        .successful(result.getSuccessfulIds())
        .failed(result.getErrors())
        .build();
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

  /** Helper to build BatchErrorDto. */
  private BatchErrorDto buildBatchError(String employeeId, String errorMessage) {
    return BatchErrorDto.builder()
        .clientId(employeeId)
        .errorCode(MessageConstant.S_TEAM_EMP_001)
        .errorMessage(errorMessage)
        .build();
  }

  /** Validates if an employee can be transferred. */
  private void validateTransferEligibility(String fromDept, String toDept, String empId) {
    boolean isInFromDept =
        departmentEmployeeRepository.findEmployeeInDepartment(fromDept, empId) != null;
    if (!isInFromDept) {
      throw ExceptionFactory.notFound(MessageConstant.E_DEP_010);
    }

    boolean isInToDept =
        departmentEmployeeRepository.findEmployeeInDepartment(toDept, empId) != null;
    if (isInToDept) {
      throw ExceptionFactory.badRequest(MessageConstant.E_DEP_011);
    }
  }

  /** Helper to process add requests logic separate from DB execution. */
  private BatchProcessResultDto<AssignEmployeeToDepartmentRequest> processAddBatchRequests(
      List<AssignEmployeeToDepartmentRequest> requests, Set<String> existingIds) {

    List<AssignEmployeeToDepartmentRequest> toInsert = new ArrayList<>();
    List<String> successfulIds = new ArrayList<>();
    List<BatchErrorDto> errors = new ArrayList<>();

    for (AssignEmployeeToDepartmentRequest req : requests) {
      String empId = req.getEmployeeId();

      if (existingIds.contains(empId)) {
        errors.add(buildBatchError(empId, i18nService.getMessage(MessageConstant.E_DEP_011)));
        continue;
      }

      try {
        // Validation check (e.g. check if user exists in system)
        employeeService.validateExists(empId);

        toInsert.add(req);
        successfulIds.add(empId);
      } catch (Exception e) {
        errors.add(buildBatchError(empId, i18nService.getMessage(e.getMessage())));
      }
    }

    return new BatchProcessResultDto<>(toInsert, successfulIds, errors);
  }

  /** Helper to process remove requests logic. */
  private BatchProcessResultDto<String> processRemoveBatchRequests(
      List<String> employeeIds, Set<String> existingIds) {

    List<String> toDelete = new ArrayList<>();
    List<String> successfulIds = new ArrayList<>();
    List<BatchErrorDto> errors = new ArrayList<>();

    for (String empId : employeeIds) {
      if (existingIds.contains(empId)) {
        toDelete.add(empId);
        successfulIds.add(empId);
      } else {
        errors.add(buildBatchError(empId, i18nService.getMessage(MessageConstant.E_DEP_010)));
      }
    }

    return new BatchProcessResultDto<>(toDelete, successfulIds, errors);
  }
}
