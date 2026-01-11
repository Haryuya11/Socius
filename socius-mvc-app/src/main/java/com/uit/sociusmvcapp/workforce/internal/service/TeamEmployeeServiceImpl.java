package com.uit.sociusmvcapp.workforce.internal.service;

import com.uit.sociusmvcapp.employee.EmployeeService;
import com.uit.sociusmvcapp.iam.enums.TeamRoleEnums;
import com.uit.sociusmvcapp.shared.constants.MessageConstant;
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
import org.springframework.stereotype.Service;

/** Implementation of TeamEmployeeService for team-employee relationship operations. */
@Slf4j
@Service
@RequiredArgsConstructor
public class TeamEmployeeServiceImpl implements TeamEmployeeService {

  private final TeamEmployeeRepository teamEmployeeRepository;
  private final TeamService teamService;
  private final EmployeeService employeeService;
  private final TeamEmployeeConverter teamEmployeeConverter;
  private final I18nService i18nService;

  @Override
  public List<TeamEmployeeDto> getEmployeesByTeamCode(String teamCode) {
    teamService.validateExists(teamCode);
    return teamEmployeeRepository.findEmployeesByTeamCode(teamCode);
  }

  @Override
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

    // Step 7: Fetch and return results
    List<TeamEmployeeDto> results = fetchResults(teamCode, requestsToProcess);
    successful.addAll(results);

    return buildResult(successful, failed);
  }

  @Override
  public TeamEmployeeRemovalResultDto removeEmployeesFromTeam(
      String teamCode, RemoveEmployeesFromTeamRequest request) {
    teamService.validateExists(teamCode);

    List<String> removedEmployeeIds = new ArrayList<>();
    List<BatchErrorDto> failed = new ArrayList<>();

    // Batch validate: get all employees in team with 1 query
    List<String> existingEmployeeIds =
        teamEmployeeRepository.findEmployeesByTeamCode(teamCode).stream()
            .map(te -> te.getEmployee().getClientId())
            .toList();

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
    }

    return TeamEmployeeRemovalResultDto.builder()
        .removedEmployeeIds(removedEmployeeIds)
        .failed(failed)
        .build();
  }

  @Override
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

    return teamEmployeeRepository.findByTeamCodeAndEmployeeId(teamCode, newLeadEmployeeId);
  }

  /**
   * Transfer an employee from one team to another.
   *
   * @param request the request containing transfer details
   */
  @Override
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

    // Use batch remove API
    RemoveEmployeesFromTeamRequest removeRequest =
        new RemoveEmployeesFromTeamRequest(
            List.of(new RemoveEmployeeFromTeamRequest(request.getEmployeeId())));
    removeEmployeesFromTeam(request.getFromTeamCode(), removeRequest);

    // Use batch add API
    AddEmployeesToTeamRequest batchAddRequest = new AddEmployeesToTeamRequest(List.of(addRequest));
    addEmployeesToTeam(request.getToTeamCode(), batchAddRequest);
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
}
