package com.uit.sociuscoremodules.team.repository;

import com.uit.sociuscoremodules.employee.dto.EmployeeDto;
import com.uit.sociuscoremodules.shared.request.SortRequest;
import com.uit.sociuscoremodules.team.converter.TeamConverter;
import com.uit.sociuscoremodules.team.domain.Team;
import com.uit.sociuscoremodules.team.dto.SearchTeamDto;
import com.uit.sociuscoremodules.team.dto.TeamDto;
import com.uit.sociuscoremodules.team.persistence.TeamMapper;
import com.uit.sociuscoremodules.team.request.TeamCreateRequest;
import com.uit.sociuscoremodules.team.request.TeamSearchRequest;
import com.uit.sociuscoremodules.team.request.TeamUpdateRequest;
import com.uit.sociuscoremodules.teamemployee.converter.TeamEmployeeConverter;
import com.uit.sociuscoremodules.teamemployee.repository.TeamEmployeeRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/** Repository for Team entity. */
@Repository
@RequiredArgsConstructor
public class TeamRepository {
  private final TeamMapper teamMapper;
  private final TeamEmployeeRepository teamEmployeeRepository;
  private final TeamConverter teamConverter;
  private final TeamEmployeeConverter teamEmployeeConverter;

  /**
   * Find team by team code.
   *
   * @param teamCode the team code
   * @return TeamDto
   */
  public TeamDto findByTeamCode(String teamCode) {
    Team team = teamMapper.findByTeamCode(teamCode);
    return team != null ? teamConverter.entityToDto(team) : null;
  }

  /**
   * Find deleted team by team code.
   *
   * @param teamCode the team code
   * @return TeamDto
   */
  public TeamDto findDeletedByTeamCode(String teamCode) {
    Team team = teamMapper.findDeletedByTeamCode(teamCode);
    return team != null ? teamConverter.entityToDto(team) : null;
  }

  /**
   * Insert a new team.
   *
   * @param request the team creation request
   */
  public void insert(TeamCreateRequest request) {
    teamMapper.insert(teamConverter.createRequestToEntity(request));
  }

  /**
   * Update team information.
   *
   * @param teamCode the team code
   * @param request the team update request
   */
  public void update(String teamCode, TeamUpdateRequest request) {
    teamMapper.update(teamCode, teamConverter.updateRequestToEntity(request));
  }

  /**
   * Soft delete a team.
   *
   * @param teamCode the team code
   */
  public void softDelete(String teamCode) {
    teamMapper.softDelete(teamCode);
  }

  /**
   * Check if team exists by team code.
   *
   * @param teamCode the team code
   * @return true if exists, false otherwise
   */
  public Boolean existsByTeamCode(String teamCode) {
    return teamMapper.existsByTeamCode(teamCode);
  }

  /**
   * Reactivate a soft-deleted team.
   *
   * @param request the team creation request
   */
  public void reactivateTeam(TeamCreateRequest request) {
    teamMapper.reactivateTeam(teamConverter.createRequestToEntity(request));
  }

  /**
   * Find all employees in a team.
   *
   * @param teamCode the team code
   * @return list of EmployeeDto
   */
  public List<EmployeeDto> findEmployeesByTeamCode(String teamCode) {
    return teamEmployeeConverter.toEmployeeDtos(
        teamEmployeeRepository.findEmployeesByTeamCode(teamCode));
  }

  /**
   * Count teams based on search criteria.
   *
   * @param criteria the search criteria
   * @return count of teams
   */
  public int count(TeamSearchRequest criteria) {
    return teamMapper.count(criteria);
  }

  /**
   * Search teams with pagination and sorting.
   *
   * @param criteria the search criteria
   * @param sortRequests the sorting options
   * @param limit the page size
   * @param offset the offset
   * @return list of SearchTeamDto
   */
  public List<SearchTeamDto> search(
      TeamSearchRequest criteria, List<SortRequest> sortRequests, int limit, int offset) {
    List<Team> teams = teamMapper.search(criteria, sortRequests, limit, offset);
    return teamConverter.entitiesToSearchDtos(teams);
  }
}
