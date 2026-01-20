package com.uit.sociusmvcapp.workforce.internal.service;

import com.uit.sociusmvcapp.employee.EmployeeService;
import com.uit.sociusmvcapp.employee.dto.EmployeeDto;
import com.uit.sociusmvcapp.iam.UserContentProvider;
import com.uit.sociusmvcapp.iam.enums.TeamRoleEnums;
import com.uit.sociusmvcapp.shared.constants.MessageConstant;
import com.uit.sociusmvcapp.shared.event.NotificationMultiSendRequest;
import com.uit.sociusmvcapp.shared.event.NotificationSendEvent;
import com.uit.sociusmvcapp.shared.service.ExceptionFactory;
import com.uit.sociusmvcapp.shared.service.I18nService;
import com.uit.sociusmvcapp.team.TeamService;
import com.uit.sociusmvcapp.workforce.TeamEmployeeService;
import com.uit.sociusmvcapp.workforce.dto.BatchErrorDto;
import com.uit.sociusmvcapp.workforce.dto.TeamEmployeeBatchResultDto;
import com.uit.sociusmvcapp.workforce.dto.TeamEmployeeDto;
import com.uit.sociusmvcapp.workforce.dto.TeamEmployeeRemovalResultDto;
import com.uit.sociusmvcapp.workforce.dto.request.AddEmployeeToTeamRequest;
import com.uit.sociusmvcapp.workforce.dto.request.AddEmployeesToTeamRequest;
import com.uit.sociusmvcapp.workforce.dto.request.RemoveEmployeeFromTeamRequest;
import com.uit.sociusmvcapp.workforce.dto.request.RemoveEmployeesFromTeamRequest;
import com.uit.sociusmvcapp.workforce.dto.request.TransferTeamEmployeeRequest;
import com.uit.sociusmvcapp.workforce.internal.constants.TeamEmployeeConstant;
import com.uit.sociusmvcapp.workforce.internal.converter.TeamEmployeeConverter;
import com.uit.sociusmvcapp.workforce.internal.repository.TeamEmployeeRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Implementation of TeamEmployeeService for team-employee relationship operations. */
@Slf4j
@Service
@RequiredArgsConstructor
public class TeamEmployeeServiceImpl implements TeamEmployeeService {

  private static final String LOG_EMPLOYEE_UNIT = "employee(s)";

  private static final String TEAMS_PATH = "/teams/";

  private final TeamEmployeeRepository teamEmployeeRepository;
  private final TeamService teamService;
  private final EmployeeService employeeService;
  private final TeamEmployeeConverter teamEmployeeConverter;
  private final I18nService i18nService;
  private final ApplicationEventPublisher eventPublisher;
  private final UserContentProvider userContentProvider;

  /**
   * Get all employees in a team.
   *
   * @param teamCode the team code
   * @return list of TeamEmployeeDto
   */
  @Override
  public List<TeamEmployeeDto> getEmployeesByTeamCode(String teamCode) {
    teamService.validateExists(teamCode);
    return teamEmployeeRepository.findEmployeesByTeamCode(teamCode);
  }

  /**
   * Add multiple employees to a team in batch.
   *
   * @param teamCode the team code
   * @param request the request containing employees to add
   * @return TeamEmployeeBatchResultDto with results of the operation
   */
  @Override
  @Transactional
  public TeamEmployeeBatchResultDto addEmployeesToTeam(
      String teamCode, AddEmployeesToTeamRequest request) {
    teamService.validateExists(teamCode);

    List<TeamEmployeeDto> successful = new ArrayList<>();
    List<BatchErrorDto> failed = new ArrayList<>();

    // Step 1: Validate employees and filter out invalid ones
    List<AddEmployeeToTeamRequest> validRequests =
        validateAndFilterInvalidEmployees(request.getEmployees(), failed);
    if (validRequests.isEmpty()) {
      return buildResult(successful, failed);
    }

    // Step 2: Filter out already active employees
    List<AddEmployeeToTeamRequest> requestsToProcess =
        filterAlreadyActiveEmployees(teamCode, validRequests, failed);
    if (requestsToProcess.isEmpty()) {
      return buildResult(successful, failed);
    }

    // Step 3: Auto-assign role codes
    assignRoleCodes(requestsToProcess);

    // Step 4: Separate reactivate vs new insert based on soft-deleted status
    SeparatedRequests separatedRequests = separateReactivateAndInsert(teamCode, requestsToProcess);

    // Step 5: Validate team lead constraint
    if (!validateLeaderConstraint(teamCode, requestsToProcess, failed)) {
      return buildResult(successful, failed);
    }

    // Step 6: Execute batch operations (reactivate + insert)
    executeInsertions(teamCode, separatedRequests);

    // Step 7: Fetch results for processed requests
    List<TeamEmployeeDto> results = fetchResults(teamCode, requestsToProcess);
    successful.addAll(results);

    // Step 8: Send batch notifications
    if (!results.isEmpty()) {
      List<String> newEmployeeIds =
          results.stream().map(te -> te.getEmployee().getClientId()).toList();
      sendBatchAddNotifications(results, teamCode, newEmployeeIds);
    }

    return buildResult(successful, failed);
  }

  /**
   * Remove multiple employees from a team in batch.
   *
   * @param teamCode the team code
   * @param request the request containing employees to remove
   * @return TeamEmployeeRemovalResultDto with results of the operation
   */
  @Override
  @Transactional
  public TeamEmployeeRemovalResultDto removeEmployeesFromTeam(
      String teamCode, RemoveEmployeesFromTeamRequest request) {
    teamService.validateExists(teamCode);

    List<String> removedEmployeeIds = new ArrayList<>();
    List<BatchErrorDto> failed = new ArrayList<>();

    // Batch validate: get all employees in team with 1 query
    List<TeamEmployeeDto> allTeamEmployees =
        teamEmployeeRepository.findEmployeesByTeamCode(teamCode);
    List<String> existingEmployeeIds =
        allTeamEmployees.stream().map(te -> te.getEmployee().getClientId()).toList();

    // Separate valid and invalid employees
    List<RemoveEmployeeFromTeamRequest> validRequests =
        request.getEmployees().stream()
            .filter(
                req -> {
                  if (!existingEmployeeIds.contains(req.getEmployeeId())) {
                    failed.add(
                        buildBatchError(
                            req.getEmployeeId(),
                            i18nService.getMessage(MessageConstant.E_TEAM_EMP_002)));
                    return false;
                  }
                  return true;
                })
            .toList();

    // Batch soft delete valid employees only
    if (!validRequests.isEmpty()) {
      List<String> validEmployeeIds =
          validRequests.stream().map(RemoveEmployeeFromTeamRequest::getEmployeeId).toList();

      teamEmployeeRepository.batchRemoveEmployees(teamCode, validEmployeeIds);
      removedEmployeeIds.addAll(validEmployeeIds);

      // Send batch notification to removed employees
      publishMultiNotification(
          validEmployeeIds,
          "Removed from Team",
          String.format("You have been removed from team (%s).", teamCode),
          "/teams");

      // Notify all remaining members about the removals
      String performerId = userContentProvider.getUserContent().getClientId();
      List<String> remainingMemberIds =
          allTeamEmployees.stream()
              .map(te -> te.getEmployee().getClientId())
              .filter(id -> !validEmployeeIds.contains(id))
              .filter(id -> !id.equals(performerId)) // Exclude performer to avoid duplicate
              .toList();

      if (!remainingMemberIds.isEmpty()) {
        // Fetch all employee details in one batch query to avoid N+1
        List<EmployeeDto> removedEmployees = employeeService.findByClientIds(validEmployeeIds);
        String employeeNames = formatEmployeeNames(removedEmployees);

        // Send notification to remaining members
        publishMultiNotification(
            remainingMemberIds,
            "Team Members Removed",
            String.format("%s removed from team (%s).", employeeNames, teamCode),
            TEAMS_PATH + teamCode);
      }

      // Notify performer about successful removals (only if they didn't remove themselves)
      if (!validEmployeeIds.contains(performerId)) {
        String countText = validEmployeeIds.size() + " " + LOG_EMPLOYEE_UNIT;
        publishSingleNotification(
            performerId,
            "Team Members Removed",
            String.format("Successfully removed %s from team (%s).", countText, teamCode),
            TEAMS_PATH + teamCode);
      }
    }

    return TeamEmployeeRemovalResultDto.builder()
        .removedEmployeeIds(removedEmployeeIds)
        .failed(failed)
        .build();
  }

  /**
   * Change the team lead of a team.
   *
   * @param teamCode the team code
   * @param newLeadEmployeeId the employee ID of the new team lead
   * @return TeamEmployeeDto of the new team lead
   */
  @Override
  @Transactional
  public TeamEmployeeDto changeTeamLead(String teamCode, String newLeadEmployeeId) {
    teamService.validateExists(teamCode);
    employeeService.validateExists(newLeadEmployeeId);
    validateEmployeeExistsInTeam(teamCode, newLeadEmployeeId);

    TeamEmployeeDto currentLead = teamEmployeeRepository.findTeamLeadByTeamCode(teamCode);
    if (currentLead != null && !currentLead.getEmployee().getClientId().equals(newLeadEmployeeId)) {
      teamEmployeeRepository.updateLeadershipStatus(
          teamCode, currentLead.getEmployee().getClientId(), false);
      teamEmployeeRepository.updateRoleCode(
          teamCode, currentLead.getEmployee().getClientId(), TeamRoleEnums.TEAM_MEM.getRoleCode());
    }

    teamEmployeeRepository.updateLeadershipStatus(teamCode, newLeadEmployeeId, true);
    teamEmployeeRepository.updateRoleCode(
        teamCode, newLeadEmployeeId, TeamRoleEnums.TEAM_LEAD.getRoleCode());

    // Send notification to new team lead
    publishSingleNotification(
        newLeadEmployeeId,
        "Promoted to Team Lead",
        String.format("You have been promoted to Team Lead of (%s).", teamCode),
        TEAMS_PATH + teamCode);

    // Send notification to previous lead if exists and different
    if (currentLead != null && !currentLead.getEmployee().getClientId().equals(newLeadEmployeeId)) {
      publishSingleNotification(
          currentLead.getEmployee().getClientId(),
          "Team Lead Role Changed",
          String.format("Your Team Lead role for (%s) has been transferred.", teamCode),
          TEAMS_PATH + teamCode);
    }

    return teamEmployeeRepository.findByTeamCodeAndEmployeeId(teamCode, newLeadEmployeeId);
  }

  /**
   * Transfer an employee from one team to another.
   *
   * @param request the request containing transfer details
   */
  @Override
  @Transactional
  public void transferEmployee(TransferTeamEmployeeRequest request) {
    teamService.validateExists(request.getFromTeamCode());
    teamService.validateExists(request.getToTeamCode());

    TeamEmployeeDto existingEmployeeInFromTeam =
        teamEmployeeRepository.findByTeamCodeAndEmployeeId(
            request.getFromTeamCode(), request.getEmployeeId());
    if (existingEmployeeInFromTeam == null) {
      throw ExceptionFactory.notFound(MessageConstant.E_TEAM_EMP_002);
    }

    TeamEmployeeDto existingEmployeeInToTeam =
        teamEmployeeRepository.findByTeamCodeAndEmployeeId(
            request.getToTeamCode(), request.getEmployeeId());
    if (existingEmployeeInToTeam != null) {
      throw ExceptionFactory.badRequest(MessageConstant.E_TEAM_EMP_001);
    }

    AddEmployeeToTeamRequest addRequest = teamEmployeeConverter.toTeamEmployeeAddRequest(request);

    validateTeamLeadConstraint(request.getToTeamCode(), request.getIsLeader());

    // Get employee info for notification messages
    String employeeName = getEmployeeFullName(existingEmployeeInFromTeam);

    // Get all members in both teams BEFORE the transfer
    String performerId = userContentProvider.getUserContent().getClientId();
    List<String> oldTeamMemberIds =
        teamEmployeeRepository.findEmployeesByTeamCode(request.getFromTeamCode()).stream()
            .map(te -> te.getEmployee().getClientId())
            .filter(id -> !id.equals(request.getEmployeeId())) // Exclude the transferred employee
            .filter(id -> !id.equals(performerId)) // Exclude performer to avoid duplicate
            .toList();

    List<String> newTeamMemberIds =
        teamEmployeeRepository.findEmployeesByTeamCode(request.getToTeamCode()).stream()
            .map(te -> te.getEmployee().getClientId())
            .filter(id -> !id.equals(performerId)) // Exclude performer to avoid duplicate
            .toList();

    // Perform database operations directly to avoid duplicate notifications
    performTransferDatabaseOperations(request, addRequest);

    // Send notifications
    sendTransferNotifications(request, employeeName, oldTeamMemberIds, newTeamMemberIds);
  }

  // ========================= VALIDATION METHODS =========================

  private void validateEmployeeExistsInTeam(String teamCode, String employeeId) {
    TeamEmployeeDto teamEmployee =
        teamEmployeeRepository.findByTeamCodeAndEmployeeId(teamCode, employeeId);
    if (teamEmployee == null) {
      throw ExceptionFactory.badRequest(MessageConstant.E_TEAM_EMP_002);
    }
  }

  private void validateTeamLeadConstraint(String teamCode, Boolean isLeader) {
    if (Boolean.TRUE.equals(isLeader)
        && Boolean.TRUE.equals(teamEmployeeRepository.existsTeamLeadByTeamCode(teamCode))) {
      throw ExceptionFactory.badRequest(MessageConstant.E_TEAM_006);
    }
  }

  // ========================= BATCH OPERATION HELPERS =========================

  /**
   * Validate employees and filter out invalid ones.
   *
   * @param requests the list of requests to validate
   * @param failed the list to add failed operations to
   * @return list of valid requests
   */
  private List<AddEmployeeToTeamRequest> validateAndFilterInvalidEmployees(
      List<AddEmployeeToTeamRequest> requests, List<BatchErrorDto> failed) {
    List<String> invalidEmployees = new ArrayList<>();

    for (AddEmployeeToTeamRequest request : requests) {
      try {
        employeeService.validateExists(request.getEmployeeId());
      } catch (Exception e) {
        invalidEmployees.add(request.getEmployeeId());
        failed.add(
            buildBatchError(request.getEmployeeId(), i18nService.getMessage(e.getMessage())));
      }
    }

    return requests.stream()
        .filter(req -> !invalidEmployees.contains(req.getEmployeeId()))
        .toList();
  }

  /**
   * Filter out employees already active in the team.
   *
   * @param teamCode the team code
   * @param requests the list of valid requests
   * @param failed the list to add failed operations to
   * @return list of requests to process
   */
  private List<AddEmployeeToTeamRequest> filterAlreadyActiveEmployees(
      String teamCode, List<AddEmployeeToTeamRequest> requests, List<BatchErrorDto> failed) {
    List<String> employeeIds =
        requests.stream().map(AddEmployeeToTeamRequest::getEmployeeId).toList();

    List<String> alreadyActiveIds =
        teamEmployeeRepository.findByEmployeeIdIn(employeeIds).stream()
            .filter(te -> te.getTeam().getTeamCode().equals(teamCode))
            .map(te -> te.getEmployee().getClientId())
            .toList();

    alreadyActiveIds.forEach(
        employeeId ->
            failed.add(
                buildBatchError(
                    employeeId, i18nService.getMessage(MessageConstant.E_TEAM_EMP_001))));

    return requests.stream()
        .filter(req -> !alreadyActiveIds.contains(req.getEmployeeId()))
        .toList();
  }

  /**
   * Auto-assign role codes for requests without role codes.
   *
   * @param requests the list of requests
   */
  private void assignRoleCodes(List<AddEmployeeToTeamRequest> requests) {
    requests.forEach(
        req -> {
          if (req.getRoleCode() == null || req.getRoleCode().isEmpty()) {
            req.setRoleCode(determineRoleCode(req.getIsLeader()));
          }
        });
  }

  /**
   * Separate requests into reactivate and insert lists based on soft-deleted status.
   *
   * @param teamCode the team code
   * @param requests the list of requests to process
   * @return separated requests
   */
  private SeparatedRequests separateReactivateAndInsert(
      String teamCode, List<AddEmployeeToTeamRequest> requests) {
    List<String> softDeletedIds =
        teamEmployeeRepository
            .findSoftDeletedByTeamCodeAndEmployeeIds(
                teamCode, requests.stream().map(AddEmployeeToTeamRequest::getEmployeeId).toList())
            .stream()
            .map(te -> te.getEmployee().getClientId())
            .toList();

    List<AddEmployeeToTeamRequest> toReactivate =
        requests.stream().filter(req -> softDeletedIds.contains(req.getEmployeeId())).toList();

    List<AddEmployeeToTeamRequest> toInsert =
        requests.stream().filter(req -> !softDeletedIds.contains(req.getEmployeeId())).toList();

    return new SeparatedRequests(toReactivate, toInsert);
  }

  /**
   * Validate team lead constraint for all requests.
   *
   * @param teamCode the team code
   * @param requests the list of requests
   * @param failed the list to add failed operations to
   * @return true if validation passes, false otherwise
   */
  private boolean validateLeaderConstraint(
      String teamCode, List<AddEmployeeToTeamRequest> requests, List<BatchErrorDto> failed) {
    long leaderCount =
        requests.stream().filter(req -> Boolean.TRUE.equals(req.getIsLeader())).count();
    boolean teamHasLead = teamEmployeeRepository.existsTeamLeadByTeamCode(teamCode);

    if (leaderCount > TeamEmployeeConstant.MAX_TEAM_LEADERS
        || (leaderCount > TeamEmployeeConstant.NO_TEAM_LEADERS && teamHasLead)) {
      requests.stream()
          .filter(req -> Boolean.TRUE.equals(req.getIsLeader()))
          .forEach(
              req ->
                  failed.add(
                      buildBatchError(
                          req.getEmployeeId(),
                          i18nService.getMessage(MessageConstant.E_TEAM_006))));
      return false;
    }
    return true;
  }

  /**
   * Execute batch reactivate and insert operations.
   *
   * @param teamCode the team code
   * @param separatedRequests the separated requests
   */
  private void executeInsertions(String teamCode, SeparatedRequests separatedRequests) {
    if (!separatedRequests.toReactivate().isEmpty()) {
      teamEmployeeRepository.batchReactivate(teamCode, separatedRequests.toReactivate());
    }

    if (!separatedRequests.toInsert().isEmpty()) {
      teamEmployeeRepository.batchInsert(teamCode, separatedRequests.toInsert());
    }
  }

  /**
   * Fetch results for processed requests.
   *
   * @param teamCode the team code
   * @param requests the list of processed requests
   * @return list of TeamEmployeeDto
   */
  private List<TeamEmployeeDto> fetchResults(
      String teamCode, List<AddEmployeeToTeamRequest> requests) {
    List<String> processedIds =
        requests.stream().map(AddEmployeeToTeamRequest::getEmployeeId).toList();

    return teamEmployeeRepository.findByEmployeeIdIn(processedIds).stream()
        .filter(te -> te.getTeam().getTeamCode().equals(teamCode))
        .toList();
  }

  /**
   * Build batch result DTO.
   *
   * @param successful list of successful operations
   * @param failed list of failed operations
   * @return TeamEmployeeBatchResultDto
   */
  private TeamEmployeeBatchResultDto buildResult(
      List<TeamEmployeeDto> successful, List<BatchErrorDto> failed) {
    return TeamEmployeeBatchResultDto.builder().successful(successful).failed(failed).build();
  }

  /** Record to hold separated reactivate and insert requests. */
  private record SeparatedRequests(
      List<AddEmployeeToTeamRequest> toReactivate, List<AddEmployeeToTeamRequest> toInsert) {}

  // ========================= HELPER METHODS =========================

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
   * Send batch notifications for added employees.
   *
   * @param results the list of added team employees
   * @param teamCode the team code
   * @param newEmployeeIds the list of newly added employee IDs
   */
  private void sendBatchAddNotifications(
      List<TeamEmployeeDto> results, String teamCode, List<String> newEmployeeIds) {
    // Find leader and members (max 1 leader per team)
    String leaderId = null;
    List<String> memberIds = new ArrayList<>();

    for (TeamEmployeeDto result : results) {
      String empId = result.getEmployee().getClientId();
      if (Boolean.TRUE.equals(result.getIsLeader())) {
        leaderId = empId;
      } else {
        memberIds.add(empId);
      }
    }

    // Send notification to leader if exists
    if (leaderId != null) {
      publishSingleNotification(
          leaderId,
          "Added to Team",
          String.format("You have been added to team (%s) as Team Lead.", teamCode),
          TEAMS_PATH + teamCode);
    }

    // Send batch notification to members
    if (!memberIds.isEmpty()) {
      publishMultiNotification(
          memberIds,
          "Added to Team",
          String.format("You have been added to team (%s) as Member.", teamCode),
          TEAMS_PATH + teamCode);
    }

    // Notify existing members about new additions
    String performerId = userContentProvider.getUserContent().getClientId();
    List<String> existingMemberIds =
        teamEmployeeRepository.findEmployeesByTeamCode(teamCode).stream()
            .map(te -> te.getEmployee().getClientId())
            .filter(id -> !newEmployeeIds.contains(id))
            .filter(id -> !id.equals(performerId)) // Exclude performer to avoid duplicate
            .toList();

    if (!existingMemberIds.isEmpty()) {
      // Fetch all employee details in one batch query to avoid N+1
      List<EmployeeDto> addedEmployees = employeeService.findByClientIds(newEmployeeIds);
      String employeeNames = formatEmployeeNames(addedEmployees);

      publishMultiNotification(
          existingMemberIds,
          "New Team Members",
          String.format("%s joined team (%s).", employeeNames, teamCode),
          TEAMS_PATH + teamCode);
    }

    // Notify performer about successful additions (only if they didn't add themselves)
    if (!newEmployeeIds.contains(performerId)) {
      String countText = results.size() + " " + LOG_EMPLOYEE_UNIT;
      publishSingleNotification(
          performerId,
          "New Team Members",
          String.format("Successfully added %s to team (%s).", countText, teamCode),
          TEAMS_PATH + teamCode);
    }
  }

  /**
   * Get the full name of an employee from TeamEmployeeDto.
   *
   * @param teamEmployee the team employee DTO
   * @return full name (firstName + lastName)
   */
  private String getEmployeeFullName(TeamEmployeeDto teamEmployee) {
    return teamEmployee.getEmployee().getFirstName()
        + " "
        + teamEmployee.getEmployee().getLastName();
  }

  private String determineRoleCode(Boolean isLeader) {
    return Boolean.TRUE.equals(isLeader)
        ? TeamRoleEnums.TEAM_LEAD.getRoleCode()
        : TeamRoleEnums.TEAM_MEM.getRoleCode();
  }

  private BatchErrorDto buildBatchError(String employeeId, String errorMessage) {
    return BatchErrorDto.builder()
        .clientId(employeeId)
        .errorCode(MessageConstant.S_TEAM_EMP_001)
        .errorMessage(errorMessage)
        .build();
  }

  /**
   * Helper to publish single notification event.
   *
   * @param receiverId the receiver's ID
   * @param title title
   * @param content content
   * @param linkUrl link URL
   */
  private void publishSingleNotification(
      String receiverId, String title, String content, String linkUrl) {
    eventPublisher.publishEvent(
        new NotificationSendEvent(this, receiverId, title, content, linkUrl));
  }

  /**
   * Helper to publish multi notification event.
   *
   * @param receiverIds list of receiver IDs
   * @param title title
   * @param content content
   * @param linkUrl link URL
   */
  private void publishMultiNotification(
      List<String> receiverIds, String title, String content, String linkUrl) {
    eventPublisher.publishEvent(
        new NotificationMultiSendRequest(this, receiverIds, title, content, linkUrl));
  }

  // ========================= TRANSFER HELPER METHODS =========================

  /**
   * Perform database operations for team transfer.
   *
   * @param request transfer request
   * @param addRequest add employee request
   */
  private void performTransferDatabaseOperations(
      TransferTeamEmployeeRequest request, AddEmployeeToTeamRequest addRequest) {
    // Remove from old team (no notification)
    teamEmployeeRepository.batchRemoveEmployees(
        request.getFromTeamCode(), List.of(request.getEmployeeId()));

    // Add to new team (no notification)
    // Check if soft-deleted record exists for reactivation
    List<TeamEmployeeDto> softDeleted =
        teamEmployeeRepository.findSoftDeletedByTeamCodeAndEmployeeIds(
            request.getToTeamCode(), List.of(request.getEmployeeId()));

    if (!softDeleted.isEmpty()) {
      teamEmployeeRepository.batchReactivate(request.getToTeamCode(), List.of(addRequest));
    } else {
      teamEmployeeRepository.batchInsert(request.getToTeamCode(), List.of(addRequest));
    }
  }

  /**
   * Send all notifications for team transfer.
   *
   * @param request transfer request
   * @param employeeName transferred employee name
   * @param oldTeamMemberIds members in old team
   * @param newTeamMemberIds members in new team
   */
  private void sendTransferNotifications(
      TransferTeamEmployeeRequest request,
      String employeeName,
      List<String> oldTeamMemberIds,
      List<String> newTeamMemberIds) {

    String roleText = Boolean.TRUE.equals(request.getIsLeader()) ? "Team Lead" : "Team Member";

    // Notify the transferred employee
    publishSingleNotification(
        request.getEmployeeId(),
        "Team Transfer",
        String.format(
            "You have been transferred from team (%s) to team (%s) as %s.",
            request.getFromTeamCode(), request.getToTeamCode(), roleText),
        TEAMS_PATH + request.getToTeamCode());

    // Notify all members in both teams
    List<String> allTeamMemberIds = new java.util.ArrayList<>(oldTeamMemberIds);
    allTeamMemberIds.addAll(newTeamMemberIds);

    List<String> uniqueMemberIds =
        allTeamMemberIds.stream()
            .distinct()
            .filter(id -> !id.equals(request.getEmployeeId()))
            .toList();

    if (!uniqueMemberIds.isEmpty()) {
      publishMultiNotification(
          uniqueMemberIds,
          "Team Member Transfer",
          String.format(
              "%s has been transferred from team (%s) to team (%s) as %s.",
              employeeName, request.getFromTeamCode(), request.getToTeamCode(), roleText),
          TEAMS_PATH + request.getToTeamCode());
    }

    // Notify the performer
    String performedBy = userContentProvider.getUserContent().getClientId();
    if (!performedBy.equals(request.getEmployeeId())) {
      publishSingleNotification(
          performedBy,
          "Team Transfer Completed",
          String.format(
              "%s has been successfully transferred from team (%s) to team (%s).",
              employeeName, request.getFromTeamCode(), request.getToTeamCode()),
          TEAMS_PATH + request.getToTeamCode());
    }
  }
}
