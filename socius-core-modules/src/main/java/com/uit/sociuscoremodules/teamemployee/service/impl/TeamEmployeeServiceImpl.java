package com.uit.sociuscoremodules.teamemployee.service.impl;

import com.uit.sociuscoremodules.employee.dto.EmployeeDto;
import com.uit.sociuscoremodules.employee.repository.EmployeeRepository;
import com.uit.sociuscoremodules.shared.constants.CommonConstant;
import com.uit.sociuscoremodules.shared.constants.MessageConstant;
import com.uit.sociuscoremodules.shared.request.PaginationSearchRequest;
import com.uit.sociuscoremodules.shared.response.PageResponse;
import com.uit.sociuscoremodules.shared.service.I18nService;
import com.uit.sociuscoremodules.shared.service.impl.BaseServiceImpl;
import com.uit.sociuscoremodules.team.constants.TeamConstant;
import com.uit.sociuscoremodules.team.dto.TeamDto;
import com.uit.sociuscoremodules.team.repository.TeamRepository;
import com.uit.sociuscoremodules.teamemployee.converter.TeamEmployeeConverter;
import com.uit.sociuscoremodules.teamemployee.dto.BatchErrorDto;
import com.uit.sociuscoremodules.teamemployee.dto.SearchTeamEmployeeDto;
import com.uit.sociuscoremodules.teamemployee.dto.TeamEmployeeBatchResultDto;
import com.uit.sociuscoremodules.teamemployee.dto.TeamEmployeeDto;
import com.uit.sociuscoremodules.teamemployee.enums.TeamRoleEnums;
import com.uit.sociuscoremodules.teamemployee.repository.TeamEmployeeRepository;
import com.uit.sociuscoremodules.teamemployee.request.SearchTeamEmployeeRequest;
import com.uit.sociuscoremodules.teamemployee.request.TeamEmployeeAddRequest;
import com.uit.sociuscoremodules.teamemployee.request.TeamEmployeeBatchAddRequest;
import com.uit.sociuscoremodules.teamemployee.request.TransferTeamEmployeeRequest;
import com.uit.sociuscoremodules.teamemployee.service.TeamEmployeeService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/** Implementation of TeamEmployeeService for team-employee relationship operations. */
@Slf4j
@Service
@RequiredArgsConstructor
public class TeamEmployeeServiceImpl extends BaseServiceImpl implements TeamEmployeeService {

  private final TeamEmployeeRepository teamEmployeeRepository;
  private final TeamRepository teamRepository;
  private final EmployeeRepository employeeRepository;
  private final TeamEmployeeConverter teamEmployeeConverter;
  private final I18nService i18nService;

  // ========================= TEAM EMPLOYEE SERVICE MAIN METHODS =========================

  @Override
  public TeamEmployeeDto addEmployeeToTeam(String teamCode, TeamEmployeeAddRequest request) {
    validateTeamExists(teamCode);
    validateEmployeeExists(request.getEmployeeId());
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
    validateTeamExists(teamCode);

    List<TeamEmployeeDto> successful = new ArrayList<>();
    List<BatchErrorDto> failed = new ArrayList<>();

    for (TeamEmployeeAddRequest employeeRequest : request.getEmployees()) {
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
    validateTeamExists(teamCode);
    validateEmployeeExistsInTeam(teamCode, employeeId);

    teamEmployeeRepository.removeEmployeeFromTeam(teamCode, employeeId);
  }

  @Override
  public TeamEmployeeDto changeTeamLead(String teamCode, String newLeadEmployeeId) {
    validateTeamExists(teamCode);
    validateEmployeeExists(newLeadEmployeeId);
    validateEmployeeExistsInTeam(teamCode, newLeadEmployeeId);

    TeamEmployeeDto currentLead = teamEmployeeRepository.findTeamLeadByTeamCode(teamCode);
    if (currentLead != null && !currentLead.getEmployeeId().equals(newLeadEmployeeId)) {
      teamEmployeeRepository.updateLeadershipStatus(teamCode, currentLead.getEmployeeId(), false);
      teamEmployeeRepository.updateRoleCode(
          teamCode, currentLead.getEmployeeId(), TeamRoleEnums.EMPLOYEE.getRoleCode());
    }

    teamEmployeeRepository.updateLeadershipStatus(teamCode, newLeadEmployeeId, true);
    teamEmployeeRepository.updateRoleCode(
        teamCode, newLeadEmployeeId, TeamRoleEnums.TEAM_LEAD.getRoleCode());

    return teamEmployeeRepository.findByTeamCodeAndEmployeeId(teamCode, newLeadEmployeeId);
  }

  @Override
  public List<EmployeeDto> getEmployeesByTeamCode(String teamCode) {
    validateTeamExists(teamCode);

    List<TeamEmployeeDto> teamEmployees = teamEmployeeRepository.findEmployeesByTeamCode(teamCode);

    return teamEmployeeConverter.toEmployeeDtos(teamEmployees);
  }

  @Override
  public EmployeeDto getTeamLeadByTeamCode(String teamCode) {
    validateTeamExists(teamCode);

    TeamEmployeeDto teamLead = teamEmployeeRepository.findTeamLeadByTeamCode(teamCode);
    return teamLead != null ? teamEmployeeConverter.toEmployeeDto(teamLead) : null;
  }

  @Override
  public PageResponse<SearchTeamEmployeeDto> searchEmployeesInTeam(
      String teamCode, PaginationSearchRequest<SearchTeamEmployeeRequest> request) {
    validateTeamExists(teamCode);

    SearchTeamEmployeeRequest criteria = request.getCondition();
    if (criteria == null) {
      criteria = new SearchTeamEmployeeRequest();
    }
    criteria.setTeamCode(teamCode);

    int total = teamEmployeeRepository.count(criteria);
    if (total == CommonConstant.INIT_INDEX) {
      return PageResponse.empty();
    }

    int limit = request.getPageRequest().getPageSize();
    int offset = (request.getPageRequest().getPageNumber() - 1) * limit;

    List<SearchTeamEmployeeDto> result =
        teamEmployeeRepository.search(criteria, request.getSortRequests(), limit, offset);

    return PageResponse.of(result, total, offset, limit);
  }

  // ========================= VALIDATION METHODS =========================

  private void validateTeamExists(String teamCode) {
    TeamDto team = teamRepository.findByTeamCode(teamCode);
    if (team == null) {
      throw notFound(MessageConstant.E_TEAM_001);
    }
  }

  private void validateEmployeeExists(String employeeId) {
    EmployeeDto employee = employeeRepository.findByClientId(employeeId);
    if (employee == null) {
      throw notFound(MessageConstant.W_EMP_002);
    }
  }

  private void validateEmployeeNotActiveInTeam(String teamCode, String employeeId) {
    if (Boolean.TRUE.equals(
        teamEmployeeRepository.existsByTeamCodeAndEmployeeId(teamCode, employeeId))) {
      throw badRequest(MessageConstant.E_TEAM_EMP_001);
    }
  }

  private void validateEmployeeExistsInTeam(String teamCode, String employeeId) {
    TeamEmployeeDto teamEmployee =
        teamEmployeeRepository.findByTeamCodeAndEmployeeId(teamCode, employeeId);
    if (teamEmployee == null) {
      throw badRequest(MessageConstant.E_TEAM_EMP_002);
    }
  }

  private void validateTeamLeadConstraint(String teamCode, Boolean isLeader) {
    if (Boolean.TRUE.equals(isLeader)
        && Boolean.TRUE.equals(teamEmployeeRepository.existsTeamLeadByTeamCode(teamCode))) {
      throw badRequest(MessageConstant.E_TEAM_006);
    }
  }

  // ========================= HELPER METHODS =========================

  private TeamEmployeeDto createNewTeamEmployee(String teamCode, TeamEmployeeAddRequest request) {
    validateTeamLeadConstraint(teamCode, request.getIsLeader());

    teamEmployeeRepository.addEmployeeToTeam(request, teamCode, request.getEmployeeId());
    return teamEmployeeRepository.findByTeamCodeAndEmployeeId(teamCode, request.getEmployeeId());
  }

  private TeamEmployeeDto reactivateEmployee(String teamCode, TeamEmployeeAddRequest request) {
    validateTeamLeadConstraint(teamCode, request.getIsLeader());

    teamEmployeeRepository.reactivate(teamCode, request);
    return teamEmployeeRepository.findByTeamCodeAndEmployeeId(teamCode, request.getEmployeeId());
  }

  private String determineRoleCode(Boolean isLeader) {
    return Boolean.TRUE.equals(isLeader)
        ? TeamRoleEnums.TEAM_LEAD.getRoleCode()
        : TeamRoleEnums.EMPLOYEE.getRoleCode();
  }

  private BatchErrorDto buildBatchError(String employeeId, String errorMessage) {
    return BatchErrorDto.builder()
        .clientId(employeeId)
        .errorCode(MessageConstant.S_TEAM_EMP_001)
        .errorMessage(errorMessage)
        .build();
  }

  /**
   * Transfer an employee from one team to another.
   *
   * @param request the request containing transfer details
   * @return Map containing transfer details
   */
  @Override
  public Map<String, String> transferEmployee(TransferTeamEmployeeRequest request) {
    TeamDto fromTeam = teamRepository.findByTeamCode(request.getFromTeamCode());
    if (fromTeam == null) {
      throw notFound(MessageConstant.E_TEAM_002);
    }

    TeamDto toTeam = teamRepository.findByTeamCode(request.getToTeamCode());
    if (toTeam == null) {
      throw notFound(MessageConstant.E_TEAM_002);
    }

    TeamEmployeeDto existingEmployeeInFromTeam =
        teamEmployeeRepository.findByTeamCodeAndEmployeeId(
            request.getFromTeamCode(), request.getEmployeeId());
    if (existingEmployeeInFromTeam == null) {
      throw notFound(MessageConstant.E_TEAM_EMP_002);
    }

    TeamEmployeeDto existingEmployeeInToTeam =
        teamEmployeeRepository.findByTeamCodeAndEmployeeId(
            request.getToTeamCode(), request.getEmployeeId());
    if (existingEmployeeInToTeam != null) {
      throw badRequest(MessageConstant.E_TEAM_EMP_001);
    }

    TeamEmployeeAddRequest addRequest =
        teamEmployeeConverter.toTeamEmployeeAddRequest(
            request.getRoleCode(), request.getIsLeader());
    addRequest.setEmployeeId(request.getEmployeeId()); // Set employeeId for reactivation

    validateTeamLeadConstraint(request.getToTeamCode(), request.getIsLeader());

    teamEmployeeRepository.removeEmployeeFromTeam(
        request.getFromTeamCode(), request.getEmployeeId());

    TeamEmployeeDto softDeletedInToTeam =
        teamEmployeeRepository.findSoftDeletedByTeamCodeAndEmployeeId(
            request.getToTeamCode(), request.getEmployeeId());

    if (softDeletedInToTeam != null) {
      teamEmployeeRepository.reactivate(request.getToTeamCode(), addRequest);
    } else {
      teamEmployeeRepository.addEmployeeToTeam(
          addRequest, request.getToTeamCode(), request.getEmployeeId());
    }

    return Map.of(
        TeamConstant.EMPLOYEE_ID, request.getEmployeeId(),
        TeamConstant.FROM_TEAM_CODE, request.getFromTeamCode(),
        TeamConstant.TO_TEAM_CODE, request.getToTeamCode());
  }
}
