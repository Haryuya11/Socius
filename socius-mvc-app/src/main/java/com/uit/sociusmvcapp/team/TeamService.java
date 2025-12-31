package com.uit.sociusmvcapp.team;

import com.uit.sociusmvcapp.team.dto.TeamDto;
import com.uit.sociusmvcapp.team.dto.request.TeamCreateRequest;
import com.uit.sociusmvcapp.team.dto.request.TeamUpdateRequest;

/** Service interface for Team-related operations. */
public interface TeamService {

  /**
   * Create a new team.
   *
   * @param request the request containing team creation details
   */
  void create(TeamCreateRequest request);

  /**
   * Get team by team code.
   *
   * @param teamCode the team code
   * @return the TeamDto
   */
  TeamDto findByTeamCode(String teamCode);

  /**
   * Update team information.
   *
   * @param teamCode the team code
   * @param request the request containing update details
   * @return the updated TeamDto
   */
  TeamDto update(String teamCode, TeamUpdateRequest request);

  /**
   * Soft delete a team.
   *
   * @param teamCode the team code
   */
  void delete(String teamCode);

  /**
   * Validate if a team exists by its code.
   *
   * @param teamCode the code of the team
   */
  void validateExists(String teamCode);
}
