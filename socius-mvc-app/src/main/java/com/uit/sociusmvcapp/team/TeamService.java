package com.uit.sociusmvcapp.team;

import com.uit.sociusmvcapp.shared.request.PaginationSearchRequest;
import com.uit.sociusmvcapp.shared.response.PageResponse;
import com.uit.sociusmvcapp.team.dto.SearchTeamDto;
import com.uit.sociusmvcapp.team.dto.TeamDto;
import com.uit.sociusmvcapp.team.dto.request.CreateTeamRequest;
import com.uit.sociusmvcapp.team.dto.request.SearchTeamRequest;
import com.uit.sociusmvcapp.team.dto.request.UpdateTeamRequest;

/** Service interface for Team-related operations. */
public interface TeamService {

  /**
   * Create a new team.
   *
   * @param request the request containing team creation details
   */
  void create(CreateTeamRequest request);

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
  TeamDto update(String teamCode, UpdateTeamRequest request);

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

  /**
   * Search for teams based on given criteria with pagination.
   *
   * @param request the pagination search request containing search criteria
   * @return a paginated response of SearchTeamDto matching the search criteria
   */
  PageResponse<SearchTeamDto> search(PaginationSearchRequest<SearchTeamRequest> request);
}
