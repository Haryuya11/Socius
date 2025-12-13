package com.uit.sociuscoremodules.team.persistence;

import com.uit.sociuscoremodules.shared.request.SortRequest;
import com.uit.sociuscoremodules.team.domain.Team;
import com.uit.sociuscoremodules.team.request.TeamSearchRequest;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/** MyBatis Mapper interface for Team entity. */
@Mapper
public interface TeamMapper {

  /**
   * Find team by team code.
   *
   * @param teamCode the team code
   * @return the Team entity
   */
  Team findByTeamCode(@Param("teamCode") String teamCode);

  /**
   * Search teams based on criteria, sorting, pagination.
   *
   * @param criteria the search criteria
   * @param sorts the sorting options
   * @param limit the maximum number of records to return
   * @param offset the starting point for records to return
   * @return list of Team entities matching the search criteria
   */
  List<Team> search(
      @Param("criteria") TeamSearchRequest criteria,
      @Param("sorts") List<SortRequest> sorts,
      @Param("limit") int limit,
      @Param("offset") int offset);

  /**
   * Count teams based on search criteria.
   *
   * @param criteria the search criteria
   * @return the count of teams matching the criteria
   */
  int count(@Param("criteria") TeamSearchRequest criteria);

  /**
   * Insert a new team.
   *
   * @param team the Team entity to insert
   */
  void insert(@Param("team") Team team);

  /**
   * Update an existing team.
   *
   * @param teamCode the team code to identify the record
   * @param team the Team entity containing update data
   */
  void update(@Param("teamCode") String teamCode, @Param("team") Team team);

  /**
   * Soft delete a team by team code.
   *
   * @param teamCode the team code
   */
  void softDelete(@Param("teamCode") String teamCode);

  /**
   * Reactivate a soft deleted team.
   *
   * @param team the team to reactivate
   */
  void reactivateTeam(@Param("team") Team team);

  /**
   * Check if team code exists.
   *
   * @param teamCode the team code
   * @return true if exists, false otherwise
   */
  Boolean existsByTeamCode(@Param("teamCode") String teamCode);

  /**
   * Find deleted team by team code.
   *
   * @param teamCode the team code
   * @return the deleted Team entity
   */
  Team findDeletedByTeamCode(@Param("teamCode") String teamCode);
}
