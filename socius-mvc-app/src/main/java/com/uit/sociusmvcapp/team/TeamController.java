package com.uit.sociusmvcapp.team;

import com.uit.sociusmvcapp.shared.constants.MessageConstant;
import com.uit.sociusmvcapp.shared.request.PaginationSearchRequest;
import com.uit.sociusmvcapp.shared.response.PageResponse;
import com.uit.sociusmvcapp.shared.response.Response;
import com.uit.sociusmvcapp.shared.service.I18nService;
import com.uit.sociusmvcapp.team.dto.SearchTeamDto;
import com.uit.sociusmvcapp.team.dto.TeamDto;
import com.uit.sociusmvcapp.team.dto.request.CreateTeamRequest;
import com.uit.sociusmvcapp.team.dto.request.SearchTeamRequest;
import com.uit.sociusmvcapp.team.dto.request.UpdateTeamRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
  public ResponseEntity<Response> create(@Valid @RequestBody CreateTeamRequest request) {
    teamService.create(request);
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
   * Get team by team code.
   *
   * @param teamCode the team code
   * @return ResponseEntity containing the team
   */
  @GetMapping("/{teamCode}")
  public ResponseEntity<Response> getTeamByTeamCode(@PathVariable String teamCode) {
    TeamDto team = teamService.findByTeamCode(teamCode);
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
  @PutMapping("/{teamCode}")
  public ResponseEntity<Response> updateTeam(
      @PathVariable String teamCode, @Valid @RequestBody UpdateTeamRequest request) {
    TeamDto team = teamService.update(teamCode, request);
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
    teamService.delete(teamCode);
    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code(MessageConstant.S_TEAM_005)
            .message(i18nService.getMessage(MessageConstant.S_TEAM_005))
            .build();
    return ResponseEntity.ok(response);
  }

  /**
   * Search for teams with pagination.
   *
   * @param request the pagination search request containing search criteria
   * @return ResponseEntity containing paginated team data
   */
  @PostMapping("/search")
  public ResponseEntity<Response> search(
      @RequestBody PaginationSearchRequest<SearchTeamRequest> request) {
    PageResponse<SearchTeamDto> teams = teamService.search(request);
    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code(MessageConstant.S_TEAM_006)
            .message(i18nService.getMessage(MessageConstant.S_TEAM_006))
            .data(teams)
            .build();
    return ResponseEntity.ok(response);
  }
}
