package com.uit.sociuscoremodules.team.repository;

import com.uit.sociuscoremodules.employee.domain.Employee;
import com.uit.sociuscoremodules.shared.request.SortRequest;
import com.uit.sociuscoremodules.team.converter.TeamConverter;
import com.uit.sociuscoremodules.team.domain.Team;
import com.uit.sociuscoremodules.team.dto.SearchTeamDto;
import com.uit.sociuscoremodules.team.persistence.TeamMapper;
import com.uit.sociuscoremodules.team.request.TeamSearchRequest;
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

  /**
   * Find team by ID.
   *
   * @param id the team ID
   * @return the Team entity
   */
  public Team findById(Integer id) {
    return teamMapper.findById(id);
  }

  /**
   * Find team by team code.
   *
   * @param teamCode the team code
   * @return the Team entity
   */
  public Team findByTeamCode(String teamCode) {
    return teamMapper.findByTeamCode(teamCode);
  }

  /**
   * Find all teams with filters, sorting, and pagination.
   *
   * @param teamCode the team code
   * @return the Team entity
   */
  public Team findByTeamCodeIncludeDeleted(String teamCode) {
    return teamMapper.findByTeamCodeIncludeDeleted(teamCode);
  }

  /**
   * Insert a new team.
   *
   * @param team the Team entity to insert
   */
  public void insert(Team team) {
    teamMapper.insert(team);
  }

  /**
   * Update an existing team.
   *
   * @param team the Team entity to update
   */
  public void update(Team team) {
    teamMapper.update(team);
  }

  /**
   * Soft delete a team by ID.
   *
   * @param id the team ID
   */
  public void softDelete(Integer id) {
    teamMapper.softDelete(id);
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
  public void reactivateTeam(Team team) {
    teamMapper.reactivateTeam(team);
  }

  /**
   * Find all employees in a team.
   *
   * @param teamCode the team code
   * @return list of Employee entities
   */
  public List<Employee> findEmployeesByTeamCode(String teamCode) {
    return teamEmployeeRepository.findEmployeesByTeamCode(teamCode);
  }

  /**
   * Find team lead of a team.
   *
   * @param teamCode the team code
   * @return the Employee entity
   */
  public Employee findTeamLeadByTeamCode(String teamCode) {
    return teamEmployeeRepository.findTeamLeadByTeamCode(teamCode);
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
}
