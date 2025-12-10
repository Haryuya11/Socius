package com.uit.sociuscoremodules.team.persistence;

import com.uit.sociuscoremodules.team.domain.Team;
import com.uit.sociuscoremodules.team.request.TeamFilterRequest;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/** MyBatis Mapper interface for Team entity. */
@Mapper
public interface TeamMapper {

  /**
   * Find team by ID.
   *
   * @param id the team ID
   * @return the Team entity
   */
  Team findById(@Param("id") Integer id);

  /**
   * Find team by team code.
   *
   * @param teamCode the team code
   * @return the Team entity
   */
  Team findByTeamCode(@Param("teamCode") String teamCode);

  /**
   * Find team by team code including soft deleted teams.
   *
   * @param teamCode the team code
   * @return the Team entity (including soft deleted)
   */
  Team findByTeamCodeIncludeDeleted(@Param("teamCode") String teamCode);

  /**
   * Find all teams with filters, sorting, and pagination.
   *
   * @param filter the filter criteria
   * @return list of Team entities
   */
  List<Team> findAllWithFilters(@Param("filter") TeamFilterRequest filter);

  /**
   * Count teams with filters.
   *
   * @param filter the filter criteria
   * @return total count of teams
   */
  Integer countWithFilters(@Param("filter") TeamFilterRequest filter);

  /**
   * Insert a new team.
   *
   * @param team the Team entity to insert
   */
  void insert(@Param("team") Team team);

  /**
   * Update an existing team.
   *
   * @param team the Team entity to update
   */
  void update(@Param("team") Team team);

  /**
   * Soft delete a team by ID.
   *
   * @param id the team ID
   */
  void softDelete(@Param("id") Integer id);

  /**
   * Reactivate a soft deleted team.
   *
   * @param team the team to reactivate
   */
  void reactivateTeam(Team team);

  /**
   * Check if team code exists.
   *
   * @param teamCode the team code
   * @return true if exists, false otherwise
   */
  Boolean existsByTeamCode(@Param("teamCode") String teamCode);
}
