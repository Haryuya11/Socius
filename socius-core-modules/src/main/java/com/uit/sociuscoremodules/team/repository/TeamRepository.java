package com.uit.sociuscoremodules.team.repository;

import com.uit.sociuscoremodules.employee.converter.EmployeeConverter;
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
  private final EmployeeConverter employeeConverter;

  /**
   * Find team by team code.
   *
   * @param teamCode the team code
   * @return the Team entity
   */
  public TeamDto findByTeamCode(String teamCode) {
    return teamConverter.entityToDto(teamMapper.findByTeamCode(teamCode));
  }

  /**
   * Insert a new team.
   *
   * @param request the TeamCreateRequest
   */
  public void insert(TeamCreateRequest request) {
    teamMapper.insert(teamConverter.createRequestToEntity(request));
  }

  /**
   * Update an existing team.
   *
   * @param team the Team entity to update
   */
  public void update(TeamUpdateRequest team) {
    teamMapper.update(teamConverter.updateRequestToEntity(team));
  }

  /**
   * Soft delete a team by ID.
   *
   * @param teamCode the team code
   */
  public void softDelete(String teamCode) {
    teamMapper.softDelete(teamCode);
  }

  /**
   * Check if team code exists.
   *
   * @param teamCode the team code
   * @return true if exists, false otherwise
   */
  public Boolean existsByTeamCode(String teamCode) {
    return teamMapper.existsByTeamCode(teamCode);
  }

  /**
   * Reactivate a soft deleted team.
   *
   * @param team the team to reactivate
   */
  public void reactivateTeam(TeamCreateRequest team) {
    teamMapper.reactivateTeam(teamConverter.createRequestToEntity(team));
  }

  /**
   * Find all employees in a team.
   *
   * @param teamCode the team code
   * @return list of Employee entities
   */
  public List<EmployeeDto> findEmployeesByTeamCode(String teamCode) {
    return employeeConverter.entitiesToDtos(
        teamEmployeeRepository.findEmployeesByTeamCode(teamCode));
  }

  /**
   * Count teams based on given criteria.
   *
   * @param criteria the search criteria
   * @return the count of teams matching the criteria
   */
  public int count(TeamSearchRequest criteria) {
    return teamMapper.count(criteria);
  }

  /**
   * Search for teams based on given criteria.
   *
   * @param criteria the search criteria
   * @param sortRequests the sorting requests
   * @param limit the maximum number of records to return
   * @param offset the starting point for records to return
   * @return List of TeamDto
   */
  public List<SearchTeamDto> search(
      TeamSearchRequest criteria, List<SortRequest> sortRequests, int limit, int offset) {
    List<Team> teams = teamMapper.search(criteria, sortRequests, limit, offset);
    return teamConverter.entitiesToSearchDtos(teams);
  }

  /**
   * Find deleted team by team code.
   *
   * @param teamCode the team code
   * @return the TeamDto
   */
  public TeamDto findDeletedByTeamCode(String teamCode) {
    return teamConverter.entityToDto(teamMapper.findDeletedByTeamCode(teamCode));
  }
}
