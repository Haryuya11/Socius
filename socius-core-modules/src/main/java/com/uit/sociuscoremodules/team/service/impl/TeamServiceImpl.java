package com.uit.sociuscoremodules.team.service.impl;

import com.uit.sociuscoremodules.employee.domain.Employee;
import com.uit.sociuscoremodules.employee.persistence.EmployeeMapper;
import com.uit.sociuscoremodules.shared.enums.DeleteFlagEnums;
import com.uit.sociuscoremodules.shared.request.PaginationSearchRequest;
import com.uit.sociuscoremodules.shared.service.impl.BaseServiceImpl;
import com.uit.sociuscoremodules.team.constants.TeamConstant;
import com.uit.sociuscoremodules.team.converter.TeamConverter;
import com.uit.sociuscoremodules.team.domain.Team;
import com.uit.sociuscoremodules.team.dto.TeamDto;
import com.uit.sociuscoremodules.team.dto.TeamMemberDto;
import com.uit.sociuscoremodules.team.repository.TeamRepository;
import com.uit.sociuscoremodules.team.request.TeamCreateRequest;
import com.uit.sociuscoremodules.team.request.TeamFilterRequest;
import com.uit.sociuscoremodules.team.request.TeamSearchRequest;
import com.uit.sociuscoremodules.team.request.TeamUpdateRequest;
import com.uit.sociuscoremodules.team.service.TeamService;
import com.uit.sociuscoremodules.teamemployee.enums.TeamRoleEnums;
import com.uit.sociuscoremodules.teamemployee.repository.TeamEmployeeRepository;
import com.uit.sociuscoremodules.teamemployee.service.TeamEmployeeService;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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

  private static final List<String> VALID_SORT_FIELDS =
      Arrays.asList("id", "created_at", "team_code", "team_name");

  /**
   * Create a new team or reactivate a soft-deleted team.
   *
   * <p>If team code exists and is soft-deleted, reactivates it with new information. Otherwise,
   * creates a new team with the specified team lead.
   *
   * @param request the team creation request containing team code, name, department, and team lead
   * @return the created or reactivated TeamDto with team lead and members
   */
  @Override
  public TeamDto createTeam(TeamCreateRequest request) {
    log.info("Creating team with code: {}", request.getTeamCode());

    // Check if team exists (including soft deleted)
    Team existingTeam = teamRepository.findByTeamCodeIncludeDeleted(request.getTeamCode());

    if (existingTeam != null) {
      // If team exists and is soft deleted, reactivate it
      if (existingTeam.getDeleteFlag() == DeleteFlagEnums.DELETED.getValue().shortValue()) {
        log.info("Reactivating soft deleted team: {}", request.getTeamCode());

        existingTeam.setTeamName(request.getTeamName());
        existingTeam.setDepartmentCode(request.getDepartmentCode());
        existingTeam.setUpdatedAt(LocalDateTime.now());
        existingTeam.setDeletedAt(null);
        existingTeam.setDeleteFlag(DeleteFlagEnums.NOT_DELETED.getValue().shortValue());

        teamRepository.reactivateTeam(existingTeam);
        log.info("Team {} reactivated successfully", existingTeam.getTeamCode());

        // Add team lead
        Employee teamLead = employeeMapper.findByClientId(request.getTeamLeadClientId());
        if (teamLead == null) {
          log.error("Employee with clientId {} not found", request.getTeamLeadClientId());
          throw notFound(TeamConstant.EMPLOYEE_NOT_FOUND);
        }

        // Check if team lead already exists in team_employees
        if (!Boolean.TRUE.equals(
            teamEmployeeRepository.existsByTeamCodeAndEmployeeId(
                existingTeam.getTeamCode(), teamLead.getClientId()))) {
          teamEmployeeService.addEmployeeToTeam(
              existingTeam.getTeamCode(),
              teamLead.getClientId(),
              TeamRoleEnums.TEAM_LEAD.getRoleCode(),
              true);
          log.info(
              "Team lead {} added to reactivated team {}",
              teamLead.getClientId(),
              existingTeam.getTeamCode());
        }

        return getTeamByTeamCode(existingTeam.getTeamCode());
      } else {
        // Team exists and is not deleted
        log.error("Team with code {} already exists", request.getTeamCode());
        throw badRequest(TeamConstant.TEAM_ALREADY_EXISTS);
      }
    }

    // Create new team
    Employee teamLead = employeeMapper.findByClientId(request.getTeamLeadClientId());
    if (teamLead == null) {
      log.error("Employee with clientId {} not found", request.getTeamLeadClientId());
      throw notFound(TeamConstant.EMPLOYEE_NOT_FOUND);
    }

    Team team = new Team();
    team.setTeamCode(request.getTeamCode());
    team.setTeamName(request.getTeamName());
    team.setDepartmentCode(request.getDepartmentCode());
    team.setCreatedAt(LocalDateTime.now());
    team.setUpdatedAt(LocalDateTime.now());
    team.setDeleteFlag(DeleteFlagEnums.NOT_DELETED.getValue().shortValue());

    teamRepository.insert(team);
    log.info("Team {} created successfully", team.getTeamCode());

    teamEmployeeService.addEmployeeToTeam(
        team.getTeamCode(), teamLead.getClientId(), TeamRoleEnums.TEAM_LEAD.getRoleCode(), true);

    log.info("Team lead {} added to team {}", teamLead.getClientId(), team.getTeamCode());

    return getTeamByTeamCode(team.getTeamCode());
  }

  /**
   * Retrieve team details by team ID.
   *
   * @param id the team ID
   * @return TeamDto with team lead and all members
   * @throws NotFoundException if team not found
   */
  @Override
  public TeamDto getTeamById(Integer id) {
    log.info("Getting team by ID: {}", id);
    Team team = teamRepository.findById(id);
    if (team == null) {
      throw notFound(TeamConstant.TEAM_NOT_FOUND);
    }

    return enrichTeamDto(team);
  }

  /**
   * Retrieve team details by team code.
   *
   * @param teamCode the unique team code
   * @return TeamDto with team lead and all members
   * @throws NotFoundException if team not found
   */
  @Override
  public TeamDto getTeamByTeamCode(String teamCode) {
    log.info("Getting team by code: {}", teamCode);
    Team team = teamRepository.findByTeamCode(teamCode);
    if (team == null) {
      throw notFound(TeamConstant.TEAM_NOT_FOUND);
    }

    return enrichTeamDto(team);
  }

  /**
   * Get all teams with optional filters, sorting, and pagination.
   *
   * @param filter the filter criteria including search, sorting, and pagination parameters
   * @return map containing "teams" list and "total" count
   * @throws BadRequestException if invalid sort field provided
   */
  @Override
  public Map<String, Object> getAllTeams(TeamFilterRequest filter) {
    log.info("Getting all teams with filter: {}", filter);

    if (filter.getSortBy() != null && !VALID_SORT_FIELDS.contains(filter.getSortBy())) {
      throw badRequest(TeamConstant.INVALID_SORT_FIELD);
    }

    return teamRepository.findAllWithFilters(filter);
  }

  /**
   * Search teams with advanced filtering, pagination, and sorting.
   *
   * <p>Supports searching by team code, team name, and department code with pagination and
   * multi-field sorting.
   *
   * @param request the search request with condition, page request, and sort requests
   * @return map containing "teams" list and "total" count
   * @throws BadRequestException if invalid sort field provided
   */
  @Override
  public Map<String, Object> searchTeams(PaginationSearchRequest<TeamSearchRequest> request) {
    log.info("Searching teams with request: {}", request);

    TeamSearchRequest condition = request.getCondition();
    TeamFilterRequest filter = new TeamFilterRequest();

    if (condition != null) {
      filter.setTeamCode(condition.getTeamCode());
      filter.setTeamName(condition.getTeamName());
      filter.setDepartmentCode(condition.getDepartmentCode());
    }

    if (request.getPageRequest() != null) {
      filter.setPage(request.getPageRequest().getPageNumber());
      filter.setSize(request.getPageRequest().getPageSize());
    }

    if (request.getSortRequests() != null && !request.getSortRequests().isEmpty()) {
      String sortBy = request.getSortRequests().get(0).getSortBy();
      if (!VALID_SORT_FIELDS.contains(sortBy)) {
        throw badRequest(TeamConstant.INVALID_SORT_FIELD);
      }
      filter.setSortBy(sortBy);
      filter.setSortOrder(request.getSortRequests().get(0).getSortDirection());
    }

    return teamRepository.findAllWithFilters(filter);
  }

  /**
   * Update team information (partial update).
   *
   * <p>Only updates team name if provided in the request.
   *
   * @param teamCode the team code to update
   * @param request the update request containing optional team name
   * @return the updated TeamDto
   * @throws NotFoundException if team not found
   */
  @Override
  public TeamDto updateTeam(String teamCode, TeamUpdateRequest request) {
    log.info("Updating team: {}", teamCode);

    Team team = teamRepository.findByTeamCode(teamCode);
    if (team == null) {
      throw notFound(TeamConstant.TEAM_NOT_FOUND);
    }

    if (request.getTeamName() != null && !request.getTeamName().isEmpty()) {
      team.setTeamName(request.getTeamName());
    }
    team.setUpdatedAt(LocalDateTime.now());

    teamRepository.update(team);
    log.info("Team {} updated successfully", teamCode);

    return getTeamByTeamCode(teamCode);
  }

  /**
   * Soft delete a team (only if empty).
   *
   * <p>Team can only be deleted if it has no employees. Remove all employees first before deleting
   * the team.
   *
   * @param teamCode the team code to delete
   * @throws NotFoundException if team not found
   * @throws BadRequestException if team has any employees
   */
  @Override
  public void deleteTeam(String teamCode) {
    log.info("Deleting team: {}", teamCode);

    Team team = teamRepository.findByTeamCode(teamCode);
    if (team == null) {
      throw notFound(TeamConstant.TEAM_NOT_FOUND);
    }

    // Check if team has any employees
    List<Employee> employees = teamEmployeeService.getEmployeesByTeamCode(teamCode);
    if (!employees.isEmpty()) {
      log.error("Cannot delete team {} with {} employee(s)", teamCode, employees.size());
      throw badRequest(TeamConstant.CANNOT_DELETE_TEAM_WITH_EMPLOYEES);
    }

    teamRepository.softDelete(team.getId());
    log.info("Team {} deleted successfully", teamCode);
  }

  /**
   * Enrich team DTO with team lead and members information.
   *
   * @param team the team entity
   * @return enriched TeamDto with team lead and all members
   */
  private TeamDto enrichTeamDto(Team team) {
    TeamDto dto = teamConverter.entityToDto(team);

    Employee teamLead = teamEmployeeService.getTeamLeadByTeamCode(team.getTeamCode());
    if (teamLead != null) {
      TeamMemberDto teamLeadDto = new TeamMemberDto();
      teamLeadDto.setClientId(teamLead.getClientId());
      teamLeadDto.setUserId(teamLead.getUserId());
      teamLeadDto.setFirstName(teamLead.getFirstName());
      teamLeadDto.setLastName(teamLead.getLastName());
      teamLeadDto.setImageUrl(teamLead.getImageUrl());
      dto.setTeamLead(teamLeadDto);
    }

    List<Employee> employees = teamEmployeeService.getEmployeesByTeamCode(team.getTeamCode());
    List<TeamMemberDto> members =
        employees.stream()
            .map(
                emp -> {
                  TeamMemberDto memberDto = new TeamMemberDto();
                  memberDto.setClientId(emp.getClientId());
                  memberDto.setUserId(emp.getUserId());
                  memberDto.setFirstName(emp.getFirstName());
                  memberDto.setLastName(emp.getLastName());
                  memberDto.setImageUrl(emp.getImageUrl());
                  return memberDto;
                })
            .toList();
    dto.setMembers(members);

    return dto;
  }
}
