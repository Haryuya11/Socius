package com.uit.sociusmvcapp.endpoint;

import com.uit.sociuscoremodules.shared.constants.MessageConstant;
import com.uit.sociuscoremodules.shared.request.PaginationSearchRequest;
import com.uit.sociuscoremodules.shared.response.PageResponse;
import com.uit.sociuscoremodules.shared.response.Response;
import com.uit.sociuscoremodules.shared.service.I18nService;
import com.uit.sociuscoremodules.team.dto.SearchTeamDto;
import com.uit.sociuscoremodules.team.dto.TeamDto;
import com.uit.sociuscoremodules.team.request.TeamCreateRequest;
import com.uit.sociuscoremodules.team.request.TeamSearchRequest;
import com.uit.sociuscoremodules.team.request.TeamUpdateRequest;
import com.uit.sociuscoremodules.team.service.TeamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** TeamController handles HTTP requests related to team operations. */
@RestController
@RequiredArgsConstructor
@RequestMapping("/teams")
public class TeamController {

  private final I18nService i18nService;
  private final TeamService teamService;

  /**
   * Create a new team.
   *
   * @param request the team creation request
   * @return ResponseEntity containing the created team
   */
  @PostMapping
  public ResponseEntity<Response> createTeam(@Valid @RequestBody TeamCreateRequest request) {
    teamService.createTeam(request);
    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.CREATED.value())
            .code(MessageConstant.S_TEAM_001)
            .message(i18nService.getMessage(MessageConstant.S_TEAM_001))
            .build();
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  /**
   * Search teams with pagination and sorting.
   *
   * <p>GET /api/teams
   *
   * @param request the search request containing condition, pagination, and sorting
   * @return ResponseEntity containing the list of teams and pagination info
   */
  @PostMapping("/search")
  public ResponseEntity<Response> searchTeams(
      @RequestBody PaginationSearchRequest<TeamSearchRequest> request) {

    PageResponse<SearchTeamDto> result = teamService.searchTeams(request);

    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code(MessageConstant.S_TEAM_003)
            .message(i18nService.getMessage(MessageConstant.S_TEAM_003))
            .data(result)
            .build();
    return ResponseEntity.ok(response);
  }

  /**
   * Get team by team code.
   *
   * @param teamCode the team code
   * @return ResponseEntity containing the team
   */
  @GetMapping("/{teamCode}")
  public ResponseEntity<Response> getTeamByTeamCode(@PathVariable String teamCode) {
    TeamDto team = teamService.getTeamByTeamCode(teamCode);
    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code(MessageConstant.S_TEAM_003)
            .message(i18nService.getMessage(MessageConstant.S_TEAM_003))
            .data(team)
            .build();
    return ResponseEntity.ok(response);
  }

  /**
   * Update team information.
   *
   * @param teamCode the team code
   * @param request the team update request
   * @return ResponseEntity containing the updated team
   */
  @PatchMapping("/{teamCode}")
  public ResponseEntity<Response> updateTeam(
      @PathVariable String teamCode, @Valid @RequestBody TeamUpdateRequest request) {
    TeamDto team = teamService.updateTeam(teamCode, request);
    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code(MessageConstant.S_TEAM_004)
            .message(i18nService.getMessage(MessageConstant.S_TEAM_004))
            .data(team)
            .build();
    return ResponseEntity.ok(response);
  }

  /**
   * Soft delete a team.
   *
   * @param teamCode the team code
   * @return ResponseEntity indicating the result
   */
  @DeleteMapping("/{teamCode}")
  public ResponseEntity<Response> deleteTeam(@PathVariable String teamCode) {
    teamService.deleteTeam(teamCode);
    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code(MessageConstant.S_TEAM_005)
            .message(i18nService.getMessage(MessageConstant.S_TEAM_005))
            .build();
    return ResponseEntity.ok(response);
  }
}
