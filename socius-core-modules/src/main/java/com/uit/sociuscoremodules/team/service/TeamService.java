package com.uit.sociuscoremodules.team.service;

import com.uit.sociuscoremodules.shared.request.PaginationSearchRequest;
import com.uit.sociuscoremodules.shared.response.PageResponse;
import com.uit.sociuscoremodules.team.dto.SearchTeamDto;
import com.uit.sociuscoremodules.team.dto.TeamDto;
import com.uit.sociuscoremodules.team.request.TeamCreateRequest;
import com.uit.sociuscoremodules.team.request.TeamSearchRequest;
import com.uit.sociuscoremodules.team.request.TeamUpdateRequest;

/** Service interface for Team-related operations. */
public interface TeamService {

  /**
   * Create a new team with a team lead.
   *
   * @param request the request containing team creation details
   * @return the created TeamDto
   */
  TeamDto createTeam(TeamCreateRequest request);

  /**
   * Get team by ID.
   *
   * @param id the team ID
   * @return the TeamDto
   */
  TeamDto getTeamById(Integer id);

  /**
   * Get team by team code.
   *
   * @param teamCode the team code
   * @return the TeamDto
   */
  TeamDto getTeamByTeamCode(String teamCode);

  /**
   * Search teams with pagination and sorting.
   *
   * @param request the search request with condition, pagination, and sorting
   * @return PageResponse containing list of SearchTeamDto
   */
  PageResponse<SearchTeamDto> searchTeams(PaginationSearchRequest<TeamSearchRequest> request);

  /**
   * Update team information.
   *
   * @param teamCode the team code
   * @param request the request containing update details
   * @return the updated TeamDto
   */
  TeamDto updateTeam(String teamCode, TeamUpdateRequest request);

  /**
   * Soft delete a team.
   *
   * @param teamCode the team code
   */
  void deleteTeam(String teamCode);
}
