package com.uit.sociuscoremodules.team.service.impl;

import com.uit.sociuscoremodules.employee.domain.Employee;
import com.uit.sociuscoremodules.employee.persistence.EmployeeMapper;
import com.uit.sociuscoremodules.shared.constants.CommonConstant;
import com.uit.sociuscoremodules.shared.constants.MessageConstant;
import com.uit.sociuscoremodules.shared.enums.DeleteFlagEnums;
import com.uit.sociuscoremodules.shared.request.PaginationSearchRequest;
import com.uit.sociuscoremodules.shared.response.PageResponse;
import com.uit.sociuscoremodules.shared.service.impl.BaseServiceImpl;
import com.uit.sociuscoremodules.team.converter.TeamConverter;
import com.uit.sociuscoremodules.team.domain.Team;
import com.uit.sociuscoremodules.team.dto.SearchTeamDto;
import com.uit.sociuscoremodules.team.dto.TeamDto;
import com.uit.sociuscoremodules.team.dto.TeamMemberDto;
import com.uit.sociuscoremodules.team.repository.TeamRepository;
import com.uit.sociuscoremodules.team.request.TeamCreateRequest;
import com.uit.sociuscoremodules.team.request.TeamSearchRequest;
import com.uit.sociuscoremodules.team.request.TeamUpdateRequest;
import com.uit.sociuscoremodules.team.service.TeamService;
import com.uit.sociuscoremodules.teamemployee.enums.TeamRoleEnums;
import com.uit.sociuscoremodules.teamemployee.repository.TeamEmployeeRepository;
import com.uit.sociuscoremodules.teamemployee.service.TeamEmployeeService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/** Implementation of TeamService for team management operations. */
@Slf4j
@Service
@RequiredArgsConstructor
public class TeamServiceImpl extends BaseServiceImpl implements TeamService {

  private final TeamRepository teamRepository;
  private final TeamConverter teamConverter;
  private final EmployeeMapper employeeMapper;
  private final TeamEmployeeService teamEmployeeService;
  private final TeamEmployeeRepository teamEmployeeRepository;

  // ========================= TEAM SERVICE MAIN METHODS =========================

  /**
   * Create a new team or reactivate a soft-deleted team.
   *
   * @param request the team creation request
   * @return the created or reactivated TeamDto
   */
  @Override
  public TeamDto createTeam(TeamCreateRequest request) {
    Team existingTeam = teamRepository.findByTeamCodeIncludeDeleted(request.getTeamCode());

    if (existingTeam != null) {
      if (isDeleted(existingTeam)) {
        return reactivateTeam(existingTeam, request);
      } else {
        throw badRequest(MessageConstant.E_TEAM_002);
      }
    }

    return createNewTeam(request);
  }

  /**
   * Retrieve team details by team ID.
   *
   * @param id the team ID
   * @return TeamDto with details
   */
  @Override
  public TeamDto getTeamById(Integer id) {
    Team team = teamRepository.findById(id);
    if (team == null) {
      throw notFound(MessageConstant.E_TEAM_001);
    }
    return enrichTeamDto(team);
  }

  /**
   * Retrieve team details by team code.
   *
   * @param teamCode the team code
   * @return TeamDto with details
   */
  @Override
  public TeamDto getTeamByTeamCode(String teamCode) {
    Team team = teamRepository.findByTeamCode(teamCode);
    if (team == null) {
      throw notFound(MessageConstant.E_TEAM_001);
    }
    return enrichTeamDto(team);
  }

  /**
   * Search teams with filtering and pagination.
   *
   * @param request the search request
   * @return PageResponse of SearchTeamDto
   */
  @Override
  public PageResponse<SearchTeamDto> searchTeams(
      PaginationSearchRequest<TeamSearchRequest> request) {

    TeamSearchRequest criteria = request.getCondition();
    if (criteria == null) {
      criteria = new TeamSearchRequest();
    }

    int total = teamRepository.count(criteria);
    if (total == CommonConstant.INIT_INDEX) {
      return PageResponse.empty();
    }

    int limit = request.getPageRequest().getPageSize();
    int offset = (request.getPageRequest().getPageNumber() - 1) * limit;

    List<SearchTeamDto> result =
        teamRepository.search(criteria, request.getSortRequests(), limit, offset);

    return PageResponse.of(result, total, offset, limit);
  }

  /**
   * Update team information.
   *
   * @param teamCode the team code
   * @param request the update request
   * @return the updated TeamDto
   */
  @Override
  public TeamDto updateTeam(String teamCode, TeamUpdateRequest request) {
    Team team = teamRepository.findByTeamCode(teamCode);
    if (team == null) {
      throw notFound(MessageConstant.E_TEAM_001);
    }

    if (request.getTeamName() != null && !request.getTeamName().isEmpty()) {
      team.setTeamName(request.getTeamName());
    }
    team.setUpdatedAt(LocalDateTime.now());

    teamRepository.update(team);
    return getTeamByTeamCode(teamCode);
  }

  /**
   * Soft delete a team.
   *
   * @param teamCode the team code
   */
  @Override
  public void deleteTeam(String teamCode) {
    Team team = teamRepository.findByTeamCode(teamCode);
    if (team == null) {
      throw notFound(MessageConstant.E_TEAM_001);
    }

    // Check if team has any employees before deleting
    List<Employee> employees = teamEmployeeService.getEmployeesByTeamCode(teamCode);
    if (!employees.isEmpty()) {
      throw badRequest(MessageConstant.E_TEAM_007);
    }

    teamRepository.softDelete(team.getId());
  }

  // ========================= HELPER METHODS =========================

  /** Check if a team is soft-deleted. */
  private boolean isDeleted(Team team) {
    return team.getDeleteFlag() == DeleteFlagEnums.DELETED.getValue().shortValue();
  }

  /** Reactivate a soft-deleted team. */
  private TeamDto reactivateTeam(Team team, TeamCreateRequest request) {
    team.setTeamName(request.getTeamName());
    team.setDepartmentCode(request.getDepartmentCode());
    team.setUpdatedAt(LocalDateTime.now());
    team.setDeletedAt(null);
    team.setDeleteFlag(DeleteFlagEnums.NOT_DELETED.getValue().shortValue());

    teamRepository.reactivateTeam(team);

    addTeamLeadIfMissing(team.getTeamCode(), request.getTeamLeadClientId());

    return getTeamByTeamCode(team.getTeamCode());
  }

  /** Create a completely new team using Builder pattern. */
  private TeamDto createNewTeam(TeamCreateRequest request) {
    validateAndGetTeamLead(request.getTeamLeadClientId());

    // Using Lombok Builder here matching EmployeeService style
    Team team =
        Team.builder()
            .teamCode(request.getTeamCode())
            .teamName(request.getTeamName())
            .departmentCode(request.getDepartmentCode())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .deleteFlag(DeleteFlagEnums.NOT_DELETED.getValue().shortValue())
            .build();

    teamRepository.insert(team);

    // Add Team Lead immediately
    teamEmployeeService.addEmployeeToTeam(
        team.getTeamCode(),
        request.getTeamLeadClientId(),
        TeamRoleEnums.TEAM_LEAD.getRoleCode(),
        true);

    return getTeamByTeamCode(team.getTeamCode());
  }

  /** Add team lead if they are not already in the team. */
  private void addTeamLeadIfMissing(String teamCode, String leadClientId) {
    Employee teamLead = validateAndGetTeamLead(leadClientId);

    if (!Boolean.TRUE.equals(
        teamEmployeeRepository.existsByTeamCodeAndEmployeeId(teamCode, teamLead.getClientId()))) {
      teamEmployeeService.addEmployeeToTeam(
          teamCode, teamLead.getClientId(), TeamRoleEnums.TEAM_LEAD.getRoleCode(), true);
    }
  }

  /** Validate if team lead exists. */
  private Employee validateAndGetTeamLead(String clientId) {
    Employee teamLead = employeeMapper.findByClientId(clientId);
    if (teamLead == null) {
      throw notFound(MessageConstant.W_EMP_002);
    }
    return teamLead;
  }

  /** Enrich team DTO with team lead and members information. */
  private TeamDto enrichTeamDto(Team team) {
    TeamDto dto = teamConverter.entityToDto(team);

    Employee teamLead = teamEmployeeService.getTeamLeadByTeamCode(team.getTeamCode());
    if (teamLead != null) {
      dto.setTeamLead(buildTeamMemberDto(teamLead));
    }

    List<Employee> employees = teamEmployeeService.getEmployeesByTeamCode(team.getTeamCode());
    if (employees != null && !employees.isEmpty()) {
      dto.setMembers(employees.stream().map(this::buildTeamMemberDto).toList());
    }

    return dto;
  }

  /** Build TeamMemberDto from Employee entity using Builder. */
  private TeamMemberDto buildTeamMemberDto(Employee employee) {
    return TeamMemberDto.builder()
        .clientId(employee.getClientId())
        .userId(employee.getUserId())
        .firstName(employee.getFirstName())
        .lastName(employee.getLastName())
        .imageUrl(employee.getImageUrl())
        .build();
  }
}
