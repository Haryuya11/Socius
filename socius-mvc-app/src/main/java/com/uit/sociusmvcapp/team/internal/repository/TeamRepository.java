package com.uit.sociusmvcapp.team.internal.repository;

import com.uit.sociusmvcapp.team.dto.TeamDto;
import com.uit.sociusmvcapp.team.dto.request.TeamCreateRequest;
import com.uit.sociusmvcapp.team.dto.request.TeamUpdateRequest;
import com.uit.sociusmvcapp.team.internal.converter.TeamConverter;
import com.uit.sociusmvcapp.team.internal.domain.Team;
import com.uit.sociusmvcapp.team.internal.persistence.TeamMapper;
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
  public boolean existsByTeamCode(String teamCode) {
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
}
