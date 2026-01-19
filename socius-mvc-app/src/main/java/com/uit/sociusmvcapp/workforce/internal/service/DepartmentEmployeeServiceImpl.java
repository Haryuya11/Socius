package com.uit.sociusmvcapp.workforce.internal.service;

import com.uit.sociusmvcapp.department.DepartmentService;
import com.uit.sociusmvcapp.department.dto.DepartmentDto;
import com.uit.sociusmvcapp.employee.EmployeeService;
import com.uit.sociusmvcapp.iam.UserContentProvider;
import com.uit.sociusmvcapp.notification.NotificationService;
import com.uit.sociusmvcapp.shared.constants.MessageConstant;
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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

  private final ApplicationEventPublisher eventPublisher;

  private final UserContentProvider userContentProvider;

  private final NotificationService notificationService;

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
  public void addEmployeeToDepartment(
      AssignEmployeeToDepartmentRequest request, String departmentCode) {
    departmentService.validateExists(departmentCode);

    employeeService.validateExists(request.getEmployeeId());
    departmentEmployeeRepository.addEmployeeToDepartment(request, departmentCode);

    // Send notification to the added employee
    DepartmentDto department = departmentService.findByDepartmentCode(departmentCode);
    sendEmployeeAddedNotification(request.getEmployeeId(), department, departmentCode);
  }

  /**
   * Remove an employee from a department.
   *
   * @param departmentCode the code of the department
   * @param employeeId the client ID of the employee to remove
   */
  @Override
  @Transactional
  public void removeEmployeeFromDepartment(String departmentCode, String employeeId) {
    departmentService.validateExists(departmentCode);
    employeeService.validateExists(employeeId);
    departmentEmployeeRepository.removeEmployeeFromDepartment(departmentCode, employeeId);

    // Send notification to the removed employee
    DepartmentDto department = departmentService.findByDepartmentCode(departmentCode);
    sendEmployeeRemovedNotification(employeeId, department, departmentCode);
  }

  /**
   * Transfer an employee from one department to another.
   *
   * @param request the request containing transfer details
   * @return Map containing transfer details
   */
  @Override
  @Transactional
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
    var employee = employeeService.findByClientId(empId);
    String employeeName = employee.getFirstName() + " " + employee.getLastName();

    // Get department information for notifications
    DepartmentDto fromDepartment = departmentService.findByDepartmentCode(fromDept);
    DepartmentDto toDepartment = departmentService.findByDepartmentCode(toDept);

    // Get all members in both departments BEFORE the transfer
    List<String> oldDeptMemberIds =
        departmentEmployeeRepository.getEmployeesByDepartmentCode(fromDept).stream()
            .map(de -> de.getEmployee().getClientId())
            .filter(id -> !id.equals(empId)) // Exclude the transferred employee
            .toList();

    List<String> newDeptMemberIds =
        departmentEmployeeRepository.getEmployeesByDepartmentCode(toDept).stream()
            .map(de -> de.getEmployee().getClientId())
            .toList();

    // 3. Execution (use repository directly to avoid duplicate notifications)
    AssignEmployeeToDepartmentRequest addRequest =
        departmentEmployeeConverter.toAssignEmployeeToDepartmentRequest(request);

    departmentEmployeeRepository.removeEmployeeFromDepartment(fromDept, empId);
    departmentEmployeeRepository.addEmployeeToDepartment(addRequest, toDept);

    // Send notifications
    sendTransferNotifications(
        empId,
        employeeName,
        fromDepartment,
        toDepartment,
        fromDept,
        toDept,
        oldDeptMemberIds,
        newDeptMemberIds);

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

      // Send batch notification to added employees
      DepartmentDto department = departmentService.findByDepartmentCode(departmentCode);
      notificationService.sendMultiNotification(
          result.getSuccessfulIds(),
          "Added to Department",
          String.format(
              "You have been added to department '%s' (%s).",
              department.getDepartmentName(), departmentCode),
          "/departments/" + departmentCode);

      // Notify all existing members in the department about new additions
      List<String> newEmployeeIds = result.getSuccessfulIds();
      List<String> existingMemberIds =
          departmentEmployeeRepository.getEmployeesByDepartmentCode(departmentCode).stream()
              .map(de -> de.getEmployee().getClientId())
              .filter(id -> !newEmployeeIds.contains(id)) // Exclude newly added employees
              .toList();

      if (!existingMemberIds.isEmpty()) {
        String countText =
            newEmployeeIds.size() == 1
                ? "1 employee"
                : String.format("%d employees", newEmployeeIds.size());
        notificationService.sendMultiNotification(
            existingMemberIds,
            "New Department Members",
            String.format(
                "%s added to department '%s' (%s).",
                countText, department.getDepartmentName(), departmentCode),
            "/departments/" + departmentCode);
      }

      // Notify performer about successful additions
      String performerId = userContentProvider.getUserContent().getClientId();
      if (!newEmployeeIds.contains(performerId)) { // Don't notify if performer added themselves
        String countText =
            newEmployeeIds.size() == 1
                ? "1 employee"
                : String.format("%d employees", newEmployeeIds.size());
        eventPublisher.publishEvent(
            new NotificationSendEvent(
                this,
                performerId,
                "Employees Added to Department",
                String.format(
                    "Successfully added %s to department '%s' (%s).",
                    countText, department.getDepartmentName(), departmentCode),
                "/departments/" + departmentCode));
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
      // Get all current members before removal for notification
      List<String> allCurrentMemberIds =
          departmentEmployeeRepository.getEmployeesByDepartmentCode(departmentCode).stream()
              .map(de -> de.getEmployee().getClientId())
              .toList();

      departmentEmployeeRepository.removeEmployeesFromDepartmentBatch(
          result.getToInsert(), departmentCode);

      // Send batch notification to removed employees
      DepartmentDto department = departmentService.findByDepartmentCode(departmentCode);
      notificationService.sendMultiNotification(
          result.getSuccessfulIds(),
          "Removed from Department",
          String.format(
              "You have been removed from department '%s' (%s).",
              department.getDepartmentName(), departmentCode),
          "/departments");

      // Notify all remaining members about the removals
      List<String> remainingMemberIds =
          allCurrentMemberIds.stream()
              .filter(id -> !result.getSuccessfulIds().contains(id))
              .toList();

      if (!remainingMemberIds.isEmpty()) {
        String countText =
            result.getSuccessfulIds().size() == 1
                ? "1 member"
                : String.format("%d members", result.getSuccessfulIds().size());
        notificationService.sendMultiNotification(
            remainingMemberIds,
            "Department Members Removed",
            String.format(
                "%s removed from department '%s' (%s).",
                countText, department.getDepartmentName(), departmentCode),
            "/departments/" + departmentCode);
      }

      // Notify performer about successful removals
      String performerId = userContentProvider.getUserContent().getClientId();
      if (!result
          .getSuccessfulIds()
          .contains(performerId)) { // Don't notify if performer removed themselves
        String countText =
            result.getSuccessfulIds().size() == 1
                ? "1 employee"
                : String.format("%d employees", result.getSuccessfulIds().size());
        eventPublisher.publishEvent(
            new NotificationSendEvent(
                this,
                performerId,
                "Employees Removed from Department",
                String.format(
                    "Successfully removed %s from department '%s' (%s).",
                    countText, department.getDepartmentName(), departmentCode),
                "/departments/" + departmentCode));
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
  public Map<String, String> changeEmployeeRoleInDepartment(
      String departmentCode, String employeeId, String roleCode) {
    departmentService.validateExists(departmentCode);
    employeeService.validateExists(employeeId);
    departmentEmployeeRepository.changeEmployeeRole(departmentCode, employeeId, roleCode);

    // Send notifications
    DepartmentDto department = departmentService.findByDepartmentCode(departmentCode);
    sendRoleChangeNotifications(employeeId, department, departmentCode, roleCode);

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
   * Send notification to employee added to department.
   *
   * @param employeeId employee ID
   * @param department department DTO
   * @param departmentCode department code
   */
  private void sendEmployeeAddedNotification(
      String employeeId, DepartmentDto department, String departmentCode) {
    eventPublisher.publishEvent(
        new NotificationSendEvent(
            this,
            employeeId,
            "Added to Department",
            String.format(
                "You have been added to department '%s' (%s).",
                department.getDepartmentName(), departmentCode),
            "/departments/" + departmentCode));
  }

  /**
   * Send notification to employee removed from department.
   *
   * @param employeeId employee ID
   * @param department department DTO
   * @param departmentCode department code
   */
  private void sendEmployeeRemovedNotification(
      String employeeId, DepartmentDto department, String departmentCode) {
    eventPublisher.publishEvent(
        new NotificationSendEvent(
            this,
            employeeId,
            "Removed from Department",
            String.format(
                "You have been removed from department '%s' (%s).",
                department.getDepartmentName(), departmentCode),
            "/departments"));
  }

  /**
   * Send all notifications for employee transfer.
   *
   * @param employeeId transferred employee ID
   * @param employeeName transferred employee name
   * @param fromDepartment source department
   * @param toDepartment destination department
   * @param fromDeptCode source department code
   * @param toDeptCode destination department code
   * @param oldDeptMemberIds members in old department
   * @param newDeptMemberIds members in new department
   */
  private void sendTransferNotifications(
      String employeeId,
      String employeeName,
      DepartmentDto fromDepartment,
      DepartmentDto toDepartment,
      String fromDeptCode,
      String toDeptCode,
      List<String> oldDeptMemberIds,
      List<String> newDeptMemberIds) {

    // Notify the transferred employee
    eventPublisher.publishEvent(
        new NotificationSendEvent(
            this,
            employeeId,
            "Department Transfer",
            String.format(
                "You have been transferred from department '%s' (%s) to department '%s' (%s).",
                fromDepartment.getDepartmentName(),
                fromDeptCode,
                toDepartment.getDepartmentName(),
                toDeptCode),
            "/departments/" + toDeptCode));

    // Notify all members in both departments
    List<String> allDeptMemberIds = new ArrayList<>(oldDeptMemberIds);
    allDeptMemberIds.addAll(newDeptMemberIds);

    List<String> uniqueMemberIds =
        allDeptMemberIds.stream().distinct().filter(id -> !id.equals(employeeId)).toList();

    if (!uniqueMemberIds.isEmpty()) {
      notificationService.sendMultiNotification(
          uniqueMemberIds,
          "Department Member Transfer",
          String.format(
              "%s has been transferred from department '%s' (%s) to department '%s' (%s).",
              employeeName,
              fromDepartment.getDepartmentName(),
              fromDeptCode,
              toDepartment.getDepartmentName(),
              toDeptCode),
          "/departments/" + toDeptCode);
    }

    // Notify the performer
    String performedBy = userContentProvider.getUserContent().getClientId();
    if (!performedBy.equals(employeeId)) {
      eventPublisher.publishEvent(
          new NotificationSendEvent(
              this,
              performedBy,
              "Department Transfer Completed",
              String.format(
                  "%s has been successfully transferred from department '%s' (%s) "
                      + "to department '%s' (%s).",
                  employeeName,
                  fromDepartment.getDepartmentName(),
                  fromDeptCode,
                  toDepartment.getDepartmentName(),
                  toDeptCode),
              "/departments/" + toDeptCode));
    }
  }

  /**
   * Send notifications for role change in department.
   *
   * @param employeeId employee ID
   * @param department department DTO
   * @param departmentCode department code
   * @param roleCode new role code
   */
  private void sendRoleChangeNotifications(
      String employeeId, DepartmentDto department, String departmentCode, String roleCode) {

    // Notify the employee
    eventPublisher.publishEvent(
        new NotificationSendEvent(
            this,
            employeeId,
            "Department Role Changed",
            String.format(
                "Your role in department '%s' (%s) has been changed to '%s'.",
                department.getDepartmentName(), departmentCode, roleCode),
            "/departments/" + departmentCode));

    // Notify the performer
    String performedBy = userContentProvider.getUserContent().getClientId();
    if (!performedBy.equals(employeeId)) {
      eventPublisher.publishEvent(
          new NotificationSendEvent(
              this,
              performedBy,
              "Department Role Changed",
              String.format(
                  "Employee's role in department '%s' (%s) has been changed to '%s'.",
                  department.getDepartmentName(), departmentCode, roleCode),
              "/departments/" + departmentCode));
    }
  }
}
