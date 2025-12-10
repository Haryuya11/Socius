package com.uit.sociuscoremodules.team.repository;

import com.uit.sociuscoremodules.employee.domain.Employee;
import com.uit.sociuscoremodules.team.domain.Team;
import com.uit.sociuscoremodules.team.persistence.TeamMapper;
import com.uit.sociuscoremodules.team.request.TeamFilterRequest;
import com.uit.sociuscoremodules.teamemployee.repository.TeamEmployeeRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/** Repository for Team entity. */
@Repository
@RequiredArgsConstructor
public class TeamRepository {
  private final TeamMapper teamMapper;
  private final TeamEmployeeRepository teamEmployeeRepository;

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
   * @param filter the filter criteria
   * @return map containing list of teams and total count
   */
  public Map<String, Object> findAllWithFilters(TeamFilterRequest filter) {
    List<Team> teams = teamMapper.findAllWithFilters(filter);
    Integer total = teamMapper.countWithFilters(filter);

    Map<String, Object> result = new HashMap<>();
    result.put("teams", teams);
    result.put("total", total);
    result.put("page", filter.getPage());
    result.put("size", filter.getSize());
    result.put("totalPages", (int) Math.ceil((double) total / filter.getSize()));

    return result;
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
   * Find team by team code including soft deleted teams.
   *
   * @param teamCode the team code
   * @return the Team entity (including soft deleted)
   */
  public Team findByTeamCodeIncludeDeleted(String teamCode) {
    return teamMapper.findByTeamCodeIncludeDeleted(teamCode);
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
}
