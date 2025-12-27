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
import com.uit.sociusmvcapp.workforce.dto.request.AssignEmployeeToTeamRequest;
import com.uit.sociusmvcapp.workforce.dto.request.TeamEmployeeBatchAddRequest;
import com.uit.sociusmvcapp.workforce.dto.request.TransferTeamEmployeeRequest;
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

  // ========================= TEAM EMPLOYEE SERVICE MAIN METHODS =========================

  @Override
  public TeamEmployeeDto addEmployeeToTeam(String teamCode, AssignEmployeeToTeamRequest request) {
    teamService.validateExists(teamCode);
    employeeService.validateExists(request.getEmployeeId());
    validateEmployeeNotActiveInTeam(teamCode, request.getEmployeeId());

    if (request.getRoleCode() == null || request.getRoleCode().isEmpty()) {
      request.setRoleCode(determineRoleCode(request.getIsLeader()));
    }

    TeamEmployeeDto softDeleted =
        teamEmployeeRepository.findSoftDeletedByTeamCodeAndEmployeeId(
            teamCode, request.getEmployeeId());

    if (softDeleted != null) {
      return reactivateEmployee(teamCode, request);
    }

    return createNewTeamEmployee(teamCode, request);
  }

  @Override
  public TeamEmployeeBatchResultDto addEmployeesToTeam(
      String teamCode, TeamEmployeeBatchAddRequest request) {
    teamService.validateExists(teamCode);

    List<TeamEmployeeDto> successful = new ArrayList<>();
    List<BatchErrorDto> failed = new ArrayList<>();

    for (AssignEmployeeToTeamRequest employeeRequest : request.getEmployees()) {
      try {
        TeamEmployeeDto result = addEmployeeToTeam(teamCode, employeeRequest);
        successful.add(result);
      } catch (Exception e) {
        String errorMessage = i18nService.getMessage(e.getMessage());
        failed.add(buildBatchError(employeeRequest.getEmployeeId(), errorMessage));
      }
    }

    return TeamEmployeeBatchResultDto.builder().successful(successful).failed(failed).build();
  }

  @Override
  public void removeEmployeeFromTeam(String teamCode, String employeeId) {
    teamService.validateExists(teamCode);
    validateEmployeeExistsInTeam(teamCode, employeeId);

    teamEmployeeRepository.removeEmployeeFromTeam(teamCode, employeeId);
  }

  @Override
  public TeamEmployeeDto changeTeamLead(String teamCode, String newLeadEmployeeId) {
    teamService.validateExists(teamCode);
    employeeService.validateExists(newLeadEmployeeId);
    validateEmployeeExistsInTeam(teamCode, newLeadEmployeeId);

    TeamEmployeeDto currentLead = teamEmployeeRepository.findTeamLeadByTeamCode(teamCode);
    if (currentLead != null && !currentLead.getClientId().equals(newLeadEmployeeId)) {
      teamEmployeeRepository.updateLeadershipStatus(teamCode, currentLead.getClientId(), false);
      teamEmployeeRepository.updateRoleCode(
          teamCode, currentLead.getClientId(), TeamRoleEnums.TEAM_MEM.getRoleCode());
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

    AssignEmployeeToTeamRequest addRequest =
        teamEmployeeConverter.toTeamEmployeeAddRequest(request);

    validateTeamLeadConstraint(request.getToTeamCode(), request.getIsLeader());

    teamEmployeeRepository.removeEmployeeFromTeam(
        request.getFromTeamCode(), request.getEmployeeId());

    teamEmployeeRepository.addEmployeeToTeam(addRequest, request.getToTeamCode());
  }

  // ========================= VALIDATION METHODS =========================
  private void validateEmployeeNotActiveInTeam(String teamCode, String employeeId) {
    if (Boolean.TRUE.equals(
        teamEmployeeRepository.existsByTeamCodeAndEmployeeId(teamCode, employeeId))) {
      throw ExceptionFactory.badRequest(MessageConstant.E_TEAM_EMP_001);
    }
  }

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

  // ========================= HELPER METHODS =========================

  private TeamEmployeeDto createNewTeamEmployee(
      String teamCode, AssignEmployeeToTeamRequest request) {
    validateTeamLeadConstraint(teamCode, request.getIsLeader());

    teamEmployeeRepository.addEmployeeToTeam(request, teamCode);
    return teamEmployeeRepository.findByTeamCodeAndEmployeeId(teamCode, request.getEmployeeId());
  }

  private TeamEmployeeDto reactivateEmployee(String teamCode, AssignEmployeeToTeamRequest request) {
    validateTeamLeadConstraint(teamCode, request.getIsLeader());

    teamEmployeeRepository.reactivate(teamCode, request);
    return teamEmployeeRepository.findByTeamCodeAndEmployeeId(teamCode, request.getEmployeeId());
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
}
