package com.uit.sociusmvcapp.team.internal.persistence;

import com.uit.sociusmvcapp.shared.request.SortRequest;
import com.uit.sociusmvcapp.team.dto.request.SearchTeamRequest;
import com.uit.sociusmvcapp.team.internal.domain.Team;
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
  Team findByTeamCode(String teamCode);

  /**
   * Insert a new team.
   *
   * @param team the Team entity to insert
   */
  void insert(Team team);

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
  void softDelete(String teamCode);

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
  boolean existsByTeamCode(String teamCode);

  /**
   * Find deleted team by team code.
   *
   * @param teamCode the team code
   * @return the deleted Team entity
   */
  Team findDeletedByTeamCode(String teamCode);

  /**
   * Search for teams based on criteria, sorting, pagination.
   *
   * @param criteria the search criteria
   * @param sorts the sorting options
   * @param limit the maximum number of records to return
   * @param offset the starting point for records to return
   * @return list of Team entities matching the search criteria
   */
  List<Team> search(
      @Param("criteria") SearchTeamRequest criteria,
      @Param("sorts") List<SortRequest> sorts,
      @Param("limit") int limit,
      @Param("offset") int offset);

  /**
   * Count teams based on search criteria.
   *
   * @param criteria the search criteria
   * @return the total count of teams matching the criteria
   */
  int count(@Param("criteria") SearchTeamRequest criteria);
}
