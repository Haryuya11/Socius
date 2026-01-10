package com.uit.sociusmvcapp.team.internal.repository;

import com.uit.sociusmvcapp.shared.request.SortRequest;
import com.uit.sociusmvcapp.team.dto.SearchTeamDto;
import com.uit.sociusmvcapp.team.dto.TeamDto;
import com.uit.sociusmvcapp.team.dto.request.CreateTeamRequest;
import com.uit.sociusmvcapp.team.dto.request.SearchTeamRequest;
import com.uit.sociusmvcapp.team.dto.request.UpdateTeamRequest;
import com.uit.sociusmvcapp.team.internal.converter.TeamConverter;
import com.uit.sociusmvcapp.team.internal.persistence.TeamMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/** Repository for Team entity. */
@Repository
@RequiredArgsConstructor
public class TeamRepository {
  private final TeamMapper teamMapper;
  private final TeamConverter teamConverter;

  /**
   * Find team by team code.
   *
   * @param teamCode the team code
   * @return TeamDto
   */
  public TeamDto findByTeamCode(String teamCode) {
    return teamConverter.entityToDto(teamMapper.findByTeamCode(teamCode));
  }

  /**
   * Find deleted team by team code.
   *
   * @param teamCode the team code
   * @return TeamDto
   */
  public TeamDto findDeletedByTeamCode(String teamCode) {
    return teamConverter.entityToDto(teamMapper.findDeletedByTeamCode(teamCode));
  }

  /**
   * Insert a new team.
   *
   * @param request the team creation request
   */
  public void insert(CreateTeamRequest request) {
    teamMapper.insert(teamConverter.createRequestToEntity(request));
  }

  /**
   * Update team information.
   *
   * @param teamCode the team code
   * @param request the team update request
   */
  public void update(String teamCode, UpdateTeamRequest request) {
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
  public boolean existsByTeamCode(String teamCode) {
    return teamMapper.existsByTeamCode(teamCode);
  }

  /**
   * Reactivate a soft-deleted team.
   *
   * @param request the team creation request
   */
  public void reactivateTeam(CreateTeamRequest request) {
    teamMapper.reactivateTeam(teamConverter.createRequestToEntity(request));
  }

  /**
   * Search for teams based on criteria, sorting, pagination.
   *
   * @param criteria the search criteria
   * @param sorts the sorting options
   * @param limit the maximum number of records to return
   * @param offset the starting point for records to return
   * @return list of SearchTeamDto matching the search criteria
   */
  public List<SearchTeamDto> search(
      SearchTeamRequest criteria, List<SortRequest> sorts, int limit, int offset) {
    return teamConverter.entitiesToSearchDtos(teamMapper.search(criteria, sorts, limit, offset));
  }

  /**
   * Count teams based on search criteria.
   *
   * @param criteria the search criteria
   * @return the total count of teams matching the criteria
   */
  public int count(SearchTeamRequest criteria) {
    return teamMapper.count(criteria);
  }
}
