package com.uit.sociusmvcapp.workforce.internal.service;

import com.uit.sociusmvcapp.department.DepartmentService;
import com.uit.sociusmvcapp.employee.EmployeeService;
import com.uit.sociusmvcapp.employee.dto.EmployeeDto;
import com.uit.sociusmvcapp.iam.UserContentProvider;
import com.uit.sociusmvcapp.shared.constants.MessageConstant;
import com.uit.sociusmvcapp.shared.event.NotificationMultiSendRequest;
import com.uit.sociusmvcapp.shared.event.NotificationSendEvent;
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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Implementation of DepartmentEmployeeService for managing department-employee relationships. */
@Service
@Slf4j
@RequiredArgsConstructor
public class DepartmentEmployeeServiceImpl implements DepartmentEmployeeService {

  private static final String LOG_EMPLOYEE_UNIT = "employee(s)";

  private static final String DEPARTMENTS_PATH = "/departments/";

  private final DepartmentService departmentService;

  private final EmployeeService employeeService;

  private final DepartmentEmployeeRepository departmentEmployeeRepository;

  private final DepartmentEmployeeConverter departmentEmployeeConverter;

  private final I18nService i18nService;

  private final ApplicationEventPublisher eventPublisher;

  private final UserContentProvider userContentProvider;

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
  @Transactional
  @CacheEvict(value = "user-principal", key = "#request.employeeId")
  public void addEmployeeToDepartment(
      AssignEmployeeToDepartmentRequest request, String departmentCode) {
    departmentService.validateExists(departmentCode);

    employeeService.validateExists(request.getEmployeeId());
    departmentEmployeeRepository.addEmployeeToDepartment(request, departmentCode);

    Map<String, String> params = Map.of("DEPARTMENT_CODE", departmentCode);
    publishSingleNotification(
        request.getEmployeeId(),
        "S_DEP_EMP_TITLE_004",
        "S_DEP_EMP_CONTENT_004",
        params,
        DEPARTMENTS_PATH + departmentCode);
  }

  /**
   * Remove an employee from a department.
   *
   * @param departmentCode the code of the department
   * @param employeeId the client ID of the employee to remove
   */
  @Override
  @Transactional
  @CacheEvict(value = "user-principal", key = "#employeeId")
  public void removeEmployeeFromDepartment(String departmentCode, String employeeId) {
    departmentService.validateExists(departmentCode);
    employeeService.validateExists(employeeId);
    departmentEmployeeRepository.removeEmployeeFromDepartment(departmentCode, employeeId);

    Map<String, String> params = Map.of("DEPARTMENT_CODE", departmentCode);
    publishSingleNotification(
        employeeId, "S_DEP_EMP_TITLE_005", "S_DEP_EMP_CONTENT_005", params, "/departments");
  }

  /**
   * Transfer an employee from one department to another.
   *
   * @param request the request containing transfer details
   * @return Map containing transfer details
   */
  @Override
  @Transactional
  @CacheEvict(value = "user-principal", key = "#request.employeeId")
  public Map<String, String> transferEmployee(TransferEmployeeRequest request) {
    String fromDept = request.getFromDepartmentCode();
    String toDept = request.getToDepartmentCode();
    String empId = request.getEmployeeId();

    // 1. Validation
    departmentService.validateExists(fromDept);
    departmentService.validateExists(toDept);

    // 2. Domain Logic Check
    validateTransferEligibility(fromDept, toDept, empId);

    // Get employee information for notifications
    EmployeeDto employee = employeeService.findByClientId(empId);
    String employeeName = employee.getFirstName() + " " + employee.getLastName();

    // Get all members in both departments BEFORE the transfer
    String performerId = userContentProvider.getUserContent().getClientId();
    List<String> oldDeptMemberIds =
        departmentEmployeeRepository.getEmployeesByDepartmentCode(fromDept).stream()
            .map(de -> de.getEmployee().getClientId())
            .filter(id -> !id.equals(empId)) // Exclude the transferred employee
            .filter(id -> !id.equals(performerId)) // Exclude performer to avoid duplicate
            .toList();

    List<String> newDeptMemberIds =
        departmentEmployeeRepository.getEmployeesByDepartmentCode(toDept).stream()
            .map(de -> de.getEmployee().getClientId())
            .filter(id -> !id.equals(performerId)) // Exclude performer to avoid duplicate
            .toList();

    // 3. Execution (use repository directly to avoid duplicate notifications)
    AssignEmployeeToDepartmentRequest addRequest =
        departmentEmployeeConverter.toAssignEmployeeToDepartmentRequest(request);

    departmentEmployeeRepository.removeEmployeeFromDepartment(fromDept, empId);
    departmentEmployeeRepository.addEmployeeToDepartment(addRequest, toDept);

    // Send notifications
    sendTransferNotifications(
        empId, employeeName, fromDept, toDept, oldDeptMemberIds, newDeptMemberIds);

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
  @Transactional
  @CacheEvict(value = "user-principal", allEntries = true)
  public DepartmentEmployeeBatchResultDto addEmployeesToDepartment(
      List<AssignEmployeeToDepartmentRequest> requests, String departmentCode) {
    departmentService.validateExists(departmentCode);
    String performerId = userContentProvider.getUserContent().getClientId();

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

      // Send batch notification to added employees
      Map<String, String> addedParams = Map.of("DEPARTMENT_CODE", departmentCode);
      publishMultiNotification(
          result.getSuccessfulIds(),
          "S_DEP_EMP_TITLE_006",
          "S_DEP_EMP_CONTENT_006",
          addedParams,
          DEPARTMENTS_PATH + departmentCode);

      // Notify all existing members in the department about new additions
      List<String> newEmployeeIds = result.getSuccessfulIds();
      List<String> existingMemberIds =
          departmentEmployeeRepository.getEmployeesByDepartmentCode(departmentCode).stream()
              .map(de -> de.getEmployee().getClientId())
              .filter(id -> !newEmployeeIds.contains(id)) // Exclude newly added employees
              .filter(id -> !id.equals(performerId)) // Exclude performer to avoid duplicate
              .toList();

      if (!existingMemberIds.isEmpty()) {
        // Get names of added employees
        List<EmployeeDto> addedEmployees =
            newEmployeeIds.stream().map(employeeService::findByClientId).toList();
        String employeeNames = formatEmployeeNames(addedEmployees);

        Map<String, String> existingParams =
            Map.of(
                "EMPLOYEE_NAMES", employeeNames,
                "DEPARTMENT_CODE", departmentCode);
        publishMultiNotification(
            existingMemberIds,
            "S_DEP_EMP_TITLE_007",
            "S_DEP_EMP_CONTENT_007",
            existingParams,
            DEPARTMENTS_PATH + departmentCode);
      }

      // Notify performer about successful additions
      if (!newEmployeeIds.contains(performerId)) { // Don't notify if performer added themselves
        String countText = newEmployeeIds.size() + " " + LOG_EMPLOYEE_UNIT;

        Map<String, String> performerParams =
            Map.of(
                "COUNT", countText,
                "DEPARTMENT_CODE", departmentCode);
        publishSingleNotification(
            performerId,
            "S_DEP_EMP_TITLE_008",
            "S_DEP_EMP_CONTENT_008",
            performerParams,
            DEPARTMENTS_PATH + departmentCode);
      }
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
  @Transactional
  @CacheEvict(value = "user-principal", allEntries = true)
  public DepartmentEmployeeBatchResultDto removeEmployeesFromDepartmentBatch(
      List<String> employeeIds, String departmentCode) {
    departmentService.validateExists(departmentCode);
    String performerId = userContentProvider.getUserContent().getClientId();

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
      // Get all current members before removal for notification
      List<String> allCurrentMemberIds =
          departmentEmployeeRepository.getEmployeesByDepartmentCode(departmentCode).stream()
              .map(de -> de.getEmployee().getClientId())
              .toList();

      departmentEmployeeRepository.removeEmployeesFromDepartmentBatch(
          result.getToInsert(), departmentCode);

      // Send batch notification to removed employees
      Map<String, String> removedParams = Map.of("DEPARTMENT_CODE", departmentCode);
      publishMultiNotification(
          result.getSuccessfulIds(),
          "S_DEP_EMP_TITLE_009",
          "S_DEP_EMP_CONTENT_009",
          removedParams,
          DEPARTMENTS_PATH + departmentCode);

      // Notify all remaining members about the removals
      List<String> remainingMemberIds =
          allCurrentMemberIds.stream()
              .filter(id -> !result.getSuccessfulIds().contains(id))
              .filter(id -> !id.equals(performerId)) // Exclude performer to avoid duplicate
              .toList();

      if (!remainingMemberIds.isEmpty()) {
        // Get names of removed employees
        List<EmployeeDto> removedEmployees =
            result.getSuccessfulIds().stream().map(employeeService::findByClientId).toList();
        String employeeNames = formatEmployeeNames(removedEmployees);

        Map<String, String> remainingParams =
            Map.of(
                "EMPLOYEE_NAMES", employeeNames,
                "DEPARTMENT_CODE", departmentCode);
        publishMultiNotification(
            remainingMemberIds,
            "S_DEP_EMP_TITLE_010",
            "S_DEP_EMP_CONTENT_010",
            remainingParams,
            DEPARTMENTS_PATH + departmentCode);
      }

      // Notify performer about successful removals
      if (!result
          .getSuccessfulIds()
          .contains(performerId)) { // Don't notify if performer removed themselves
        String countText = result.getSuccessfulIds().size() + " " + LOG_EMPLOYEE_UNIT;
        Map<String, String> performerParams =
            Map.of(
                "COUNT", countText,
                "DEPARTMENT_CODE", departmentCode);
        publishSingleNotification(
            performerId,
            "S_DEP_EMP_TITLE_011",
            "S_DEP_EMP_CONTENT_011",
            performerParams,
            DEPARTMENTS_PATH + departmentCode);
      }
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
  @Transactional
  @CacheEvict(value = "user-principal", key = "#employeeId")
  public Map<String, String> changeEmployeeRoleInDepartment(
      String departmentCode, String employeeId, String roleCode) {
    departmentService.validateExists(departmentCode);
    employeeService.validateExists(employeeId);
    departmentEmployeeRepository.changeEmployeeRole(departmentCode, employeeId, roleCode);

    // Send notifications
    Map<String, String> employeeParams =
        Map.of(
            "DEPARTMENT_CODE", departmentCode,
            "ROLE_CODE", roleCode);
    publishSingleNotification(
        employeeId,
        "S_DEP_EMP_TITLE_012",
        "S_DEP_EMP_CONTENT_012",
        employeeParams,
        DEPARTMENTS_PATH + departmentCode);

    // Notify the performer
    String performedBy = userContentProvider.getUserContent().getClientId();
    if (!performedBy.equals(employeeId)) {
      Map<String, String> performerParams =
          Map.of(
              "DEPARTMENT_CODE", departmentCode,
              "ROLE_CODE", roleCode);
      publishSingleNotification(
          performedBy,
          "S_DEP_EMP_TITLE_013",
          "S_DEP_EMP_CONTENT_013",
          performerParams,
          DEPARTMENTS_PATH + departmentCode);
    }

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

  // ========================= NOTIFICATION HELPER METHODS =========================

  /**
   * Format a list of employees into a readable string (e.g., "John Doe, Jane Smith and 2 others").
   *
   * @param employees list of EmployeeDto
   * @return formatted string of employee names
   */
  private String formatEmployeeNames(List<EmployeeDto> employees) {
    if (employees.isEmpty()) {
      return "";
    }

    if (employees.size() == 1) {
      EmployeeDto emp = employees.get(0);
      return emp.getFirstName() + " " + emp.getLastName();
    }

    if (employees.size() == 2) {
      EmployeeDto emp1 = employees.get(0);
      EmployeeDto emp2 = employees.get(1);
      return emp1.getFirstName()
          + " "
          + emp1.getLastName()
          + " and "
          + emp2.getFirstName()
          + " "
          + emp2.getLastName();
    }

    // For 3+ employees: show first 2 names + "and X others"
    EmployeeDto emp1 = employees.get(0);
    EmployeeDto emp2 = employees.get(1);
    int remaining = employees.size() - 2;
    return emp1.getFirstName()
        + " "
        + emp1.getLastName()
        + ", "
        + emp2.getFirstName()
        + " "
        + emp2.getLastName()
        + " and "
        + remaining
        + " other"
        + (remaining > 1 ? "s" : "");
  }

  /**
   * Send all notifications for employee transfer.
   *
   * @param employeeId transferred employee ID
   * @param employeeName transferred employee name
   * @param fromDepartment source department
   * @param toDepartment destination department
   * @param oldDeptMemberIds members in old department
   * @param newDeptMemberIds members in new department
   */
  private void sendTransferNotifications(
      String employeeId,
      String employeeName,
      String fromDepartment,
      String toDepartment,
      List<String> oldDeptMemberIds,
      List<String> newDeptMemberIds) {

    // Notify the transferred employee
    Map<String, String> params =
        Map.of(
            "FROM_DEPARTMENT", fromDepartment,
            "TO_DEPARTMENT", toDepartment);
    publishSingleNotification(
        employeeId,
        "S_DEP_EMP_TITLE_001",
        "S_DEP_EMP_CONTENT_001",
        params,
        DEPARTMENTS_PATH + toDepartment);

    // Notify all members in both departments
    List<String> allDeptMemberIds = new ArrayList<>(oldDeptMemberIds);
    allDeptMemberIds.addAll(newDeptMemberIds);

    List<String> uniqueMemberIds =
        allDeptMemberIds.stream().distinct().filter(id -> !id.equals(employeeId)).toList();

    if (!uniqueMemberIds.isEmpty()) {
      Map<String, String> multiParams =
          Map.of(
              "EMPLOYEE_NAME", employeeName,
              "FROM_DEPARTMENT", fromDepartment,
              "TO_DEPARTMENT", toDepartment);
      publishMultiNotification(
          uniqueMemberIds,
          "S_DEP_EMP_TITLE_002",
          "S_DEP_EMP_CONTENT_002",
          multiParams,
          DEPARTMENTS_PATH + toDepartment);
    }

    // Notify the performer
    String performedBy = userContentProvider.getUserContent().getClientId();
    if (!performedBy.equals(employeeId)) {
      Map<String, String> performerParams =
          Map.of(
              "EMPLOYEE_NAME", employeeName,
              "FROM_DEPARTMENT", fromDepartment,
              "TO_DEPARTMENT", toDepartment);
      publishSingleNotification(
          performedBy,
          "S_DEP_EMP_TITLE_003",
          "S_DEP_EMP_CONTENT_003",
          performerParams,
          DEPARTMENTS_PATH + toDepartment);
    }
  }

  /**
   * Helper to publish single notification event.
   *
   * @param receiverId the receiver's ID
   * @param title title
   * @param content content
   * @param parameters parameters map
   * @param linkUrl link URL
   */
  private void publishSingleNotification(
      String receiverId,
      String title,
      String content,
      Map<String, String> parameters,
      String linkUrl) {
    eventPublisher.publishEvent(
        new NotificationSendEvent(this, receiverId, title, content, parameters, linkUrl));
  }

  /**
   * Helper to publish multi notification event.
   *
   * @param receiverIds list of receiver IDs
   * @param title title
   * @param content content
   * @param parameters parameters map
   * @param linkUrl link URL
   */
  private void publishMultiNotification(
      List<String> receiverIds,
      String title,
      String content,
      Map<String, String> parameters,
      String linkUrl) {
    eventPublisher.publishEvent(
        new NotificationMultiSendRequest(this, receiverIds, title, content, parameters, linkUrl));
  }
}
